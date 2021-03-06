package com.ayoza.camera_sputnik.camerasputnik.storage.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class handles with database creation and population
 * Created by raul on 3/02/15.
 */
public class ConfigurationHelper extends SQLiteOpenHelper {

    public static final String TABLE_BLUETOOTH_DEVICE = "bluetooth_device";
    public static final String BLUETOOTH_DEVICE_ID = "_id";
    public static final String BLUETOOTH_DEVICE_NAME = "name";
    public static final String BLUETOOTH_DEVICE_MAC = "mac";
    public static final String BLUETOOTH_DEVICE_PAIRED = "paired";
    
    public static final String TABLE_IMAGES_DOWNLOADED = "image_downloaded";
    public static final String IMAGES_DOWNLOADED_ID = "_id";
    public static final String IMAGES_DOWNLOADED_FILENAME = "filename";
    public static final String IMAGES_DOWNLOADED_DATE = "created_date";
    
    public static final String TABLE_TRACK = "track";
    public static final String TRACK_ID = "_id";
    public static final String TRACK_DATE = "created_date";
    
    public static final String TABLE_POINT = "point";
    public static final String POINT_ID = "_id";
    public static final String POINT_LATITUDE = "latitude";
    public static final String POINT_LONGITUDE = "longitude";
    public static final String POINT_DATE = "created_date";
    public static final String POINT_IMAGES_DOWNLOADED_ID = "id_image";
    public static final String POINT_TRACK_ID = "id_track";

    // Table bluetooth device creation sql statement
    private static final String TABLE_BLUETOOTH_DEVICE_CREATE = "create table "
            + TABLE_BLUETOOTH_DEVICE + "(" + BLUETOOTH_DEVICE_ID
            + " integer primary key autoincrement, " + BLUETOOTH_DEVICE_NAME
            + " text not null, " + BLUETOOTH_DEVICE_MAC + " text not null, "
            +  BLUETOOTH_DEVICE_PAIRED + " integer not null);";

    // Table images downloaded creation sql statement
    private static final String TABLE_IMAGES_DOWNLOADED_CREATE = "create table "
            + TABLE_IMAGES_DOWNLOADED + "(" + IMAGES_DOWNLOADED_ID
            + " integer primary key autoincrement, " + IMAGES_DOWNLOADED_FILENAME
            + " text not null, " + IMAGES_DOWNLOADED_DATE + " datetime default CURRENT_TIMESTAMP);";
    
    // Populate images downloaded Table
    private static final String TABLE_IMAGES_DOWNLOADED_POPULATE = "insert into "
            + TABLE_IMAGES_DOWNLOADED + " (" + IMAGES_DOWNLOADED_ID +"," + IMAGES_DOWNLOADED_FILENAME
            + ") values(0, '0.jpg')";
    
    // Create table track
    private static final String TABLE_TRACK_CREATE = "create table "
            + TABLE_TRACK + " (" + TRACK_ID + " integer primary key autoincrement," + TRACK_DATE
            + " datetime default CURRENT_TIMESTAMP);"
            ;
    
    // Create table Point
    private static final String TABLE_POINT_CREATE = "create table " +
            TABLE_POINT + " (" + POINT_ID + " integer primary key autoincrement, " +
            POINT_LATITUDE + " real, " + POINT_LONGITUDE + " real," +
            POINT_DATE + " datetime default CURRENT_TIMESTAMP, " +
            POINT_IMAGES_DOWNLOADED_ID + " integer," +
            POINT_TRACK_ID + " integer," +
            "  FOREIGN KEY(" + POINT_IMAGES_DOWNLOADED_ID + 
            ") REFERENCES " + TABLE_IMAGES_DOWNLOADED + 
            "(" + IMAGES_DOWNLOADED_ID + ")," +
            "  FOREIGN KEY(" + POINT_TRACK_ID +
            ") REFERENCES " + TABLE_TRACK +
            "(" + TRACK_ID + "))" +
            ";"
            ;
    
    public ConfigurationHelper(Context context) {
        super(context, GeneralHelper.DATABASE_NAME, null, GeneralHelper.DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Schema creation
        db.execSQL(TABLE_BLUETOOTH_DEVICE_CREATE);
        db.execSQL(TABLE_IMAGES_DOWNLOADED_CREATE);
        db.execSQL(TABLE_TRACK_CREATE);
        db.execSQL(TABLE_POINT_CREATE);
        
        // Populate
        db.execSQL(TABLE_IMAGES_DOWNLOADED_POPULATE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(ConfigurationHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BLUETOOTH_DEVICE);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_POINT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES_DOWNLOADED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
        
        onCreate(db);
    }
}
