package com.mrpi.kanmeiju.utils;

import java.util.List;

/**
 * Created by mrpi on 2016/7/4.
 */
public interface LoadListener {
    void onSuccess(String response);
    void onFail(String msg);
}
