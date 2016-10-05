package com.dhn.peanut.retrofit;

import com.dhn.peanut.data.Comment;
import com.dhn.peanut.data.Following;
import com.dhn.peanut.data.LikedShot;
import com.dhn.peanut.data.Shot;
import com.dhn.peanut.util.PeanutInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;
import rx.Observable;


/**
 * Created by DHN on 2016/9/8.
 */
public class DribleApi {
    private static Retrofit retrofit;
    private static Retrofit retrofitWithoutConverter;

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

    public synchronized static Retrofit getInstanceWithoutConverter() {
        if (retrofitWithoutConverter == null) {
            retrofitWithoutConverter = new Retrofit.Builder()
                    .baseUrl(PeanutInfo.URL_BASE)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofitWithoutConverter;
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

    public interface IDetail {
        @Headers(PeanutInfo.CLIENT_AUTH)
        @GET("shots/{id}/comments")
        Observable<List<Comment>> getCommnet(@Path("id") int shotId);

        @Headers(PeanutInfo.CLIENT_AUTH)
        @GET("shots/{id}/like")
        Observable<ResponseBody> checkIfLike(@Path("id") int shotId);

        @Headers(PeanutInfo.CLIENT_AUTH)
        @POST("shots/{id}/like")
        Observable<ResponseBody> likeShot(@Path("id") int shotId);

        @Headers(PeanutInfo.CLIENT_AUTH)
        @DELETE("shots/{id}/like")
        Observable<ResponseBody> unLikeShot(@Path("id") int shotId);
    }

    public interface IProfile {

        @Headers(PeanutInfo.CLIENT_AUTH)
        @GET
        Observable<Response<ResponseBody>> getShot(@Url String url);

        @Headers(PeanutInfo.CLIENT_AUTH)
        @GET("user/following/{userId}")
        Observable<ResponseBody> checkIfFollow(@Path("userId") int userid);

        @PUT("users/{userId}/follow")
        Call<ResponseBody> followUser(@Path("userId") int userid, @Header("Authorization") String token);

        @DELETE("users/{userId}/follow")
        Call<ResponseBody> unFollowUser(@Path("userId") int userid, @Header("Authorization") String token);
    }

    public interface IFollowing {
        @GET("user/following?page=1&per_page=100")
        Call<List<Following>> getFollowList(@Header("Authorization") String token);
    }

    public interface ILile {
        @Headers(PeanutInfo.CLIENT_AUTH)
        @GET
        Call<List<LikedShot>> getLiked(@Url String url);
    }

    public interface ILogin {
        @POST
        Call<ResponseBody> getToken(@Url String url, @QueryMap Map<String, String> options);
    }

    public interface ICreate {
        @POST
    }
}

