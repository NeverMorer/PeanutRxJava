package com.dhn.peanut.like;

import android.text.TextUtils;

import com.dhn.peanut.data.LikedShot;
import com.dhn.peanut.data.Shot;
import com.dhn.peanut.data.base.LikeDataSource;
import com.dhn.peanut.util.AuthoUtil;
import com.dhn.peanut.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;

/**
 * Created by DHN on 2016/6/8.
 */
public class LikePresenter implements LikeContract.Presenter {
    private LikeContract.View mView;
    private LikeDataSource mData;

    public LikePresenter(LikeContract.View view, LikeDataSource data) {
        mView = view;
        mData = data;
        mView.setPresenter(this);
    }


    @Override
    public void loadLikes() {
        if (!AuthoUtil.isLogined()) {
            mView.showNeedAutho();
        } else {
            mView.showLoading();

            Call<List<LikedShot>> call = mData.getLikes();
            call.enqueue(new Callback<List<LikedShot>>() {
                @Override
                public void onResponse(Call<List<LikedShot>> call, Response<List<LikedShot>> response) {
                    mView.hideLoading();
                    mView.showLoadingIndicator(false);

                    Log.e("方法:" + call.request().method());
                    Log.e("url:" + call.request().url() );
                    Log.e("状态码:" + response.code());

                    if (response == null || response.body() == null || response.body().isEmpty()) {
                        mView.showNoContent();
                    } else {
                        mView.showLikes(response.body());
                    }
                }

                @Override
                public void onFailure(Call<List<LikedShot>> call, Throwable t) {

                }
            });


//                    .subscribe(new Subscriber<List<LikedShot>>() {
//                        @Override
//                        public void onCompleted() {
//                            mView.hideLoading();
//                            mView.showLoadingIndicator(false);
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//
//                        }
//
//                        @Override
//                        public void onNext(List<LikedShot> likedShots) {
//                            if (likedShots == null || likedShots.isEmpty()) {
//                                mView.showNoContent();
//                            } else {
//                                mView.showLikes(likedShots);
//                            }
//                        }
//                    });

//            mData.getLikes(new LikeDataSource.LoadLikeCallback() {
//                @Override
//                public void onShotsLoaded(List<LikedShot> shots) {
//                    mView.hideLoading();
//                    mView.showLoadingIndicator(false);
//
//                    if (shots == null || shots.isEmpty()) {
//                        mView.showNoContent();
//                    } else {
//                        mView.showLikes(shots);
//                    }
//                }
//
//                @Override
//                public void onDataNotAvailable() {
//                    mView.showNoContent();
//                }
//            });
        }
    }
}
