package com.sn.go4lunch.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class ListViewModel extends ViewModel {
    private CompositeDisposable disposable = new CompositeDisposable();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<List<PlaceDetailsResults>> places = new MutableLiveData<>();
    public MutableLiveData<Throwable> error = new MutableLiveData<>();

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    public void streamFetchPlaceInfo(String location, int radius, String type, String key){
        this.disposable.add(PlacesStreams.streamFetchPlaceInfo(location, radius, type, key)
                .doOnSubscribe(sub -> isLoading.postValue(true))
                .doOnComplete(() -> isLoading.postValue(false))
                .doOnError(error -> isLoading.postValue(false))
                .subscribe((List<PlaceDetailsResults>result) -> places.postValue(result),
                        (Throwable error) -> this.error.postValue(error)));
    }

}