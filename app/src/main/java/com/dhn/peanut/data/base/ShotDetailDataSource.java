package com.dhn.peanut.data.base;

import com.dhn.peanut.data.Comment;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

/**
 * Created by DHN on 2016/6/2.
 */
public interface ShotDetailDataSource {
    interface LoadShotDetailCallBack {
        void onCommentLoaded(List<Comment> comments);
        void onFavorChecked(boolean isLiked);
        void onCommentNotAvailable();
    }

    Observable<List<Comment>> getComment(int shotId);
    Observable<ResponseBody> checkIfLike(int shotId);
    void changeLike(int shotId, boolean isLike);
}
