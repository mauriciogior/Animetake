package tv.animetake.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import tv.animetake.app.adapter.EpisodeListAdapter;
import tv.animetake.app.helper.Updater;
import tv.animetake.app.model.Anime;
import tv.animetake.app.model.Episode;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AnimeActivity extends AppCompatActivity {

    private Anime anime;
    private EpisodeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anime);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            anime = extras.getParcelable("anime");

        } else if (savedInstanceState != null) {
            anime = savedInstanceState.getParcelable("anime");
        }

        if (anime == null) {
            finish(); return;
        }

        getSupportActionBar().setTitle(anime.getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EpisodeListAdapter();
        List<Episode> episodeList = Episode.getAll(this, anime);
        adapter.setDataList(episodeList);

        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new EpisodeListAdapter.OnClickListener() {
            @Override
            public void onClick(Episode episode) {
                Intent intent = new Intent(AnimeActivity.this, EpisodeActivity.class);
                intent.putExtra("episode", episode);
                startActivity(intent);
            }
        });

        if (episodeList.size() == 0) {
            updateAnimeEpisodeList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            List<Episode> episodeList = Episode.getAll(this, anime);
            adapter.setDataList(episodeList);
        }
    }

    private void updateAnimeEpisodeList() {
        final Updater updater = new Updater(this);
        final ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.progress_title);
        progressDialog.setMessage(getString(R.string.progress_text_episode_list));
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        updater.updateAnimeEpisodeList(anime, new Updater.OnProgress() {
            @Override
            public void progress(final int amount, final int total) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (amount == total) {
                            progressDialog.dismiss();

                            List<Episode> episodeList = Episode.getAll(AnimeActivity.this, anime);
                            adapter.setDataList(episodeList);
                            updater.release();
                        } else {
                            float progress = ((float) amount / (float) total) * 100;
                            progressDialog.setProgress((int) progress);
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.episode_list, menu);

        if (anime.getFavorite() == 1) {
            MenuItem item = menu.findItem(R.id.action_favorite);
            item.setTitle(R.string.menu_favorited);
            item.setIcon(R.drawable.ic_star_white_24dp);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            updateAnimeEpisodeList();
        } else if (item.getItemId() == R.id.action_favorite) {
            if (item.getTitle().equals(getString(R.string.menu_favorite))) {
                item.setTitle(R.string.menu_favorited);
                item.setIcon(R.drawable.ic_star_white_24dp);
                anime.setFavorite(1);
            } else {
                item.setTitle(R.string.menu_favorite);
                item.setIcon(R.drawable.ic_star_border_white_24dp);
                anime.setFavorite(0);
            }

            anime.saveAnime(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("anime", anime);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
