package com.dhn.peanut.shots;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dhn.peanut.PeanutApplication;
import com.dhn.peanut.R;
import com.dhn.peanut.shotdetail.ShotDetailActivity;
import com.dhn.peanut.view.AutoLoadRecyclerView;
import com.dhn.peanut.data.Shot;
import com.squareup.leakcanary.RefWatcher;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class BaseFragment extends Fragment implements ShotsContract.View {

    public static final String TAG = "BaseFragment";

    @BindView(R.id.refreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.recyclerview)
    AutoLoadRecyclerView mRecyclerView;
    @BindView(R.id.rotateloading)
    RotateLoading mLoadingView;

    private ShotAdapter shotAdapter;
    protected ShotsContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_shots, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = PeanutApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    protected void initView() {
        //RecyclerView
        shotAdapter = new ShotAdapter(getActivity(), this);
        mRecyclerView.setAdapter(shotAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //SwiperefreshLayout
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.colorPrimaryDark);

    }

    @Override
    public void setPresenter(ShotsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showShots(List<Shot> shots) {
        shotAdapter.relpaceData(shots);
    }

    @Override
    public void showLoadingIndicator(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    @Override
    public void showShotDetails(Shot shot) {
        //TODO 跳转到图片详情页
        Intent intent = new Intent(getContext(), ShotDetailActivity.class);
        intent.putExtra("shot", shot);
        startActivity(intent);
    }

    @Override
    public void changeLayout(int layout) {
        if (layout == ShotsContract.LINEAR) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        } else if (layout == ShotsContract.GRIDE) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
    }

    @Override
    public void showLoading() {
        mLoadingView.start();
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        mRecyclerView.setVisibility(View.VISIBLE);
        if (mLoadingView.isStart()) {
            mLoadingView.stop();
        }
    }


}
