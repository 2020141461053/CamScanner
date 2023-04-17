package com.example.fragment.Utils;

import android.content.Context;
import android.widget.Toast;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    static Gson gson = new Gson();
    private static OkHttpClient client = new OkHttpClient();
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";

    public static final String BaseUrl = "http://43.156.5.87:8090/";
    private HttpUtils() {


    }


    public static Response uploadFile(String path, File file,String fileName) throws IOException {
        System.out.println(BaseUrl + path);
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody fileBody = RequestBody.create(mediaType, file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .build();
        Request request = new Request.Builder()
                .url(BaseUrl + path)
                .post(requestBody)
                .build();
        okhttp3.Response response = client.newCall(request).execute();

        return gson.fromJson(response.body().string(), Response.class);
    }


    public static Response doGet(String requestUrl) throws IOException {

        requestUrl=BaseUrl+requestUrl;
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        String res=response.toString();
        return   gson.fromJson(res, Response.class);
    }



}
