package tv.animetake.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import tv.animetake.app.AnimeActivity;
import tv.animetake.app.R;
import tv.animetake.app.adapter.AnimeListGridAdapter;
import tv.animetake.app.helper.Updater;
import tv.animetake.app.model.Anime;

/**
 * Created by mauricio on 02/08/17.
 */

public class AnimeListFragment extends Fragment {

    private AnimeListGridAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_anime_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        adapter = new AnimeListGridAdapter();
        List<Anime> animeList = Anime.getAll(getActivity());
        adapter.setDataList(animeList);

        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new AnimeListGridAdapter.OnClickListener() {
            @Override
            public void onClick(Anime anime) {
                Intent intent = new Intent(getActivity(), AnimeActivity.class);
                intent.putExtra("anime", anime);
                startActivity(intent);
            }
        });

        //updateAnimeList();
    }

    private void updateAnimeList() {
        final Updater updater = new Updater(getActivity());
        updater.updateAnimeList(new Updater.OnUpdated() {
            @Override
            public void updated(Object object) {
                List<Anime> animeList = Anime.getAll(getActivity());
                adapter.setDataList(animeList);
                updater.release();
            }
        });
    }

}
