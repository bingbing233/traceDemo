package com.bing.tracedemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bing.tracedemo.ui.tracelist.Trace;
import com.bing.tracedemo.ui.tracelist.TraceListFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity{

    String TAG = "Main2Activity";
    private AppBarConfiguration mAppBarConfiguration;
    TextView accountText;
    ImageView imageView;
    String account;
    SensorManager sensorManager;
    Sensor stepCount;
    Sensor stepDetector;
    int step = 0;
    TextView stepText;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_trace, R.id.nav_trace_list, R.id.nav_step,
                R.id.nav_info)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

         View view =   navigationView.getHeaderView(0);
        accountText = view.findViewById(R.id.account_text);
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        accountText.setText(account);;



        //点击进入登录界面
        imageView = view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });

        //计步器管理器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepCount = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        //sensorManager.registerListener(this,stepCount,1000000);
        Log.d(TAG,"计步器启动");
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public String getAccount(){
        return account;
    }
}
