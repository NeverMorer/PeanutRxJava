package com.dhn.peanut.data.base;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dhn.peanut.PeanutApplication;
import com.dhn.peanut.data.LikedShot;
import com.dhn.peanut.data.Shot;
import com.dhn.peanut.retrofit.DribleApi;
import com.dhn.peanut.util.AuthoUtil;
import com.dhn.peanut.util.Log;
import com.dhn.peanut.util.PeanutInfo;
import com.dhn.peanut.util.Request4LikedShot;
import com.dhn.peanut.util.Request4Shots;
import com.dhn.peanut.util.RequestManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DHN on 2016/6/8.
 */
public class LikeDataSource {


    public Call<List<LikedShot>> getLikes() {
        String url = AuthoUtil.getLikesUrl() + "?per_page=100";

        Retrofit retrofit = DribleApi.getInstance();
        DribleApi.ILile service = retrofit.create(DribleApi.ILile.class);

        return service.getLiked(url);

    }
}
