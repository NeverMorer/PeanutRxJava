package com.dhn.peanut.data.base;

import com.dhn.peanut.data.Shot;

import java.util.List;

import rx.Observable;

/**
 * Created by DHN on 2016/5/31.
 */
public interface ShotDataSource {

    Observable<List<Shot>> getShots(int page);
    Observable<List<Shot>> getDebuts(int page);
    Observable<List<Shot>> getTeams(int page);

}

