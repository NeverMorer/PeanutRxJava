package com.dhn.peanut.data.remote;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dhn.peanut.PeanutApplication;
import com.dhn.peanut.data.Shot;
import com.dhn.peanut.data.base.LoadShotsCallback;
import com.dhn.peanut.data.base.ShotDataSource;
import com.dhn.peanut.retrofit.DribleApi;
import com.dhn.peanut.util.Request4Shots;
import com.dhn.peanut.util.RequestManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by DHN on 2016/5/31.
 */
public class RemoteShotData implements ShotDataSource {

    private static final String TAG = "RemoteShotData";
    public static RemoteShotData INSTANCE;
    List<Shot> mShots;
    List<Shot> mDebuts;
    List<Shot> mTeams;

    private RequestQueue mRequestQueue;

    private RemoteShotData() {
        mShots = new ArrayList<>();
        mDebuts = new ArrayList<>();
        mTeams = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(PeanutApplication.getContext());
    }

    //TODO 单例
    public static synchronized RemoteShotData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteShotData();
        }
        return INSTANCE;
    }


    /**
     *
     * @param page
     * @return
     */
    @Override
    public Observable<List<Shot>> getShots(int page) {
        if (page == 1) {
            mShots.clear();
        }

        //创建代理对象
        final DribleApi.IShot shot = DribleApi.getInstance().create(DribleApi.IShot.class);

        return shot.getShot(page)
                .startWith(Observable.just(mShots))      //并和已加载的和网络数据流
                .flatMap(new Func1<List<Shot>, Observable<Shot>>() {    //使发送Shot
                    @Override
                    public Observable<Shot> call(List<Shot> shots) {
                        return Observable.from(shots);
                    }
                })
                .toList()                                               //发送一次List<Shot>
                .map(new Func1<List<Shot>, List<Shot>>() {              //截获数据
                    @Override
                    public List<Shot> call(List<Shot> shots) {
                        mShots = shots;
                        return shots;
                    }
                });

    }


    @Override
    public Observable<List<Shot>> getDebuts(int page) {

        if (page == 1) {
            mDebuts.clear();
        }

        //创建代理对象
        final DribleApi.IShot shot = DribleApi.getInstance().create(DribleApi.IShot.class);

        return shot.getDebut(page)                      //发起网络调用
                .startWith(Observable.just(mDebuts))      //并和已加载的和网络数据流
                .flatMap(new Func1<List<Shot>, Observable<Shot>>() {    //将元素转换为Shot
                    @Override
                    public Observable<Shot> call(List<Shot> shots) {
                        return Observable.from(shots);
                    }
                })
                .toList()                                               //将元素转换为发送一次List<Shot>
                .map(new Func1<List<Shot>, List<Shot>>() {              //截获数据
                    @Override
                    public List<Shot> call(List<Shot> shots) {
                        mDebuts = shots;
                        return shots;
                    }
                });
    }

    @Override
    public Observable<List<Shot>> getTeams(int page) {

        if (page == 1) {
            mTeams.clear();
        }

        //创建代理对象
        final DribleApi.IShot shot = DribleApi.getInstance().create(DribleApi.IShot.class);

        return shot.getTeams(page)                      //发起网络调用
                .startWith(Observable.just(mTeams))      //并和已加载的和网络数据流
                .flatMap(new Func1<List<Shot>, Observable<Shot>>() {    //将元素转换为Shot
                    @Override
                    public Observable<Shot> call(List<Shot> shots) {
                        return Observable.from(shots);
                    }
                })
                .toList()                                               //将元素转换为发送一次List<Shot>
                .map(new Func1<List<Shot>, List<Shot>>() {              //截获数据
                    @Override
                    public List<Shot> call(List<Shot> shots) {
                        mTeams = shots;
                        return shots;
                    }
                });



    }


}
