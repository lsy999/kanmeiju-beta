package com.mrpi.kanmeiju.utils;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mrpi.kanmeiju.AppController;
import com.mrpi.kanmeiju.Appconfig;

import org.json.JSONObject;

/**
 * Created by mrpi on 2016/7/4.
 */
public class LoadVideo {

    private static LoadVideo instacne = null;
    private RequestQueue mQueue;
    private LoadListener listener;
    String tag_json_obj = "json_obj_req";
    public static LoadVideo getInstance(){
        if(instacne == null){
            instacne = new LoadVideo();
        }
        return instacne;
    }

    public void load(String param, final LoadListener listener){
        this.listener = listener;
        String url = Appconfig.URL_GET+param;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, url, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onFail(error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest,tag_json_obj);
    }

}
