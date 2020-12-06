package com.sn.go4lunch.Utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlacesStream {
    private static PlacesService mapPlacesInfo = PlacesService.retrofit.create(PlacesService.class);

    public static Observable<MapPlacesInfo> streamFetchNearbyPlaces(String location,
                                                                    int radius, String type,
                                                                    String key){
        return mapPlacesInfo.getNearbyPlaces(location,radius,type,key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static  Observable<List<PlaceDetailsResults>> streamFetchPlaceInfo(String location,
                                                                              int radius, String type,
                                                                              String key){
        return mapPlacesInfo.getNearbyPlaces(location,radius,type,key)
                .flatMapIterable(MapPlacesInfo::getResults)
                .flatMap(info -> mapPlacesInfo.getPlacesInfo(info.getPlaceId(), key))
                .map(PlaceDetailsInfo::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetailsInfo> streamSimpleFetchPlaceInfo(String placeId, String key){
        return  mapPlacesInfo.getPlacesInfo(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<AutoCompleteResult> streamFetchAutoComplete(String query,
                                                                         String location, int radius,
                                                                         String apiKey){
        return mapPlacesInfo.getPlaceAutoComplete(query, location, radius, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10,TimeUnit.SECONDS);
    }

    public static  Observable<List<PlaceDetailsResults>> streamFetchAutoCompleteInfo(String query, String location, int radius, String apiKey){
        return mapPlacesInfo.getPlaceAutoComplete(query, location, radius, apiKey)
                .flatMapIterable(AutoCompleteResult::getPredictions)
                .flatMap(info -> mapPlacesInfo.getPlacesInfo(info.getPlaceId(), apiKey))
                .map(PlaceDetailsInfo::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
