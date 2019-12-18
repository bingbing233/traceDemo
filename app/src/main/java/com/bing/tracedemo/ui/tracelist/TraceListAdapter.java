package com.bing.tracedemo.ui.tracelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bing.tracedemo.MySQLiteOpenHelper;
import com.bing.tracedemo.R;

import java.util.List;

public class TraceListAdapter extends RecyclerView.Adapter<TraceListAdapter.ViewHolder> {
    List<Trace> traces;
    Context context;
    MySQLiteOpenHelper helper;
    Activity activity;


    public TraceListAdapter(List<Trace> traces, Context context, Activity activity) {
        this.traces = traces;
        this.context = context;
        this.activity = activity;
        helper = new MySQLiteOpenHelper(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trace_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Trace trace = traces.get(position);
        holder.timeText.setText(trace.getTime());
        holder.locText.setText(trace.getLocation());
        holder.traceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Trace trace = traces.get(position);
                intent.putExtra("points", trace.getPoints());
                intent.setClass(context, ShowTrace.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        holder.traceView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.traceView);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setGravity(Gravity.RIGHT);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                builder.setTitle("提示");
                                builder.setMessage("确认删除这条记录吗？");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        delete(trace.getId(), position);
                                        Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.show();
                                break;
                            case R.id.share:
                                Intent textIntent = new Intent(Intent.ACTION_SEND);

                                textIntent.setType("text/plain");

                                textIntent.putExtra(Intent.EXTRA_TEXT, trace.getTime()+"\n"+trace.getLocation());

                                activity.startActivity(Intent.createChooser(textIntent, "分享"));
                                break;
                        }
                        return true;
                    }
                });


                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return traces.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeText;
        TextView locText;
        View traceView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.time_text);
            locText = itemView.findViewById(R.id.location_text);
            traceView = itemView;
        }
    }

    public void delete(int id, int position) {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete("trace", "id = " + id, null);
        traces.remove(position);
        this.notifyDataSetChanged();
    }
}
