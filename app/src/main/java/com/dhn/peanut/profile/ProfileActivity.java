package com.dhn.peanut.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dhn.peanut.R;
import com.dhn.peanut.data.Shot;
import com.dhn.peanut.data.base.ProfileCallback;
import com.dhn.peanut.data.base.ProfileDataSource;
import com.dhn.peanut.data.remote.RemoteProfileDataSource;
import com.dhn.peanut.login.LoginActivity;
import com.dhn.peanut.util.AuthoUtil;
import com.dhn.peanut.util.Log;
import com.dhn.peanut.util.PeanutInfo;
import com.dhn.peanut.util.RequestManager;
import com.dhn.peanut.view.AutoLoadRecyclerView;
import com.victor.loading.rotate.RotateLoading;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.user_info_recyclerview)
    AutoLoadRecyclerView mRecyclerView;
    @BindView(R.id.rotateloading)
    RotateLoading mLoading;

    private Menu mMenu;
    private Shot.User user;
    private ProfileAdapter adapter;
    private ProfileDataSource dataSource;
    private boolean mIsFollowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        user = (Shot.User) intent.getSerializableExtra("user");

        dataSource = new RemoteProfileDataSource();
    }

    private void initView() {
        //Loading
        mLoading.start();
        mRecyclerView.setVisibility(View.INVISIBLE);

        //处理menu状态
        if (AuthoUtil.isLogined()) {
            //已登录，查看是否follow
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dataSource.checkFollow(user.getId())
                            .subscribe(new Subscriber<ResponseBody>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {
                                    mMenu.findItem(R.id.menu_follow).setTitle(R.string.menu_follow);
                                    mIsFollowed = false;
                                }

                                @Override
                                public void onNext(ResponseBody responseBody) {
                                    mMenu.findItem(R.id.menu_follow).setTitle(R.string.menu_followed);
                                    mIsFollowed = true;
                                }
                            });
                }
            }, 500);
        } else {
            //未登录
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMenu.findItem(R.id.menu_follow).setTitle("未登录");
                }
            }, 500);
        }

        //ToolBar
        mToolbar.setTitle(user.getUsername());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.arrow_left);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));

        //RecyclerView
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ProfileAdapter(this, user);
        mRecyclerView.setAdapter(adapter);

        //加载第一页数据
        Observable<List<Shot>> observable = dataSource.getShot(user.getId(), true);
        if (observable == null) {
            Toast.makeText(ProfileActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
        } else {
            observable.subscribe(new Subscriber<List<Shot>>() {
                @Override
                public void onCompleted() {
                    mLoading.stop();
                    mRecyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onNext(List<Shot> shots) {
                    Log.e(shots.toString());
                    adapter.replaceData(shots);
                }
            });
        }


        //加载更多
        mRecyclerView.setLoadMoreListener(new AutoLoadRecyclerView.LoadMoreListener() {
            //滑动到底部时调用
            @Override
            public void loadMore() {
                Observable<List<Shot>> observable = dataSource.getShot(user.getId(), false);
                if (observable == null) {
                    Toast.makeText(ProfileActivity.this, "无更多数据", Toast.LENGTH_SHORT).show();
                } else {
                    observable.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<List<Shot>>() {
                                @Override
                                public void onCompleted() {

                                }

                                @Override
                                public void onError(Throwable e) {

                                }

                                @Override
                                public void onNext(List<Shot> shots) {
                                    adapter.replaceData(shots);
                                }
                            });
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_follow, menu);
        mMenu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_follow) {

            if (!AuthoUtil.isLogined()) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            } else {

                Call<ResponseBody> call = dataSource.changeFollowState(user.getId(), !mIsFollowed);

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccess()) {

                            if (mIsFollowed) {
                                Toast.makeText(ProfileActivity.this, R.string.unfollowed, Toast.LENGTH_SHORT).show();
                                item.setTitle(R.string.menu_follow);
                            } else {
                                Toast.makeText(ProfileActivity.this, R.string.followed, Toast.LENGTH_SHORT).show();
                                item.setTitle(R.string.menu_followed);
                            }
                            mIsFollowed = !mIsFollowed;
                        } else {
                            Toast.makeText(ProfileActivity.this, "出了点小问题", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
            return true;
        }else {
            return false;
        }
    }

}
