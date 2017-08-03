package tv.animetake.app.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import tv.animetake.app.helper.DatabaseHelper;

/**
 * Created by mauricio on 03/08/17.
 */

public class Historic  extends Base implements Parcelable {
    public static final String TABLE_NAME = "historic";

    public static final String KEY_ID = "id";
    public static final String KEY_ANIME_ID = "animeId";
    public static final String KEY_DATE = "date";

    public static final String[] COLUMNS = {KEY_ID,KEY_ANIME_ID,KEY_DATE};

    private int id;
    private int animeId;
    private int date;

    public Historic() {
    }

    public Historic(Parcel in){
        String[] data = new String[3];
        in.readStringArray(data);

        this.id      = Integer.parseInt(data[0]);
        this.animeId = Integer.parseInt(data[1]);
        this.date    = Integer.parseInt(data[2]);
    }

    public Historic(int id, int animeId, int date) {
        this.id = id;
        this.animeId = animeId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnimeId() {
        return animeId;
    }

    public void setAnimeId(int animeId) {
        this.animeId = animeId;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Historic [id=" + id + ", animeId=" + animeId + ", date=" + date
                + "]";
    }

    @Override
    public String createQueryAbs() {
        return "CREATE TABLE " + TABLE_NAME + "( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "animeId INTEGER, " +
                "date INTEGER)";
    }

    @Override
    public String dropQueryAbs() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String createQuery() {
        return new Historic().createQueryAbs();
    }

    public static String dropQuery() {
        return new Historic().dropQueryAbs();
    }

    public void saveHistoric(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (searchEq(context, getAnimeId()).size() > 0) {
            // Update historic
            Log.d("Historic", "Update");
        } else {
            // Insert to db
            Log.d("Historic", "Insert");
            insert(db);
        }

        // 4. close
        db.close();
    }

    public static List<Anime> getAll(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Anime> animeList = new ArrayList<>();

        Cursor cursor = db.rawQuery("" +
                "SELECT * FROM anime INNER JOIN historic ON " +
                "historic.animeId = anime.id ORDER BY historic.date DESC", new String[] {});

        // 3. if we got results get the first one
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // 4. build book object
                Anime anime = new Anime();
                anime.setId(Integer.parseInt(cursor.getString(0)));
                anime.setTitle(cursor.getString(1));
                anime.setDescription(cursor.getString(2));
                anime.setUrl(cursor.getString(3));
                anime.setThumbnail(cursor.getString(4));

                animeList.add(anime);
            }
        }

        return animeList;
    }

    public static List<Historic> searchEq(Context context, int animeId) {
        return searchQuery(context, KEY_ANIME_ID + " = ? ", new String[] { String.valueOf(animeId) });
    }

    private static List<Historic> searchQuery(Context context, String selection, String[] values) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Historic> historicList = new ArrayList<>();

        Cursor cursor = db.query(
                TABLE_NAME, // a. table
                COLUMNS, // b. column names
                selection, // c. selections
                values, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null
        ); // h. limit

        // 3. if we got results get the first one
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // 4. build book object
                Historic historic = new Historic();
                historic.setId(Integer.parseInt(cursor.getString(0)));
                historic.setAnimeId(Integer.parseInt(cursor.getString(1)));
                historic.setDate(Integer.parseInt(cursor.getString(2)));

                historicList.add(historic);
            }
        }

        return historicList;
    }

    private void insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("animeId", animeId);
        values.put("date", date);

        // Insert
        db.insert(TABLE_NAME, null, values);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(id), String.valueOf(animeId), String.valueOf(date)
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Historic createFromParcel(Parcel in) {
            return new Historic(in);
        }

        public Historic[] newArray(int size) {
            return new Historic[size];
        }
    };
}
