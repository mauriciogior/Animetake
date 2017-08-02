package tv.animetake.app;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import tv.animetake.app.helper.Updater;
import tv.animetake.app.model.Episode;

public class EpisodeActivity extends AppCompatActivity implements EasyVideoCallback {

    private EasyVideoPlayer player;
    private Episode episode;
    private Updater updater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            episode = extras.getParcelable("episode");

        } else if (savedInstanceState != null) {
            episode = savedInstanceState.getParcelable("episode");
        }

        if (episode == null) {
            finish(); return;
        }

        getSupportActionBar().setTitle(episode.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        updater = new Updater(this);

        if (episode.getVideoUrl().isEmpty()) {
            updater.getEpisodeVideoUrl(episode, new Updater.OnUpdated() {
                @Override
                public void updated(Object object) {
                    episode = (Episode) object;
                    updater.release();
                    loadVideo();
                }
            });
        } else {
            updater.release();
            loadVideo();
        }

    }

    private void loadVideo() {
        String[] urls = episode.getVideoUrl().split("\\|");
        String url = "https://animetake.tv" + urls[0];

        // Grabs a reference to the player view
        player = (EasyVideoPlayer) findViewById(R.id.player);

        // Sets the callback to this Activity, since it inherits EasyVideoCallback
        player.setCallback(this);

        // Sets the source to the HTTP URL held in the TEST_URL variable.
        // To play files, you can use Uri.fromFile(new File("..."))
        player.setSource(Uri.parse(url));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) player.pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("episode", episode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {

    }

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {

    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {

    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {

    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {

    }
}
