package com.sn.go4lunch.View;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.sn.go4lunch.Model.PlaceInfo.PlaceDetail.PlaceDetailsResults;
import com.sn.go4lunch.R;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    // FOR DATA
    private List<PlaceDetailsResults> mResults;
    private String mLocation;

    // CONSTRUCTOR
    public RestaurantAdapter(List<PlaceDetailsResults> result, String location) {
        this.mResults = result;
        this.mLocation = location;
    }

    @Override
    public RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_list_item, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RestaurantViewHolder viewHolder, int position) {
        viewHolder.updateWithData(this.mResults.get(position), this.mLocation);
    }

    public PlaceDetailsResults getRestaurant(int position) {
        return this.mResults.get(position);
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mResults != null) itemCount = mResults.size();
        return itemCount;
    }
}