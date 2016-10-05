package com.dhn.peanut.following;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dhn.peanut.R;
import com.dhn.peanut.data.Following;
import com.dhn.peanut.retrofit.DribleApi;
import com.dhn.peanut.util.AuthoUtil;
import com.dhn.peanut.util.PeanutInfo;
import com.dhn.peanut.util.Request4Following;
import com.dhn.peanut.util.RequestManager;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class FollowingActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rv_following)
    RecyclerView mRecyclerView;
    @BindView(R.id.rotateloading)
    RotateLoading mLoading;
    @BindView(R.id.tv_no_data)
    TextView mTvNoData;

    private FollowingAdapter mAdapter;
    private RequestQueue mRequestQueue = RequestManager.newInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following);
        ButterKnife.bind(this);

        mToolbar.setTitle(R.string.following_follower);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.arrow_left);

        if (!AuthoUtil.isLogined()) {
            showNeedAutho();
        } else {
            mLoading.start();
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mAdapter = new FollowingAdapter(this);

            Retrofit retrofit = DribleApi.getInstance();
            DribleApi.IFollowing service = retrofit.create(DribleApi.IFollowing.class);
            Call<List<Following>> call = service.getFollowList(PeanutInfo.HEAD_BEAR + AuthoUtil.getToken());
            call.enqueue(new Callback<List<Following>>() {
                @Override
                public void onResponse(Call<List<Following>> call, retrofit2.Response<List<Following>> response) {
                    List<Following> list = response.body();
                    if (list.isEmpty()) {
                        showNoData();
                    } else {
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.replaceData(list);
                        mLoading.stop();
                    }
                }

                @Override
                public void onFailure(Call<List<Following>> call, Throwable t) {

                }
            });
        }
    }

    public void showNoData() {
        mTvNoData.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
    }

    public void showNeedAutho() {
        mTvNoData.setVisibility(View.VISIBLE);
        mTvNoData.setText(R.string.following_login);
        mRecyclerView.setVisibility(View.GONE);
        mLoading.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        RequestManager.cancelAll(mRequestQueue, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
