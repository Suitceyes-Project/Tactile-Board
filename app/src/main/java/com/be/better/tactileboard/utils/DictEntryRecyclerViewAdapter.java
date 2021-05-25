package com.be.better.tactileboard.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.be.better.tactileboard.R;

import java.util.ArrayList;
import java.util.List;

public class DictEntryRecyclerViewAdapter extends RecyclerView.Adapter<DictEntryRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "DictEntryRecyclerViewAd";

    private List<String> texts = new ArrayList<>();
    private Context context;

    public DictEntryRecyclerViewAdapter(Context context, List<String> texts) {
        this.texts = texts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dict_entry, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "Item added");
        holder.text.setText(texts.get(position));
    }

    @Override
    public int getItemCount() {
        return texts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
