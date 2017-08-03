package tv.animetake.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tv.animetake.app.R;
import tv.animetake.app.model.Anime;

/**
 * Created by mauricio on 02/08/17.
 */

public class AnimeListGridAdapter extends RecyclerView.Adapter<AnimeListGridAdapter.ViewHolder>
    implements SectionTitleProvider  {

    private OnClickListener onClickListener;
    private List<Anime> dataList = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_anime_list_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Anime anime = dataList.get(position);

        holder.position = position;
        holder.title.setText(anime.getTitle());

        Picasso
        .with(holder.thumbnail.getContext())
        .load(anime.getThumbnail())
        .placeholder(R.drawable.thumbnail_placeholder)
        .networkPolicy(NetworkPolicy.OFFLINE)
        .into(holder.thumbnail, new Callback() {
            @Override
            public void onSuccess() {}

            @Override
            public void onError() {
                //Try again online if cache failed
                Picasso.with(holder.thumbnail.getContext())
                    .load(anime.getThumbnail())
                    .placeholder(R.drawable.thumbnail_placeholder)
                    .into(holder.thumbnail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<Anime> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public String getSectionTitle(int position) {
        return dataList.get(position).getTitle().substring(0, 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public ImageView thumbnail;
        public int position;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(dataList.get(position));
            }
        }
    }

    public interface OnClickListener {
        void onClick(Anime anime);
    }

}
