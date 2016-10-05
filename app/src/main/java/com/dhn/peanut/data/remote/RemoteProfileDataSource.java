package com.dhn.peanut.data.remote;

import com.android.volley.RequestQueue;
import com.dhn.peanut.data.Shot;
import com.dhn.peanut.data.base.ProfileCallback;
import com.dhn.peanut.data.base.ProfileDataSource;
import com.dhn.peanut.retrofit.DribleApi;
import com.dhn.peanut.util.AuthoUtil;
import com.dhn.peanut.util.Log;
import com.dhn.peanut.util.PeanutInfo;
import com.dhn.peanut.util.Request4Shots;
import com.dhn.peanut.util.RequestManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by DHN on 2016/6/15.
 */
public class RemoteProfileDataSource implements ProfileDataSource {

    private String nextUrl;
    private boolean hasNext = false;
    private List<Shot> lists;
    private boolean isFollowed = false;
    private RequestQueue mRequestQueue;

    public RemoteProfileDataSource() {
        lists = new ArrayList<>();
        mRequestQueue = RequestManager.newInstance();
    }

    @Override
    public Observable<List<Shot>> getShot(int userId, boolean first) {
        String curUrl = null;

        if (first) {
            lists.clear();
            curUrl = PeanutInfo.URL_BASE + "users/" + userId + "/shots";
        } else if (hasNext){
            curUrl = nextUrl;
        } else {                //没有更多数据
            return null;
        }

        Retrofit retrofit = DribleApi.getInstanceWithoutConverter();
        DribleApi.IProfile service = retrofit.create(DribleApi.IProfile.class);
        //创建Observable
        return service.getShot(curUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<retrofit2.Response<ResponseBody>, Observable<? extends List<Shot>>>() {
                    @Override
                    public Observable<? extends List<Shot>> call(retrofit2.Response<ResponseBody> response) {

                        String link = response.raw().header("Link");
                        getNextUrl(link);       //截取Link首部

                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(response.body().byteStream()));
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                sb.append(line);
                            }

                            Gson gson = new Gson();
                            List<Shot> shots = gson.fromJson(sb.toString(), new TypeToken<List<Shot>>() {
                            }.getType());
                            lists.addAll(shots);

                            return Observable.from(shots).toList();
                        } catch (IOException e) {
                            e.printStackTrace();

                            Log.e("throw IOException, return null");
                        }
                        return null;
                    }
                });
    }

    @Override
    public Observable<ResponseBody> checkFollow(int userId) {
        Retrofit retrofit = DribleApi.getInstanceWithoutConverter();
        DribleApi.IProfile service = retrofit.create(DribleApi.IProfile.class);
        return service.checkIfFollow(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    @Override
    public Call<ResponseBody> changeFollowState(int userId, final boolean requestFollow) {
        Retrofit retrofit = DribleApi.getInstanceWithoutConverter();
        DribleApi.IProfile service = retrofit.create(DribleApi.IProfile.class);

        if (requestFollow) {
            return service.followUser(userId, PeanutInfo.HEAD_BEAR + AuthoUtil.getToken());
        } else {
            return service.unFollowUser(userId, PeanutInfo.HEAD_BEAR + AuthoUtil.getToken());
        }

    }


    private void getNextUrl(String link) {

        Log.e("Link = " + link);

        //可能为空
        if (link != null) {
            String[] links = link.split(",");
            String next = null;
            for (String str :
                    links) {
                String url = str.substring(str.indexOf("<") + 1, str.lastIndexOf(">"));
                String flag = str.substring(str.indexOf("rel=\"") + 5, str.lastIndexOf("\""));
                if (flag.equals("next")) {
                    next = url;
                    break;
                }
            }
            if (next != null) {
                nextUrl = next;
                hasNext = true;
            } else {
                nextUrl = null;
                hasNext = false;
            }
        } else {
            nextUrl = null;
            hasNext = false;
        }
    }
}
