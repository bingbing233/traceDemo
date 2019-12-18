package com.bing.tracedemo;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import cn.leancloud.AVOSCloud;
import cn.leancloud.AVObject;

public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this.getApplicationContext());
        SDKInitializer.setCoordType(CoordType.BD09LL);
        context = getApplicationContext();
        //初始化云服务
        AVOSCloud.initialize(this, "tfrJmt1VGEBC3WV1kAAIof8u-gzGzoHsz", "XeAATNe1WnzFqHDjHfIChDhK", "tfrjmt1v.lc-cn-n1-shared.com");
       /* AVObject testObject = new AVObject("TestObject");
        testObject.put("words", "Hello world!");
        testObject.saveInBackground().blockingSubscribe();*/
    }
   public static Context getContext(){
        return context;
    }
}
