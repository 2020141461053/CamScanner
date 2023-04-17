package com.example.fragment.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import android.os.Message;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import com.example.fragment.R;
import com.example.fragment.Utils.HttpUtils;
import com.example.fragment.Utils.MyDatabaseHelper;
import com.example.fragment.Utils.Response;

import java.io.IOException;

import static com.example.fragment.Utils.HttpUtils.BaseUrl;


public class DashboardFragment extends Fragment {


    View rootView;
    Response result;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        addView();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void addView() {

        MyDatabaseHelper helper = new MyDatabaseHelper(getContext());

        Cursor c = helper.query();

        String[] from = {MyDatabaseHelper.COLUMN_ID, MyDatabaseHelper.COLUMN_DOWN_URL, MyDatabaseHelper.COLUMN_AFFAIR, MyDatabaseHelper.COLUMN_FILENAME};
        int[] to = {R.id.id, R.id.url, R.id.affair, R.id.filename};
        SimpleCursorAdapter apt = new SimpleCursorAdapter(getContext(), R.layout.query, c, from, to,0);
        //列表视图
        ListView listview = rootView.findViewById(R.id.listview);
        //为列表视图添加适配器
        listview.setAdapter(apt);
        //为listView添加监听器
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 执行删除操作
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("filename"));

                new AlertDialog.Builder(getContext())
                        .setTitle("删除")
                        .setMessage("确实删除" + itemName + " 吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                helper.deleteData((int) id);
                                apt.swapCursor(helper.query());
                                apt.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                        })
                        .create()
                        .show();

                return true;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow("filename"));
                new AlertDialog.Builder(getContext())
                        .setTitle("查看或下载")
                        .setMessage("您要查看还是下载 " + itemName + " ?")
                        .setPositiveButton("查看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                                        try {
                                            result = HttpUtils.doGet("text/" + url);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        if (result.getCode()==200) {//弹窗
                                            Message message = mHandler.obtainMessage();
                                            message.obj = result.getResult();
                                            mHandler.sendMessage(message);
                                        }//提示
                                        else {
                                            Message message = ToastMessage.obtainMessage();
                                            message.obj=result.getMessage();
                                            ToastMessage.sendMessage(message);
                                        }
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("下载", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            result = HttpUtils.doGet("down/" + url);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        if (result.getCode()==200) {//弹窗
                                            Message message = ToastMessage.obtainMessage();
                                            message.obj = "已复制下载链接到剪贴板";
                                            ToastMessage.sendMessage(message);
                                            // 获取剪贴板管理器
                                            copyToClipboard(getContext(),BaseUrl+"api/file/"+result.getResult());
                                        }//提示
                                        else {
                                            Message message = ToastMessage.obtainMessage();
                                            message.obj=result.getMessage();
                                            ToastMessage.sendMessage(message);
                                        }
                                    }
                                }).start();

                            }
                        })
                        .create()
                        .show();
            }
        });

    }
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.edit_text_dialog);
            EditText editText = (EditText) dialog.findViewById(R.id.edit_text);
            editText.setText(message);

            // 获取屏幕宽度和高度
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;
            int screenHeight = displayMetrics.heightPixels;

            // 设置 Dialog 的宽度和高度为屏幕的宽度和高度
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(window.getAttributes());
                layoutParams.width = screenWidth;
                layoutParams.height = screenHeight;
                window.setAttributes(layoutParams);
            }

            // 显示对话框
            dialog.show();
        }

    };

    private Handler ToastMessage = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    };

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", text);
        clipboard.setPrimaryClip(clip);
    }
}