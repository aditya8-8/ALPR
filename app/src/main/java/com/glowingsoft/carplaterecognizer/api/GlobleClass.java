package com.glowingsoft.carplaterecognizer.api;

import android.app.Application;
import android.util.Log;

/* loaded from: classes.dex */
public class GlobleClass extends Application {
    public static final String BASE_URL = "https://api.platerecognizer.com/";
    private static final String TAG = "Global class";
    public static GlobleClass singleton;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        Log.d("response", "onCreate: url sent");
        singleton = this;
    }

    public static GlobleClass getInstance() {
        return singleton;
    }
}
