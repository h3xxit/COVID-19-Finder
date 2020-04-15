package com.anticoronabrigade.frontend.ActivityClasses.HistoryActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.anticoronabrigade.frontend.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.PathHolder> {

    private OnNoteListener mOnNoteListener;

    public class PathHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView location;
        TextView date;
        OnNoteListener onNoteListener;

        PathHolder(View itemView, OnNoteListener onNoteListener){
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            location = itemView.findViewById(R.id.cv_start_location);
            date = itemView.findViewById(R.id.cv_start_date);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    List<PathHistory> Notifications;

    public RecyclerViewAdapter(List<PathHistory> Notifications, OnNoteListener onNoteListener){
        this.Notifications=Notifications;
        this.mOnNoteListener=onNoteListener;
    }
    @NonNull
    @Override
    public PathHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        PathHolder nh = new PathHolder(v, mOnNoteListener);
        return nh;
    }

    @Override
    public void onBindViewHolder(PathHolder holder, int position) {
        holder.location.setText(Notifications.get(position).getBeforeFirstText() + Notifications.get(position).getFirstText());
        holder.date.setText(Notifications.get(position).getBeforeSecondText() + Notifications.get(position).getSecondText());
    }

    @Override
    public int getItemCount() {
        return Notifications.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}