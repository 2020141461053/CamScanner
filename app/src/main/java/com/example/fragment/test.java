package com.example.fragment;

import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class test {
    public static void main(String[] args) throws IOException {
        System.out.println(uploadFile("task/outline", new File("D:\\test.docx")));
    }

    public static String uploadFile(String path, File file) throws IOException {
        String BaseUrl = "http://192.168.171.19:8090/";
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("multipart/form-data");
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(), fileBody)
                .build();
        Request request = new Request.Builder()
                .url(BaseUrl + path)
                .post(requestBody)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
