package com.sn.go4lunch.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.sn.go4lunch.Model.User;
import com.sn.go4lunch.R;

import java.util.List;

public class MateAdapter extends RecyclerView.Adapter<MateViewHolder> {

    // FOR DATA
    private List<User> mResults;

    // CONSTRUCTOR
    public MateAdapter(List<User> result) {
        this.mResults = result;
    }

    @Override
    public MateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_mates_item, parent, false);
        return new MateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MateViewHolder viewHolder, int position) {
        viewHolder.updateWithData(this.mResults.get(position));
    }

    public User getMates(int position) {
        return this.mResults.get(position);
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mResults != null) itemCount = mResults.size();
        return itemCount;
    }
}