package com.sn.go4lunch.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sn.go4lunch.Activity.MainActivity;
import com.sn.go4lunch.Activity.PlaceDetailActivity;
import com.sn.go4lunch.Api.RestaurantsHelper;
import com.sn.go4lunch.Api.UserHelper;
import com.sn.go4lunch.Model.User;
import com.sn.go4lunch.R;
import com.sn.go4lunch.Utils.DividerItemDecoration;
import com.sn.go4lunch.Utils.ItemClickSupport;
import com.sn.go4lunch.View.MateAdapter;
import com.sn.go4lunch.ViewModel.CommunicationViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MateFragment extends BaseFragment {

    @BindView(R.id.mates_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.mates_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private List<User> mUsers;
    private MateAdapter mMatesAdapter;

    private CommunicationViewModel mViewModel;

    public static MateFragment newInstance() {
        return new MateFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mates, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        mViewModel = new ViewModelProvider(getActivity()).get(CommunicationViewModel.class);
        mViewModel.currentUserUID.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String uid) {
                configureRecyclerView();
                updateUIWhenCreating();
                configureOnClickRecyclerView();
                configureOnSwipeRefresh();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    // CONFIGURATION

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        this.mUsers = new ArrayList<>();
        this.mMatesAdapter = new MateAdapter(this.mUsers);
        this.mRecyclerView.setAdapter(this.mMatesAdapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(
                ContextCompat.getDrawable(getContext(), R.drawable.divider), 100);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureOnSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this::updateUIWhenCreating);
    }

    // ACTION

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_mates_item)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    User result = mMatesAdapter.getMates(position);
                    retrieveBookedRestaurantByUser(result);
                });
    }

    private void retrieveBookedRestaurantByUser(User user) {
        RestaurantsHelper.getBooking(user.getUid(), getTodayDate()).addOnCompleteListener(bookingTask -> {
            if (bookingTask.isSuccessful()) {
                if (!(bookingTask.getResult().isEmpty())) {
                    for (QueryDocumentSnapshot booking : bookingTask.getResult()) {
                        showBookedRestaurantByUser(booking.getData().get("restaurantId").toString());
                    }
                } else {
                    Toast.makeText(getContext(), getResources()
                            .getString(R.string.mates_hasnt_decided, user.getUsername()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showBookedRestaurantByUser(String placeId) {
        Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
        intent.putExtra("PlaceDetailResult", placeId);
        startActivity(intent);
    }

    // UI

    // Update UI when activity is creating
    private void updateUIWhenCreating() {
        this.mSwipeRefreshLayout.setRefreshing(true);
        CollectionReference collectionReference = UserHelper.getUsersCollection();
        collectionReference.get().addOnCompleteListener(task -> {
            mSwipeRefreshLayout.setRefreshing(false);
            if (task.isSuccessful()) {
                mUsers.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (!(mViewModel.getCurrentUserUID().equals(document.getData().get("uid").toString()))) {
                        String uid = document.getData().get("uid").toString();
                        String username = document.getData().get("username").toString();
                        String urlPicture = document.getData().get("urlPicture").toString();
                        User userToAdd = new User(
                                uid,
                                username,
                                urlPicture,
                                MainActivity.DEFAULT_SEARCH_RADIUS,
                                MainActivity.DEFAULT_ZOOM, false);
                        mUsers.add(userToAdd);
                    }
                }
            } else {
                Log.e("TAG", "Error getting documents: ", task.getException());
            }
            mMatesAdapter.notifyDataSetChanged();
        })
                .addOnFailureListener(e -> {
                    getActivity().runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
                    handleError(e);
                });
    }
}