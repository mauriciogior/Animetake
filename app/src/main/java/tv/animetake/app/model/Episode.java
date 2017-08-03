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

public class Episode extends Base implements Parcelable {
    public static final String TABLE_NAME = "episode";

    public static final String KEY_ID = "id";
    public static final String KEY_ANIME_ID = "animeId";
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";
    public static final String KEY_VIDEO_URL = "videoUrl";
    public static final String KEY_WATCHED = "watched";

    public static final String[] COLUMNS = {KEY_ID,KEY_ANIME_ID,KEY_TITLE, KEY_URL, KEY_VIDEO_URL, KEY_WATCHED};

    private int id;
    private int animeId;
    private String title;
    private String url;
    private String videoUrl;
    private int watched;

    public Episode() {
    }

    public Episode(Parcel in){
        String[] data = new String[6];
        in.readStringArray(data);

        this.id       = Integer.parseInt(data[0]);
        this.animeId  = Integer.parseInt(data[1]);
        this.title    = data[2];
        this.url      = data[3];
        this.videoUrl = data[4];
        this.watched  = Integer.parseInt(data[5]);
    }

    public Episode(int id, int animeId, String title, String url, String videoUrl) {
        this.id = id;
        this.animeId = animeId;
        this.title = title;
        this.url = url;
        this.videoUrl = videoUrl;
        this.watched = 0;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getWatched() {
        return watched;
    }

    public void setWatched(int watched) {
        this.watched = watched;
    }

    @Override
    public String toString() {
        return "Episode [id=" + id + ", title=" + title + "]";
    }

    @Override
    public String createQueryAbs() {
        return "CREATE TABLE " + TABLE_NAME + "( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "animeId INTEGER, " +
                "title TEXT, " +
                "url TEXT, " +
                "videoUrl TEXT, " +
                "watched INTEGER)";
    }

    @Override
    public String dropQueryAbs() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String createQuery() {
        return new Episode().createQueryAbs();
    }

    public static String dropQuery() {
        return new Episode().dropQueryAbs();
    }

    public void saveEpisode(Context context) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (getId() == 0) {
            List<Episode> episodeList = searchEq(context, getAnimeId(), getTitle());
            if (episodeList.size() > 0) {
                setId(episodeList.get(0).getId());

                if (watched != 1) {
                    setWatched(episodeList.get(0).getWatched());
                }
            }
        }

        if (getId() != 0) {
            // Update anime
            Log.d("Episode", "Update " + getId());
            update(db);
        } else {
            // Insert to db
            Log.d("Episode", "Insert");
            insert(db);
        }

        // 4. close
        db.close();
    }

    public static List<Episode> getAll(Context context, Anime anime) {
        return searchQuery(context, KEY_ANIME_ID + " = ? ", new String[] { "" + anime.getId() });
    }

    public static List<Episode> searchEq(Context context, Integer animeId, String title) {
        return searchQuery(context, KEY_TITLE + " = ? AND " + KEY_ANIME_ID + " = " + animeId, new String[] { title });
    }

    public static List<Episode> searchLike(Context context, String title) {
        return searchQuery(context, KEY_TITLE + " like '%?%' ", new String[] { title });
    }

    private static List<Episode> searchQuery(Context context, String selection, String[] values) {
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Episode> episodeList = new ArrayList<>();

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
                Episode episode = new Episode();
                episode.setId(Integer.parseInt(cursor.getString(0)));
                episode.setAnimeId(Integer.parseInt(cursor.getString(1)));
                episode.setTitle(cursor.getString(2));
                episode.setUrl(cursor.getString(3));
                episode.setVideoUrl(cursor.getString(4));
                episode.setWatched(Integer.parseInt(cursor.getString(5)));

                episodeList.add(episode);
            }
        }

        return episodeList;
    }

    private void insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("animeId", animeId);
        values.put("title", title);
        values.put("url", url);
        values.put("videoUrl", videoUrl);
        values.put("watched", watched);

        // Insert
        db.insert(TABLE_NAME, null, values);
    }

    private void update(SQLiteDatabase db) {
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("url", url);
        values.put("videoUrl", videoUrl);
        values.put("watched", watched);

        db.update(TABLE_NAME, //table
            values, // column/value
            KEY_ID + " = ?", // selections
            new String[] { String.valueOf(id) }
        );

        // 4. close
        db.close();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                String.valueOf(id), String.valueOf(animeId), title, url, videoUrl, String.valueOf(watched)
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };
}
