package com.sn.go4lunch.View;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sn.go4lunch.Api.RestaurantsHelper;
import com.sn.go4lunch.Model.User;
import com.sn.go4lunch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MateViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.mates_main_picture)
    ImageView mImageView;
    @BindView(R.id.mates_textview_username)
    TextView mTextView;

    public MateViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateWithData(User results) {
        RequestManager glide = Glide.with(itemView);
        if (!(results.getUrlPicture() == null)) {
            glide.load(results.getUrlPicture()).apply(RequestOptions.circleCropTransform()).into(mImageView);
        } else {
            glide.load(R.drawable.ic_no_image_available).apply(RequestOptions.circleCropTransform()).into(mImageView);
        }

        RestaurantsHelper.getBooking(results.getUid(), getTodayDate()).addOnCompleteListener(restaurantTask -> {
            if (restaurantTask.isSuccessful()) {
                if (restaurantTask.getResult().size() == 1) { // User already booked a restaurant today
                    for (QueryDocumentSnapshot restaurant : restaurantTask.getResult()) {
                        this.mTextView.setText(itemView.getResources().getString(R.string.mates_is_eating_at, results.getUsername(), restaurant.getData().get("restaurantName")));
                        this.changeTextColor(R.color.colorBlack);
                    }
                } else { // No restaurant booked for this user today
                    this.mTextView.setText(itemView.getResources().getString(R.string.mates_hasnt_decided, results.getUsername()));
                    this.changeTextColor(R.color.colorGray);
                }
            }
        });
    }

    private void changeTextColor(int color) {
        int mColor = itemView.getContext().getResources().getColor(color);
        this.mTextView.setTextColor(mColor);
    }

    private String getTodayDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(c.getTime());
    }
}