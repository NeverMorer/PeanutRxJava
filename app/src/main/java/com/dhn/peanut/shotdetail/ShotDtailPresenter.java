package com.dhn.peanut.shotdetail;

import android.widget.Toast;

import com.dhn.peanut.PeanutApplication;
import com.dhn.peanut.data.Comment;
import com.dhn.peanut.data.base.ShotDetailDataSource;
import com.dhn.peanut.util.AuthoUtil;
import com.dhn.peanut.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by DHN on 2016/6/1.
 */
public class ShotDtailPresenter implements ShotDetailContract.Presenter {

    private ShotDetailContract.View mView;
    private ShotDetailDataSource mDatasource;
    private boolean mIsLiked = false;


    ShotDtailPresenter(ShotDetailContract.View view, ShotDetailDataSource dataSource) {
        mView = view;
        mView.setPresenter(this);
        mDatasource = dataSource;
    }

    @Override
    public void loadComment(int shotId) {
        //显示等待动画
        mView.showProgress();

        mDatasource.getComment(shotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Comment>>() {
                    @Override
                    public void call(List<Comment> comments) {
                        mView.hideProgress();
                        mView.showComments(comments);
                    }
                });
    }



    @Override
    public void checkLiked(int id) {
        mDatasource.checkIfLike(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showUnLike();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        String s = "";
                        try {
                            s = new String (responseBody.bytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (s.equals("empty token")) {
                            mView.showUnLike();
                        } else {
                            mView.showLike();
                            //标记喜欢
                            mIsLiked = true;
                        }
                    }
                });
    }

    @Override
    public void changeLike(final int id) {
        //先判断是否喜欢
        if (mIsLiked) {
            mDatasource.changeLike(id, false);
            mView.showUnLike();
            Toast.makeText(PeanutApplication.getContext(), "从喜欢列表去除", Toast.LENGTH_SHORT).show();
        } else {
            mDatasource.changeLike(id, true);
            mView.showLike();
            Toast.makeText(PeanutApplication.getContext(), "添加到喜欢列表", Toast.LENGTH_SHORT).show();
        }
        mIsLiked  = !mIsLiked;

    }
}
