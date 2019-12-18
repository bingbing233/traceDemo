package com.bing.tracedemo.ui.step;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bing.tracedemo.MyApplication;
import com.bing.tracedemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StepFragment extends Fragment implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepCounter;
    TextView stepText;
    TextView sportState;
    private FloatingActionButton fab;
    private boolean isBind = false;
    Context context = MyApplication.getContext();
    float step = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_step, container, false);
       fab = root.findViewById(R.id.walk_fab);
       stepText = root.findViewById(R.id.step_text);
       sportState = root.findViewById(R.id.sport_state_text);
       //获取管理器实例
       sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
       fab.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               stepText.setText("开始计步");
               initCounter();
           }
       });
        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.values[0]>6){
            step++;
            stepText.setText("当前步数："+((int)step));
            if(step<1000){
                sportState.setText("运动状态：良好");
            }
            if(step>=1000&&step<2000){
                sportState.setText("运动状态：优秀");
            }
            if(step>=1000&&step<2000){
                sportState.setText("运动状态：牛啤");
            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void initCounter(){
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if(stepCounter!=null){
            sensorManager.registerListener(this,stepCounter,1000000);
        }
        else {
            Log.d("notFound","not found sensor");
        }
    }



}