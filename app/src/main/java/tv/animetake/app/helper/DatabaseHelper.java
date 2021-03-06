package tv.animetake.app.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import tv.animetake.app.model.Anime;
import tv.animetake.app.model.Episode;
import tv.animetake.app.model.Historic;

/**
 * Created by mauricio on 02/08/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 7;

    // Database Name
    private static final String DATABASE_NAME = "Animetake";
    private static DatabaseHelper databaseHelper;

    public static DatabaseHelper getInstance(Context ctx) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(ctx.getApplicationContext());
        }

        return databaseHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Anime.createQuery());
        sqLiteDatabase.execSQL(Episode.createQuery());
        sqLiteDatabase.execSQL(Historic.createQuery());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // TODO
    }
}
