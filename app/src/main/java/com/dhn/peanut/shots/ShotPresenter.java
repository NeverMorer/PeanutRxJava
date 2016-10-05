package com.dhn.peanut.shots;


import com.dhn.peanut.data.Shot;
import com.dhn.peanut.data.base.ShotDataSource;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by DHN on 2016/5/31.
 */
public class ShotPresenter implements ShotsContract.Presenter {

    public static final String TAG = "ShortPresenter";

    private ShotsContract.View mShotView;
    private ShotsContract.View mDebutsView;
    private ShotsContract.View mTeamsView;
    private ShotDataSource mShotDataSource;

    private CompositeSubscription mSubscriptions;

    private int mShotsPage = 1;
    private int mDebutsPage = 1;
    private int mTeamPage = 1;

    public ShotPresenter(ShotsContract.View shotView,
                         ShotsContract.View debutsView,
                         ShotsContract.View gifView,
                         ShotDataSource dataSource) {

        mShotView = shotView;
        mDebutsView = debutsView;
        mTeamsView = gifView;

        mShotDataSource = dataSource;

        mShotView.setPresenter(this);
        mDebutsView.setPresenter(this);
        mTeamsView.setPresenter(this);

        mSubscriptions = new CompositeSubscription();
    }

    @Override
    public void loadShots(boolean isFirstPage) {
        if (isFirstPage == true) {
            mShotsPage = 1;
        }

        if (mShotsPage == 1) {
            mShotView.showLoading();
        }

        Subscription subscription = mShotDataSource.getShots(mShotsPage)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<Shot>>() {
                            @Override
                            public void call(List<Shot> shots) {
                                mShotView.hideLoading();
                                mShotView.showLoadingIndicator(false);
                                mShotView.showShots(shots);
                            }
                        });

        mShotsPage++;
        mSubscriptions.add(subscription);
    }

    @Override
    public void loadDebuts(boolean isFirstPage) {
        if (isFirstPage == true) {
            mDebutsPage = 1;
        }

        if (mDebutsPage == 1) {
            mDebutsView.showLoading();
        }

        Subscription subscription = mShotDataSource.getDebuts(mDebutsPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Shot>>() {
                    @Override
                    public void call(List<Shot> shots) {
                        mDebutsView.hideLoading();
                        mDebutsView.showLoadingIndicator(false);
                        mDebutsView.showShots(shots);
                    }
                });

        mDebutsPage++;
        mSubscriptions.add(subscription);
    }

    @Override
    public void loadTeams(boolean isFirstPage) {
        if (isFirstPage == true) {
            mTeamPage = 1;
        }

        if (mTeamPage == 1) {
            mTeamsView.showLoading();
        }

        Subscription subscription = mShotDataSource.getTeams(mTeamPage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Shot>>() {
                    @Override
                    public void call(List<Shot> shots) {
                        mTeamsView.hideLoading();
                        mTeamsView.showLoadingIndicator(false);
                        mTeamsView.showShots(shots);
                    }
                });

        mTeamPage++;
        mSubscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }


}
