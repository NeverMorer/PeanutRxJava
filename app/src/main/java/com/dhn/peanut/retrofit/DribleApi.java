package com.dhn.peanut.retrofit;

import com.dhn.peanut.data.Shot;
import com.dhn.peanut.util.PeanutInfo;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;


/**
 * Created by DHN on 2016/9/8.
 */
public class DribleApi {
    private static Retrofit retrofit;

    public synchronized static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(PeanutInfo.URL_BASE)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public interface IShot {

        @Headers(PeanutInfo.CLIENT_AUTH)
        @GET("shots")
        Observable<List<Shot>> getShot(@Query("page") int page);

        @Headers(PeanutInfo.CLIENT_AUTH)
        @GET("shots?list=debuts")
        Observable<List<Shot>> getDebut(@Query("page") int page);


        @Headers(PeanutInfo.CLIENT_AUTH)
        @GET("shots?list=teams")
        Observable<List<Shot>> getTeams(@Query("page") int page);
    }

}



