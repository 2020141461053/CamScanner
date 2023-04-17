package com.example.fragment.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.*;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.fragment.R;
import com.example.fragment.Utils.HttpUtils;
import com.example.fragment.Utils.MyDatabaseHelper;
import com.example.fragment.Utils.Response;

import java.io.*;

import static com.example.fragment.Utils.HttpUtils.BaseUrl;

public class Affair extends AppCompatActivity {
    public static final int OPEN_GALLERY_REQUEST_CODE = 0;
    public static final int TAKE_PHOTO_REQUEST_CODE = 1;
    TextView affair_name;

    TextView desc_;
    Button select;
    String affair;
    String url;
    String document;
    String des;
    int lock=0;//锁，是否进行上传处理中服务和上传锁屏
    LinearLayout file_show;

    TextView file_name;
    Uri uri;
    String fileName="文件名";
    File file;
    Button upLoad;
    MyDatabaseHelper helper;
  //  MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
    //SQLiteDatabase db = dbHelper.getWritableDatabase();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_affair);
        requestAllPermissions();
        initIntent();
        initView();
        helper = new MyDatabaseHelper(this);
    }

    private  void initIntent(){
        Intent intent = getIntent();
        affair=intent.getStringExtra("affair");
        url=intent.getStringExtra("url");
        document=intent.getStringExtra("document_class");
        des=intent.getStringExtra("desc_");
    }
    private  void initView(){
        affair_name=findViewById(R.id.affair_id);
        select = findViewById(R.id.select);
        desc_=findViewById(R.id.desc_);
        desc_.setText(des);
        affair_name.setText(affair);
        file_show=findViewById(R.id.file_show);
        file_name=findViewById(R.id.file_name);
        upLoad=findViewById(R.id.uoload);
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        upLoad.setClickable(false);
    }



    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        switch (document){
            // 指定要选择的文件类型
            case "photo":intent.setType("image/*");break;
            case "word":intent.setType("application/msword");break;
            case "ppt":intent.setType("application/vnd.ms-powerpoint");break;
            case "pdf":intent.setType("application/pdf");break;
            default:intent.setType("*/*");break;
        }

        startActivityForResult(intent, OPEN_GALLERY_REQUEST_CODE);
        upLoad.setClickable(true);
    }

    private boolean requestAllPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA},
                    0);
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            uri = data.getData();
            fileName = getFileNameFromUri(uri);
            Toast.makeText(getApplicationContext(), "文件名："+fileName, Toast.LENGTH_SHORT).show();
            file_name.setText(fileName);
        }
    }
    private String getRealPathFromUri(Uri uri) {
        String filePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try (InputStream is = getContentResolver().openInputStream(uri)) {
                File file = new File(getExternalFilesDir(null), "temp");
                try (OutputStream os = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    filePath = file.getAbsolutePath();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                filePath = cursor.getString(columnIndex);
                cursor.close();
            }
        }
        return filePath;
    }


    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }



    // 从URI中获取文件名
    private String getFileNameFromUri(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DISPLAY_NAME };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String fileName = cursor.getString(columnIndex);
        cursor.close();
        return fileName;
    }

    public  void  delete(View view){
        if(!fileName.equals("")){
            uri=null;
            fileName="文件名";
        }
        file_name.setText(fileName);
        upLoad.setClickable(false);
    }

    public  void  upLoad(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                Message message = mHandler.obtainMessage();
                try {
                    file=new File(getRealPathFromUri( uri));
                    Response response=HttpUtils.uploadFile(url,file,fileName);
                    System.out.println(response.getResult());
                 if(response.getCode()!=200){
                     message.obj = response.getMessage();
                 }else {
                     helper.insertData( response.getResult(),affair, fileName);
                     message.obj = "新建任务成功！请前往历史记录查看";
                 }
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.toString();
                }
            }
        }).start();

    }


    public void test(View view) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response response=HttpUtils.doGet("down/102");
                        Message message = mHandler.obtainMessage();
                        message.obj = response.getResult();
                        mHandler.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String message = (String) msg.obj;
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
        }
    };
}