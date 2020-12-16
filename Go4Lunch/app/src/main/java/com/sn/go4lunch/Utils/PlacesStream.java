package com.sn.go4lunch.Utils;

import com.sn.go4lunch.Model.AutoComplete.AutoCompleteResult;
import com.sn.go4lunch.Model.PlaceInfo.MapPlacesInfos;
import com.sn.go4lunch.Model.PlaceInfo.PlaceDetail.PlaceDetailsInfo;
import com.sn.go4lunch.Model.PlaceInfo.PlaceDetail.PlaceDetailsResults;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class PlacesStream {
    private static PlacesService mapPlacesInfos = PlacesService.retrofit.create(PlacesService.class);

    public static Observable<MapPlacesInfos> streamFetchNearbyPlaces(String location,
                                                                     int radius, String type,
                                                                     String key){
        return mapPlacesInfos.getNearbyPlaces(location,radius,type,key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<List<PlaceDetailsResults>> streamFetchPlaceInfo(String location,
                                                                             int radius, String type,
                                                                             String key){
        return mapPlacesInfos.getNearbyPlaces(location,radius,type,key)
                .flatMapIterable(MapPlacesInfos::getResults)
                .flatMap(info -> mapPlacesInfos.getPlacesInfo(info.getPlaceId(), key))
                .map(PlaceDetailsInfo::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetailsInfo> streamSimpleFetchPlaceInfo(String placeId, String key){
        return  mapPlacesInfos.getPlacesInfo(placeId, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<AutoCompleteResult> streamFetchAutoComplete(String query,
                                                                         String location, int radius,
                                                                         String apiKey){
        return mapPlacesInfos.getPlaceAutoComplete(query, location, radius, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10,TimeUnit.SECONDS);
    }

    public static  Observable<List<PlaceDetailsResults>> streamFetchAutoCompleteInfo(String query, String location, int radius, String apiKey){
        return mapPlacesInfos.getPlaceAutoComplete(query, location, radius, apiKey)
                .flatMapIterable(AutoCompleteResult::getPredictions)
                .flatMap(info -> mapPlacesInfos.getPlacesInfo(info.getPlaceId(), apiKey))
                .map(PlaceDetailsInfo::getResult)
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
