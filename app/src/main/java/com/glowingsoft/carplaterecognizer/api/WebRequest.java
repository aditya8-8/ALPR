package com.glowingsoft.carplaterecognizer.api;

import android.content.Context;
import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/* loaded from: classes.dex */
public class WebRequest {
    public static AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);

    public static void post(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        client.post(context, url, params, responseHandler);
        Log.d("response", "post: request sent");
    }
}
