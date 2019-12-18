package com.bing.tracedemo.ui.trace;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.bing.tracedemo.MyApplication;
import com.bing.tracedemo.MySQLiteOpenHelper;
import com.bing.tracedemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class TraceFragment extends Fragment {

    SensorManager sensorManager;
    private TraceViewModel traceViewModel;
    List<LatLng> points = new ArrayList<>();
    MapView mMapview;
    BaiduMap mBaiduMap;
    LocationClient mLocationClient;
    FloatingActionButton fab;
    MyLocationListener mListener = new MyLocationListener();
    boolean isFirstLoc = true;
    boolean isStartTrace = false;
    Context context = MyApplication.getContext();
    String address = "";
    private MySQLiteOpenHelper helper;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.start_trace:
                isStartTrace = true;
                Toast.makeText(context, "开始采集轨迹", Toast.LENGTH_SHORT).show();
                break;
            case R.id.stop_trace:
                isStartTrace = false;
                Toast.makeText(context, "停止采集轨迹", Toast.LENGTH_SHORT).show();
                break;
            case R.id.refesh:
                isFirstLoc = true;
                break;
            case R.id.save:
                save();
                break;
            case R.id.stop_locate:
                mLocationClient.unRegisterLocationListener(mListener);
                mLocationClient.stop();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        traceViewModel = ViewModelProviders.of(this).get(TraceViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trace, container, false);
        mMapview = root.findViewById(R.id.map_view);
        fab = root.findViewById(R.id.fab);
        mBaiduMap = mMapview.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //点击地图，绘制标记
              /*  BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.location_pink_24dp);
                OverlayOptions overlayOptions = new MarkerOptions()
                        .position(latLng)
                        .icon(bitmap);*/
                //mBaiduMap.addOverlay(overlayOptions);
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //点击标记，取消标记
                marker.remove();
                return false;
            }
        });
        setHasOptionsMenu(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTurnOnGPS();
                checkPermission();
                initLocationOption();
            }
        });
        traceViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        //读取数据库
        helper = new MySQLiteOpenHelper(context);
        return root;
    }

    /**
     * 判断是否打开GPS
     *
     * @return
     */
    public boolean isTurnOnGPS() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getActivity(), "GSP未打开", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 检查是否有权限
     */
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            Toast.makeText(context, "开始定位", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 开始定位
     */
    public void initLocationOption() {
        Log.d("initial", "initial");
        mLocationClient = new LocationClient(context);
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        //发起定位的时间间隔
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setWifiCacheTimeOut(5 * 60 * 1000);
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(mListener);
        mLocationClient.start();
    }


    /**
     * 实现位置监听接口
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            double altitude = location.getAltitude();
            String buildName = location.getBuildingName();
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            int errorCode = location.getLocType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            Log.d("position", "latitude = " + latitude + "   " +
                    "longitude = " + longitude + "   " +
                    "Altitude = " + altitude + "   " +
                    "BuildName = " + buildName + "   " +
                    "errorCode = " + errorCode);

            MyLocationData builder = new MyLocationData.Builder()
                    .accuracy(radius)
                    .direction(location.getDirection())
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            mBaiduMap.setMyLocationData(builder);
            //地址信息
            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            String city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            address = addr;
            //开始采集轨迹点经纬度
            if (isStartTrace) {
                points.add(new LatLng(latitude, longitude));
                drawTrace();
            }
            if (isFirstLoc) {
                LatLng latLng = new LatLng(latitude, longitude);
                MapStatus status = new MapStatus.Builder()
                        .target(latLng)
                        .zoom(18.f)
                        .build();
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(status));
                isFirstLoc = false;
            }
        }
    }

    public void drawTrace() {
        if (points.size() > 1) {
            //设置折线的属性
            OverlayOptions mOverlayOptions = new PolylineOptions()
                    .width(10)
                    .color(0xAAFF0000)
                    .points(points);
            //在地图上绘制折线
            //mPloyline 折线对象
            Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
        } else {
            Toast.makeText(context, "收集时间需大于5s", Toast.LENGTH_SHORT).show();
        }
    }

    public void save() {
        mBaiduMap.clear();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
        // 获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String time = year + "." + month + "." + day + "  " + date;
        Gson gson = new Gson();
        String point = gson.toJson(points);
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("time", time);
        values.put("location", address);
        values.put("points", point);
        database.insert("trace", null, values);
        Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onResume() {
        super.onResume();
        mMapview.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapview.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapview.onDestroy();
        // mLocationClient.stop();
    }

}