package tv.animetake.app.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tv.animetake.app.R;
import tv.animetake.app.model.Episode;

/**
 * Created by mauricio on 02/08/17.
 */

public class EpisodeListAdapter extends RecyclerView.Adapter<EpisodeListAdapter.ViewHolder>  {

    private OnClickListener onClickListener;
    private List<Episode> dataList = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_episode_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Episode episode = dataList.get(position);

        holder.position = position;
        holder.title.setText(episode.getTitle());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<Episode> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title;
        public int position;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);

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
        void onClick(Episode episode);
    }

}