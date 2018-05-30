package es.uji.al259348.sliwandroid.core.repositories.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import es.uji.al259348.sliwandroid.core.model.Sample;

public class SQLiteSliwDatabase extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "Sliw.db";
    private static final int DATABASE_VERSION = 3;

    public SQLiteSliwDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        Log.i("SQLiteSliwDatabase", "Creating database version " + DATABASE_VERSION + " ...");
        try {
            Log.i("SQLiteSliwDatabase", "Creating tables...");
            TableUtils.createTable(connectionSource, Sample.class);
            TableUtils.createTable(connectionSource, Sample.WifiScanResult.class);
            Log.i("SQLiteSliwDatabase", "Database created successfully.");
        } catch (SQLException e) {
            Log.e("SQLiteSliwDatabase", "Can't create table.", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.i("SQLiteSliwDatabase", "Upgrading database version from " + oldVersion + " to " + newVersion + " ...");
        try {
            Log.i("SQLiteSliwDatabase", "Droping tables...");
            TableUtils.dropTable(connectionSource, Sample.WifiScanResult.class, true);
            TableUtils.dropTable(connectionSource, Sample.class, true);
            onCreate(db, connectionSource);
            Log.i("SQLiteSliwDatabase", "Database updated successfully.");
        } catch (SQLException e) {
            Log.e("SQLiteSliwDatabase", "Can't drop table.", e);
            throw new RuntimeException(e);
        }
    }

}
