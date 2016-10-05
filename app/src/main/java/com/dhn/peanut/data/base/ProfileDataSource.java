package com.dhn.peanut.data.base;

import com.dhn.peanut.data.Shot;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import rx.Observable;

/**
 * Created by DHN on 2016/6/15.
 */
public interface ProfileDataSource {

    Observable<List<Shot>> getShot(int userId, boolean first);
    Observable<ResponseBody> checkFollow(int userId);
    Call<ResponseBody> changeFollowState(int userId, boolean isFollow);
}
