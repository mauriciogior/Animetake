package tv.animetake.app.fragment;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.futuremind.recyclerviewfastscroll.FastScroller;

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

        setHasOptionsMenu(true);
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
        FastScroller fastScroller = (FastScroller) view.findViewById(R.id.fastScroll);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        adapter = new AnimeListGridAdapter();
        List<Anime> animeList = Anime.getAll(getActivity());
        adapter.setDataList(animeList);

        recyclerView.setAdapter(adapter);
        fastScroller.setRecyclerView(recyclerView);

        adapter.setOnClickListener(new AnimeListGridAdapter.OnClickListener() {
            @Override
            public void onClick(Anime anime) {
                Intent intent = new Intent(getActivity(), AnimeActivity.class);
                intent.putExtra("anime", anime);
                startActivity(intent);
            }
        });

        if (animeList.size() == 0) {
            updateAnimeList();
        }
    }

    private void updateAnimeList() {
        final Updater updater = new Updater(getActivity());
        final ProgressDialog progressDialog;

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.progress_title);
        progressDialog.setMessage(getString(R.string.progress_text_anime_list));
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        updater.updateAnimeList(new Updater.OnProgress() {
            @Override
            public void progress(final int amount, final int total) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (amount == total) {
                            progressDialog.dismiss();

                            List<Anime> animeList = Anime.getAll(getActivity());
                            adapter.setDataList(animeList);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.anime_list_grid, menu);

        SearchView searchView = new SearchView(getActivity());
        menu.findItem(R.id.action_search).setActionView(searchView);

        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            List<Anime> animeListAll = Anime.getAll(getActivity());

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    adapter.setDataList(animeListAll);
                } else {
                    List<Anime> animeList = Anime.searchLike(getActivity(), "%" + newText + "%");
                    adapter.setDataList(animeList);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            SearchView searchView = (SearchView) item.getActionView();
            searchView.setIconified(false);
            return true;

        } else if (item.getItemId() == R.id.action_refresh) {
            updateAnimeList();
        }

        return super.onOptionsItemSelected(item);
    }
}
