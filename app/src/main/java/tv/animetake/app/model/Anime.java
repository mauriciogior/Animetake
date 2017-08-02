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
 * Created by mauricio on 02/08/17.
 */

public class Anime extends Base implements Parcelable {
    public static final String TABLE_NAME = "anime";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_URL = "url";
    public static final String KEY_THUMBNAIL = "thumbnail";

    public static final String[] COLUMNS = {KEY_ID,KEY_TITLE,KEY_DESCRIPTION,KEY_URL,KEY_THUMBNAIL};

    private int id;
    private String title;
    private String description;
    private String url;
    private String thumbnail;

    public Anime() {
    }

    public Anime(Parcel in){
        String[] data = new String[5];
        in.readStringArray(data);

        this.id          = Integer.parseInt(data[0]);
        this.title       = data[1];
        this.description = data[2];
        this.url         = data[3];
        this.thumbnail   = data[4];
    }

    public Anime(int id, String title, String description, String url, String thumbnail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.url = url;
        this.thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return "https://animetake.tv" + thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String toString() {
        return "Anime [id=" + id + ", title=" + title + ", description=" + description
                + "]";
    }

    @Override
    public String createQueryAbs() {
        return "CREATE TABLE " + TABLE_NAME + "( " +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "description TEXT, " +
            "url TEXT, " +
            "thumbnail TEXT)";
    }

    @Override
    public String dropQueryAbs() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String createQuery() {
        return new Anime().createQueryAbs();
    }

    public static String dropQuery() {
        return new Anime().dropQueryAbs();
    }

    public void saveAnime(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (searchEq(context, getTitle()).size() > 0) {
            // Update anime
            Log.d("Anime", "Update");
        } else {
            // Insert to db
            Log.d("Anime", "Insert");
            insert(db);
        }

        // 4. close
        db.close();
    }

    public static List<Anime> getAll(Context context) {
        return searchQuery(context, null, null);
    }

    public static List<Anime> searchEq(Context context, String title) {
        return searchQuery(context, KEY_TITLE + " = ? ", new String[] { title });
    }

    public static List<Anime> searchLike(Context context, String title) {
        return searchQuery(context, KEY_TITLE + " like '%?%' ", new String[] { title });
    }

    private static List<Anime> searchQuery(Context context, String selection, String[] values) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Anime> animeList = new ArrayList<>();

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

    private void insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("url", url);
        values.put("thumbnail", thumbnail);

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
            String.valueOf(id), title, description, url, thumbnail
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Anime createFromParcel(Parcel in) {
            return new Anime(in);
        }

        public Anime[] newArray(int size) {
            return new Anime[size];
        }
    };
}
