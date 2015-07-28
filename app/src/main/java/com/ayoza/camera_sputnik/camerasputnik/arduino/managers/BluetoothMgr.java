package com.ayoza.camera_sputnik.camerasputnik.arduino.managers;

/**
 * Created by raul on 18/01/15.
 * Manages bluetooth communications
 */

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.util.Log;

import com.ayoza.camera_sputnik.camerasputnik.activities.BluetoothDevicesListActivity;
import com.ayoza.camera_sputnik.camerasputnik.activities.DownloadingImageActivity;
import com.ayoza.camera_sputnik.camerasputnik.activities.LoadingActivity;
import com.ayoza.camera_sputnik.camerasputnik.activities.MainActivity;
import com.ayoza.camera_sputnik.camerasputnik.activities.ScanningDevicesActivity;
import com.ayoza.camera_sputnik.camerasputnik.exceptions.ImageException;
import com.ayoza.camera_sputnik.camerasputnik.exceptions.TrackException;
import com.ayoza.camera_sputnik.camerasputnik.interfaces.OnBackgroundListener;
import com.ayoza.camera_sputnik.camerasputnik.storage.entities.BDeviceSputnik;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is responsible for creating and managing connections between Arduino and Android
 *
 */
public final class BluetoothMgr {

    private static BluetoothMgr instance = null;

    public static final int REQUEST_ENABLE_BT = 6666;
    private static final String UUID_HC06 = "00001101-0000-1000-8000-00805f9b34fb";
    //private static final String UUID_HC06 = "00000000-0000-1000-8000-00805f9b34fb";
    //00001101-0000-1000-8000-00805f9b34fb
    private static final String MAGIC_NUMBER = "FRK14U0JKMTY71";
    private static final String CONNECTION_REQUEST = "CAMERABIKE";
    private static final long TIMEOUT_READ_MS = 10000;
    private static final int HEADER_LENGTH = 9;
    private static final char HEADER_FILL_CHARACTER = 'X';
    private static final String OK_RESPONSE = "OK";
    private static final String OK1_RESPONSE = "OK1";
    private static final int MAX_EXPECTED_DATA = 10;

    private final BroadcastReceiver mReceiver;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket bs = null;
    private Boolean isBluetoothEnabled = null;
    private ConfigurationMgr configurationMgr = null;
    private ImageMgr imageMgr = null;
    private TrackMgr trackMgr = null;
    private static Activity activity;
    
    private Boolean connected = false;
    private List<BluetoothDevice> listDevicesFound;
    private BluetoothDevice deviceConnected = null;
    private Boolean pairedDeviceExists = false;
    
    //private

