package com.bing.tracedemo.ui.tracelist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.bing.tracedemo.R;
import com.bing.tracedemo.ui.trace.TraceFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ShowTrace extends AppCompatActivity {
    List<LatLng>points ;
    MapView mMapview;
    BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_trace);
        mMapview = findViewById(R.id.show_trace_mapview);
        mBaiduMap = mMapview.getMap();
        Intent intent = getIntent();
        String time = intent.getStringExtra("time");
        String location = intent.getStringExtra("location");
        String point = intent.getStringExtra("points");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<LatLng>>(){}.getType();
        points = gson.fromJson(point,type);
        Log.d("point",point);
       /* ActionBar actionBar = getActionBar();
        actionBar.setTitle(time);*/
        turnToDestination(points.get(0));
        drawTrace();
    }

    public void drawTrace() {
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(points);
        //在地图上绘制折线
        //mPloyline 折线对象
        Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);

    }

    public void turnToDestination(LatLng latLng){
            MapStatus status = new MapStatus.Builder()
                    .target(latLng)
                    .zoom(18.f)
                    .build();
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(status));
    }
}
