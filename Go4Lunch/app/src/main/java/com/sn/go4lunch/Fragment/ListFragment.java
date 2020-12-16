package com.sn.go4lunch.Fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.sn.go4lunch.Activity.MainActivity;
import com.sn.go4lunch.Activity.PlaceDetailActivity;
import com.sn.go4lunch.Model.PlaceInfo.PlaceDetail.PlaceDetailsResults;
import com.sn.go4lunch.R;
import com.sn.go4lunch.Utils.DividerItemDecoration;
import com.sn.go4lunch.Utils.ItemClickSupport;
import com.sn.go4lunch.Utils.PlacesStream;
import com.sn.go4lunch.View.RestaurantAdapter;
import com.sn.go4lunch.ViewModel.CommunicationViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class ListFragment extends BaseFragment {

    @BindView(R.id.list_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.list_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private Disposable disposable;
    private List<PlaceDetailsResults> mResults;
    private RestaurantAdapter adapter;

    private CommunicationViewModel mViewModel;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(CommunicationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        mViewModel.currentUserPosition.observe(getViewLifecycleOwner(), latLng -> {
            executeHttpRequestWithRetrofit();
            configureRecyclerView();
        });

        this.configureOnClickRecyclerView();
        this.configureOnSwipeRefresh();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        mViewModel.currentUserPosition.removeObservers(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        inflater.inflate(R.menu.toolbar_menu, menu);

        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = new SearchView(((MainActivity) getContext()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setQueryHint(getResources().getString(R.string.toolbar_search_hint));
        searchView.setSearchableInfo(searchManager.getSearchableInfo(((MainActivity) getContext()).getComponentName()));

        searchView.setIconifiedByDefault(false);// Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    disposable = PlacesStream.streamFetchAutoCompleteInfo(
                            query, mViewModel.getCurrentUserPositionFormatted(),
                            mViewModel.getCurrentUserRadius(),
                            MapFragment.API_KEY).subscribeWith(createObserver());
                } else {
                    Toast.makeText(getContext(), getResources()
                            .getString(R.string.search_too_short), Toast.LENGTH_LONG).show();
                }
                return true;

            }

            @Override
            public boolean onQueryTextChange(String query) {
                if (query.length() > 2) {
                    disposable = PlacesStream.streamFetchAutoCompleteInfo(
                            query, mViewModel.getCurrentUserPositionFormatted(),
                            mViewModel.getCurrentUserRadius(),
                            MapFragment.API_KEY).subscribeWith(createObserver());
                }
                return false;
            }
        });

    }

    // CONFIGURATION

    // Configure RecyclerView, Adapter, LayoutManager & glue it together
    private void configureRecyclerView() {
        this.mResults = new ArrayList<>();
        this.adapter = new RestaurantAdapter(this.mResults, mViewModel.getCurrentUserPositionFormatted());
        this.mRecyclerView.setAdapter(this.adapter);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(
                ContextCompat.getDrawable(getContext(), R.drawable.divider),
                100);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void configureOnSwipeRefresh() {
        mSwipeRefreshLayout.setOnRefreshListener(this::executeHttpRequestWithRetrofit);
    }

    // ACTION

    // Configure item click on RecyclerView
    private void configureOnClickRecyclerView() {
        ItemClickSupport.addTo(mRecyclerView, R.layout.fragment_list_item)
                .setOnItemClickListener((recyclerView, position, v) -> {

                    PlaceDetailsResults result = adapter.getRestaurant(position);
                    Intent intent = new Intent(getActivity(), PlaceDetailActivity.class);
                    intent.putExtra("PlaceDetailResult", result.getPlaceId());
                    startActivity(intent);
                });
    }

    // HTTP (RxJAVA)

    private void executeHttpRequestWithRetrofit() {
        mSwipeRefreshLayout.setRefreshing(true);
        this.disposable = PlacesStream.streamFetchPlaceInfo(
                mViewModel.getCurrentUserPositionFormatted(),
                mViewModel.getCurrentUserRadius(), MapFragment.SEARCH_TYPE,
                MapFragment.API_KEY).subscribeWith(createObserver());
    }

    private <T> DisposableObserver<T> createObserver() {
        return new DisposableObserver<T>() {
            @Override
            public void onNext(T t) {
                if (t instanceof ArrayList) {
                    updateUI((ArrayList) t);
                }
            }

            @Override
            public void onError(Throwable e) {
                getActivity().runOnUiThread(() -> mSwipeRefreshLayout.setRefreshing(false));
                handleError(e);
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && !this.disposable.isDisposed()) this.disposable.dispose();
    }

    // UPDATE UI

    private void updateUI(List<PlaceDetailsResults> results) {
        mSwipeRefreshLayout.setRefreshing(false);
        mResults.clear();
        if (results.size() > 0) {
            mResults.addAll(results);
        } else {
            Toast.makeText(getContext(), getResources()
                    .getString(R.string.no_restaurant_error_message), Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }
}