    private BluetoothMgr() {

        final BDeviceSputnik bDeviceSputnik;

        if (activity != null) {
            configurationMgr = ConfigurationMgr.getInstance(activity);
            bDeviceSputnik = configurationMgr.getPairedBluetoothDevice();

            if (bDeviceSputnik != null) {
                pairedDeviceExists = true;
            }

            trackMgr = TrackMgr.getInstance(activity);
        } else {
            bDeviceSputnik = null;
        }
        
        listDevicesFound = new ArrayList<BluetoothDevice>();

        mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BDeviceSputnik object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView

                    // Store bluetooth device to be used later
                    listDevicesFound.add(device);

                    if (bDeviceSputnik != null) {
                        String name = bDeviceSputnik.getName();

                        if (device.getName().equals(name)) {
                            mBluetoothAdapter.cancelDiscovery();
                            connect(device, activity);
                        }
                    }

                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.d(BluetoothMgr.class.getSimpleName(), "Discovery finished");

                    if (ScanningDevicesActivity.getmHandlerStatic() != null) {
                        Message msgScanningDevices = new Message();
                        msgScanningDevices.obj = null;
                        msgScanningDevices.what = ScanningDevicesActivity.SCANNING_ENDED;
                        ScanningDevicesActivity.getmHandlerStatic().sendMessage(msgScanningDevices);
                    }
                    
                    
                    // Sends connection status to main thread
                    Message msg = new Message();
                    msg.obj = new Boolean(connected);
                    msg.what = MainActivity.CONNECTION_STATUS_MSG;

                    if (MainActivity.getmHandlerStatic() != null) {
                        MainActivity.getmHandlerStatic().sendMessage(msg);
                    }
                }
            }
        };

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            throw new RuntimeException("No device bluetooth was found");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            isBluetoothEnabled = false;
        } else {
            isBluetoothEnabled = true;
        }

        // FIXME creo que aquí debería de poner un if con isBluetoothEnabled para no lanzar
        // startDiscovery cuando el Bluetooth no está habilitado
        boolean ret = mBluetoothAdapter.startDiscovery();
        if (ret == true) {
            Log.d(BluetoothMgr.class.getSimpleName(), "Discovery started");
            Log.d(BluetoothMgr.class.getSimpleName(), "Starting ScanningDevicesActivity");
            Intent intent = new Intent(activity, ScanningDevicesActivity.class);
            activity.startActivityForResult(intent, 1);
            Log.d(BluetoothMgr.class.getSimpleName(), "ScanningDevicesActivity started");
        } else {
            Log.d(BluetoothMgr.class.getSimpleName(), "Discovery could not start");
        }
    }
    
    public void connect(final BluetoothDevice device, final Activity activityHost) {
        
        // TODO build a thread here and block this method while a connection is being established
        
        // Launch hear LoadingActivity and put next stub into onBackgroundListener of LoadingActivity
        LoadingActivity.setOnBackgroundListener(new OnBackgroundListener() {
            @Override
            public boolean onBackground() {

                if (device != null) {
                    try {

                        Log.d(BluetoothMgr.class.getName(), device.getName() == null ? "" : device.getName());
                        //Log.d(BluetoothMgr.class.getName(), String.valueOf(device.setPin(pin)));
                        bs = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_HC06));
                        //bs = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUID_HC06));
                        bs.connect();
        
                        // Sends CONNECTION_REQUEST to wake up camera bike device
                        OutputStream outputStream = bs.getOutputStream();
                        outputStream.write(CONNECTION_REQUEST.getBytes());
                        outputStream.flush();
                        /*try {
                            Thread.sleep(500);                 //500 milliseconds
                        } catch(InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }*/
                        
                        // Magic number is expected to be received to confirm this is a camera sputnik device
                        InputStream is = bs.getInputStream();
                        byte[] buffer = new byte[MAGIC_NUMBER.length()];
                        int lengthReadBytes = 0;
                        long initialTime = System.currentTimeMillis();
                        long currentTime = initialTime;
                        StringBuilder sb = new StringBuilder();
                        int totalReadBytes = 0;

                        while(totalReadBytes != MAGIC_NUMBER.length() && (currentTime - initialTime) < TIMEOUT_READ_MS) {
                            if( is.available() > 0 ) {
                                lengthReadBytes = is.read(buffer, 0, MAGIC_NUMBER.length());
                                totalReadBytes += lengthReadBytes;
                                sb.append(new String(buffer, 0, lengthReadBytes));
                            }

                            currentTime = System.currentTimeMillis();
                        }

                        if (sb.toString().length() > 0) {
                            Log.d(BluetoothMgr.class.getSimpleName(), "totalReadBytes: "+ totalReadBytes + ", Buffer received: " + sb.toString());
                        }
                        
                        if (sb.toString().length() > 0 && sb.toString().equals(MAGIC_NUMBER)) {

                            // Sends OK Response to confirm magic number was received
                            outputStream.write(OK_RESPONSE.getBytes());
                            outputStream.flush();
                            Log.d(this.getClass().getSimpleName(), "OK response sent");
                            
                            connected = true;
                            deviceConnected = device;
                            if (!pairedDeviceExists) {
                                // insert device in DB if there is no device still inserted
                                BDeviceSputnik bDeviceSputnik = new BDeviceSputnik();
                                bDeviceSputnik.setName(device.getName() == null ? device.getAddress() : device.getName());
                                bDeviceSputnik.setMac(device.getAddress());
                                bDeviceSputnik.setPaired(true);
                                configurationMgr.insertPairedBluetoothDevice(bDeviceSputnik);
                            }
                            // bs.close();
                            // bs = null;
                        } else {
                            // TODO inform device incompatible
                            connected = false;
                            bs.close();
                            bs = null;
                        }

                        is.close();
                        outputStream.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Sends connection status to main thread
                Message msg = new Message();
                msg.obj = new Boolean(connected);
                msg.what = MainActivity.CONNECTION_STATUS_MSG;
                
                if (MainActivity.getmHandlerStatic() != null) {
                    MainActivity.getmHandlerStatic().sendMessage(msg);
                }

                return connected;
            }
        });

        Log.d(BluetoothMgr.class.getSimpleName(), "Starting loading activity");
        Intent intent = new Intent(activityHost, LoadingActivity.class);
        activityHost.startActivityForResult(intent, BluetoothDevicesListActivity.LOADING_CONNECTING_ACTIVITY);
        Log.d(BluetoothMgr.class.getSimpleName(), "Loading activity started");
    }

    /**
     * This method should be called only after calling void connect(final BluetoothDevice device, final Activity activityHost)
     * in order to be sure that a first attempt of connection has been performed hence the variable
     * deviceConnected should be valid
     */
    public void connect() {

        if (deviceConnected != null) {
            try {
                bs = deviceConnected.createRfcommSocketToServiceRecord(UUID.fromString(UUID_HC06));
                bs.connect();

                // Sends CONNECTION_REQUEST to wake up camera bike device
                OutputStream outputStream = bs.getOutputStream();
                outputStream.write(CONNECTION_REQUEST.getBytes());
                outputStream.flush();

                // Magic number is expected to be received to confirm this is a camera sputnik device
                InputStream is = bs.getInputStream();
                byte[] buffer = new byte[MAGIC_NUMBER.length()];
                int lengthReadBytes = 0;
                long initialTime = System.currentTimeMillis();
                long currentTime = initialTime;
                StringBuilder sb = new StringBuilder();
                int totalReadBytes = 0;

                while(totalReadBytes != MAGIC_NUMBER.length() && (currentTime - initialTime) < TIMEOUT_READ_MS) {
                    if( is.available() > 0 ) {
                        lengthReadBytes = is.read(buffer, 0, MAGIC_NUMBER.length());
                        totalReadBytes += lengthReadBytes;
                        sb.append(new String(buffer, 0, lengthReadBytes));
                    }

                    currentTime = System.currentTimeMillis();
                }

                if (sb.toString().length() > 0) {
                    Log.d(BluetoothMgr.class.getSimpleName(), "totalReadBytes: "+ totalReadBytes + ", Buffer received: " + sb.toString());
                }

                if (sb.toString().length() > 0 && sb.toString().equals(MAGIC_NUMBER)) {
                    // Sends OK Response to confirm magic number was received
                    outputStream.write(OK_RESPONSE.getBytes());
                    outputStream.flush();
                    Log.d(this.getClass().getSimpleName(), "OK response sent");

                    connected = true;

                    if (!pairedDeviceExists) {
                        // insert device in DB if there is no device still inserted
                        BDeviceSputnik bDeviceSputnik = new BDeviceSputnik();
                        bDeviceSputnik.setName(deviceConnected.getName() == null ? deviceConnected.getAddress() : deviceConnected.getName());
                        bDeviceSputnik.setMac(deviceConnected.getAddress());
                        bDeviceSputnik.setPaired(true);
                        configurationMgr.insertPairedBluetoothDevice(bDeviceSputnik);
                    }
                    // bs.close();
                    // bs = null;
                } else {
                    // TODO inform device incompatible
                    connected = false;
                    bs.close();
                    bs = null;
                }

                is.close();
                outputStream.close();

            } catch (IOException e) {

            }
        }
        
    }
    
    public void disconnect() {
        try {
            if (bs != null) {
                bs.close();
            }
        } catch (IOException closeException) { 
        
        } finally {
            bs = null;
        }
        
    }
    
    public boolean receiveImage(DownloadingImageActivity downloadingImageActivity) {
        if (connected && trackMgr.isThereCurrentTrack()) {
            InputStream is = null;
            OutputStream outputStream = null;
            try {
                bs = deviceConnected.createRfcommSocketToServiceRecord(UUID.fromString(UUID_HC06));
                bs.connect();

                // send an 'OK' response to camera sputnik device in order to start
                // image download
                outputStream = bs.getOutputStream();
                outputStream.write(OK_RESPONSE.getBytes());
                outputStream.flush();
                
                // Image length is expected
                is = bs.getInputStream();
                byte[] header = new byte[HEADER_LENGTH];
                
                int lengthReadBytes = 0;
                long initialTime = System.currentTimeMillis();
                long currentTime = initialTime;
                StringBuilder sb = new StringBuilder();
                int totalReadBytes = 0;
                
                while (totalReadBytes != HEADER_LENGTH && (currentTime - initialTime) < TIMEOUT_READ_MS) {
                    if (is.available() > 0) {
                        lengthReadBytes = is.read(header, 0, HEADER_LENGTH);
                        totalReadBytes += lengthReadBytes;
                        sb.append(new String(header, 0, lengthReadBytes));
                    }

                    currentTime = System.currentTimeMillis();
                }

                if (sb.toString().length() != HEADER_LENGTH) {
                    return false;
                }

                Log.d(BluetoothMgr.class.getSimpleName(), "Header received: " + sb.toString());

                int index = sb.toString().indexOf(HEADER_FILL_CHARACTER);
                int length = -1;
                if (index != -1) {
                    String lengthStr = sb.toString().substring(0, index);

                    try {
                        length = Integer.valueOf(lengthStr);
                    } catch(NumberFormatException nfe) { }
                }

                if (length == -1) {
                    return false;
                }

                Log.d(BluetoothMgr.class.getSimpleName(), "Length received: " + length);
                // send an 'OK' message to camera sputnik device
                outputStream = bs.getOutputStream();
                outputStream.write(OK1_RESPONSE.getBytes());
                outputStream.flush();
                try {
                    Thread.sleep(500);                 //500 milliseconds
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                imageMgr = ImageMgr.getInstance(activity);
                imageMgr.startNewImage(length);

                byte[] buffer = new byte[MAX_EXPECTED_DATA];
                int i = 0;
                initialTime = System.currentTimeMillis();
                currentTime = initialTime;
                while (i < length && (currentTime - initialTime) < TIMEOUT_READ_MS) {
                    if (is.available() > 0) {
                        lengthReadBytes = is.read(buffer, 0, MAX_EXPECTED_DATA);
                       
                        imageMgr.storeBytes(buffer, lengthReadBytes);
                        
                        i += lengthReadBytes;
                        // Publish progress
                        downloadingImageActivity.publishDownloadProgress(i, length);
                        initialTime = currentTime;
                    }

                    currentTime = System.currentTimeMillis();
                }
                
                if (i < length) {
                    imageMgr.deleteCurrentImage();
                    return false;
                }

                // send an 'OK' message to camera sputnik device
                outputStream = bs.getOutputStream();
                outputStream.write(OK_RESPONSE.getBytes());
                outputStream.flush();
                try {
                    Thread.sleep(500);                 //500 milliseconds
                } catch(InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }

                Long imageId = imageMgr.closeCurrentImage();
                
                try {
                    // FIXME add GPS location here. Presently it is hardcoded with 0.0, 0.0
                    trackMgr.addPointToCurrentTrack(imageId, 0.0, 0.0);
                } catch (TrackException te) {
                    // TODO inform with toast that no current track is available
                }
                
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            } catch (ImageException e) {
                switch (e.getError()) {
                    case ImageException.EXTERNAL_STORAGE_NOT_AVAILABLE:
                        // TODO inform with toast that external storage is not available
                        break;
                    case ImageException.DIRECTORY_COULD_NOT_BE_CREATED:
                        // TODO inform with toast that directory could not be created
                        break;
                    case ImageException.NOT_ENOUGH_FREE_SPACE:
                        // TODO inform with toast that there is not enough free space
                        break;
                    case ImageException.ANOTHER_IMAGE_IS_BEEING_PROCESSED:
                        // TODO inform with toast that currently another image is being processed
                        break;
                    case ImageException.COULD_NOT_PARSE_IMAGE_NAME:
                        // TODO inform with toast that retrieved filename from DB is malformed
                        break;
                    case ImageException.IMAGE_FILE_COULD_NOT_BE_CREATED:
                        // TODO inform with toast that there was an error creating image file
                        break;
                    case ImageException.PROBLEM_WRITING_IMAGE:
                        // TODO inform with toast that there was an error writing image file
                        break;
                    case ImageException.NO_CURRENT_IMAGE_OPENED:
                        // TODO inform with toast that no current image is opened
                        break;
                    case ImageException.PROBLEM_CLOSING_IMAGE_STREAM:
                        // TODO inform with toast that there was a problem closing image stream
                        break;
                    case ImageException.PROBLEM_REMOVING_IMAGE:
                        // TODO inform with toast that there was a problem removing image
                        break;
                }
                
                e.printStackTrace();
                
                return false;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ioe2) {
                        // FIXME This could happen when the image has already been downloaded
                        ioe2.printStackTrace();
                        return false;
                    }
                }

                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException ioe2) {
                        // FIXME It could happen when the image has already been downloaded
                        ioe2.printStackTrace();
                        return false;
                    }
                }
            }

        } else {
            return false;
        }

        return true;
    }

    public List<BluetoothDevice> getListDevicesFound() {
        return listDevicesFound;
    }


    public Boolean getConnected() {
        return connected;
    }

    public static synchronized BluetoothMgr getInstance(Activity activity) {
        if (instance == null) {
            BluetoothMgr.activity = activity;
            instance = new BluetoothMgr();
        }

        return instance;
    }

    public static BluetoothMgr getInstance() {
        return getInstance(null);
    }

    public Boolean isBluetoothEnabled() {
        return isBluetoothEnabled;
    }

    public void registerReceiver(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mReceiver, filter);
    }

    public void unRegisterReceiver(Activity activity) {
        activity.unregisterReceiver(mReceiver);
    }

    public void showBluetoothTurnOnRequest(Activity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    public boolean startDiscovery() {
        // reset list of devices found previously
        listDevicesFound.clear();
        return mBluetoothAdapter.startDiscovery();
    }

    public void close() {
        if (bs != null) {
            try {
                bs.close();
            } catch (IOException e) {}
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
