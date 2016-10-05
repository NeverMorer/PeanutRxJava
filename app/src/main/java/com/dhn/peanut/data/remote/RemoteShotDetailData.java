package com.dhn.peanut.data.remote;

import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dhn.peanut.PeanutApplication;
import com.dhn.peanut.data.Comment;
import com.dhn.peanut.data.base.ShotDetailDataSource;
import com.dhn.peanut.retrofit.DribleApi;
import com.dhn.peanut.util.AuthoUtil;
import com.dhn.peanut.util.PeanutInfo;
import com.dhn.peanut.util.RequestManager;
import com.dhn.peanut.util.Requet4Comments;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by DHN on 2016/6/2.
 */
public class RemoteShotDetailData implements ShotDetailDataSource {

    public static RemoteShotDetailData INSTANCE;
    private RequestQueue mRequestQueue = RequestManager.newInstance();

    public static RemoteShotDetailData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteShotDetailData();
        }
        return INSTANCE;
    }


    @Override
    public Observable<List<Comment>> getComment(int shotId) {
        //发起网络请求
        DribleApi.IDetail detailProxy = DribleApi.getInstance().create(DribleApi.IDetail.class);
        return detailProxy.getCommnet(shotId);

//        String url = Comment.COMMENTS_BASE_URL + shotId + "/" + "comments";
//        RequestManager.addRequest(mRequestQueue,
//                new Requet4Comments(url, new Response.Listener<List<Comment>>() {
//                    @Override
//                    public void onResponse(List<Comment> response) {
//
//                        commentCallBack.onCommentLoaded(response);
//                    }
//                },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                commentCallBack.onCommentNotAvailable();
//                            }
//                        }), null);
    }

    @Override
    public Observable<ResponseBody> checkIfLike(int shotId) {
        final String token = AuthoUtil.getToken();

        if (token == null) {            //token为空时，显示不喜欢
            return Observable.just(ResponseBody.create(MediaType.parse("text/plain"), "empty token"));
        } else {
            Retrofit retrofit = DribleApi.getInstanceWithoutConverter();
            //代理对象
            DribleApi.IDetail detailProxy = retrofit.create(DribleApi.IDetail.class);
            return detailProxy.checkIfLike(shotId);
        }

//        if (token == null) {
//            loadShotDetailCallBack.onFavorChecked(false);
//        } else {
//            JsonObjectRequest request = new JsonObjectRequest(
//                    Request.Method.GET,
//                    url,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            loadShotDetailCallBack.onFavorChecked(true);
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            loadShotDetailCallBack.onFavorChecked(false);
//                        }
//                    }) {
//                @Override
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    Map<String, String> header = new HashMap<>();
//                    header.put(PeanutInfo.HEAD_AUTH_FILED, PeanutInfo.HEAD_BEAR + token);
//                    header.putAll(super.getHeaders());
//                    return header;
//                }
//            };
//
//            RequestManager.addRequest(mRequestQueue, request, null);
//        }

    }

    @Override
    public void changeLike(int shotId, boolean isLike) {
        Retrofit retrofit = DribleApi.getInstanceWithoutConverter();
        DribleApi.IDetail shotDetail = retrofit.create(DribleApi.IDetail.class);
        Observable<ResponseBody> observable;
        if (isLike) {
            observable = shotDetail.likeShot(shotId);
        } else {
            observable = shotDetail.unLikeShot(shotId);
        }
        //Observable只有在订阅数据时才会获取网络数据
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();


//        String url = PeanutInfo.URL_REQUEST_ON_SHOT + shotId + "/" + "like";
//        final String token = AuthoUtil.getToken();
//        JsonObjectRequest request = new JsonObjectRequest(
//                isLike ? Request.Method.POST : Request.Method.DELETE,
//                url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                }
//        ) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> header = new HashMap<>();
//                header.put(PeanutInfo.HEAD_AUTH_FILED, PeanutInfo.HEAD_BEAR + token);
//                header.putAll(super.getHeaders());
//                return header;
//            }
//        };
//
//        RequestManager.addRequest(mRequestQueue, request, null);

    }
}
