package com.bing.tracedemo.ui.tracelist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.model.LatLng;
import com.bing.tracedemo.Main2Activity;
import com.bing.tracedemo.MyApplication;
import com.bing.tracedemo.MySQLiteOpenHelper;
import com.bing.tracedemo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;

import io.reactivex.disposables.Disposable;

public class TraceListFragment extends Fragment {

    String TAG = "TraceListFragment";
    private TraceListViewModel traceListViewModel;
    private RecyclerView recyclerView;
    private MySQLiteOpenHelper helper;
    private SQLiteDatabase database;
    private List<Trace> traces = new ArrayList<>();
    private Activity activity;
    TraceListAdapter traceListAdapter;
    String account;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        traceListViewModel = ViewModelProviders.of(this).get(TraceListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trace_list, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        getData();
        activity = getActivity();
        traceListAdapter = new TraceListAdapter(traces, MyApplication.getContext(), activity);
        recyclerView.setAdapter(traceListAdapter);
        setHasOptionsMenu(true);
        traceListViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.trace_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.upload:
                upLoad();
                break;
            case R.id.download:
                downLoad();
                break;
        }
        return true;
    }

    public void getData() {
        helper = new MySQLiteOpenHelper(getContext());
        database = helper.getReadableDatabase();
        Cursor cursor = database.query("trace", null, null, null, null, null, null);
        traces.removeAll(traces);
        if (cursor.moveToFirst()) {
            do {
                //取出数据
                String time = cursor.getString(cursor.getColumnIndex("time"));
                String location = cursor.getString(cursor.getColumnIndex("location"));
                String points = cursor.getString(cursor.getColumnIndex("points"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                Trace trace = new Trace(points, time, location);
                trace.setId(id);
                traces.add(trace);
                Log.d("data", "time:" + time + "   " + "location:" + location);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        account = ((Main2Activity) getActivity()).getAccount();
    }

    public void upLoad() {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setTitle("上传中,请稍等……");
        dialog.show();
        getData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (Trace trace : traces) {
                    AVObject object = new AVObject("Trace");
                    object.put("time", trace.getTime());
                    object.put("location", trace.getLocation());
                    object.put("id", trace.getId());
                    object.put("points", trace.getPoints());
                    object.put("username", account);
                    object.saveInBackground().subscribe(new io.reactivex.Observer<AVObject>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(AVObject avObject) {
                            //do something
                            dialog.cancel();
                            Toast.makeText(activity,"上传成功",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            Toast.makeText(activity,"上传失败，请检查网络状态",Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                }
            }
        }).start();
    }

    public void downLoad() {
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setTitle("下载中，请稍等……");
        dialog.setCancelable(false);
        dialog.show();

        deleteData();
        new Thread(new Runnable() {
            @Override
            public void run() {
                AVQuery<AVObject> query = new AVQuery<>("Trace");
                query.whereEqualTo("username", account);
                query.findInBackground().subscribe(new io.reactivex.Observer<List<AVObject>>() {

                    public void onSubscribe(Disposable disposable) {
                    }

                    public void onNext(List<AVObject> avObjects) {
                        // avObject 是包含满足条件的对象的数组
                        SQLiteDatabase database = helper.getWritableDatabase();
                        for (AVObject avObject : avObjects) {
                            String time = (String) avObject.get("time");
                            String location = (String) avObject.get("location");
                            String points = (String) avObject.get("points");
                            Log.d(TAG, time + location + points);
                            ContentValues values = new ContentValues();
                            values.put("time", time);
                            values.put("location", location);
                            values.put("points", points);
                            database.insert("trace", null, values);
                        }
                    }

                    public void onError(Throwable throwable) {
                    }

                    public void onComplete() {
                        traces.removeAll(traces);
                        getData();
                        traceListAdapter.notifyDataSetChanged();
                        dialog.cancel();
                    }
                });
            }
        }).start();

    }

    public void deleteData() {
        String sql = "delete from trace";
        SQLiteDatabase database = helper.getWritableDatabase();
        database.execSQL(sql);
        Log.d(TAG, "删除数据成功");
    }
}