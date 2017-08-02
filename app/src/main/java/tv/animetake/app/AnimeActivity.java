package tv.animetake.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import tv.animetake.app.adapter.EpisodeListAdapter;
import tv.animetake.app.helper.Updater;
import tv.animetake.app.model.Anime;
import tv.animetake.app.model.Episode;

public class AnimeActivity extends AppCompatActivity {

    private Anime anime;
    private Updater updater;
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

        updater = new Updater(this);
        updater.updateAnimeEpisodeList(anime, new Updater.OnUpdated() {
            @Override
            public void updated(Object object) {
                List<Episode> episodeList = Episode.getAll(AnimeActivity.this, anime);
                adapter.setDataList(episodeList);
                updater.release();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("anime", anime);
    }
}
