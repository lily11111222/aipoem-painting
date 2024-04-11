package com.example.poemheavenjava.utils;


import android.os.Environment;
import android.util.Log;

import okhttp3.*;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class AsyncAnimationRequest {

    public static final String TAG = "lily";
    private static String outGIFPath;
    private static String error;

    public static String generateTimestampedFileName(String extension) {
        long timestamp = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            timestamp = Instant.now().toEpochMilli();
        }
        return String.format("%d.%s", timestamp, extension);
    }

    private static final MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg");
    private static final MediaType MEDIA_TYPE_MP4 = MediaType.parse("video/mp4");
    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.MINUTES) // 连接超时时间为10min
            .readTimeout(10, TimeUnit.MINUTES) // 读取超时时间为60min
            .writeTimeout(10, TimeUnit.MINUTES) // 写入超时时间为60min
            .build();


    public static String generateAnimator(String imgPath, String moviePath, File filePath) {
        File referenceImageFile = new File(imgPath);
        File motionSequenceFile = new File(moviePath);

        RequestBody referenceImageBody = RequestBody.create(referenceImageFile, MEDIA_TYPE_JPEG);
        RequestBody motionSequenceBody = RequestBody.create(motionSequenceFile, MEDIA_TYPE_MP4);

        MultipartBody.Part referenceImagePart = MultipartBody.Part.createFormData("reference_image", referenceImageFile.getName(), referenceImageBody);
        MultipartBody.Part motionSequencePart = MultipartBody.Part.createFormData("motion_sequence", motionSequenceFile.getName(), motionSequenceBody);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(referenceImagePart)
                .addPart(motionSequencePart)
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://139.224.214.59:7861/animate/").newBuilder();
        urlBuilder.addQueryParameter("seed", "1");
        urlBuilder.addQueryParameter("steps", "25");
        urlBuilder.addQueryParameter("guidance_scale", "7.5");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        CompletableFuture<File> future = CompletableFuture.supplyAsync(() -> {
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorMessage = response.body().string();
                    throw new IOException("Unexpected code " + response.code() + ": " + errorMessage);
                }

                InputStream inputStream = response.body().byteStream();
                byte[] bytes = new byte[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    bytes = inputStream.readAllBytes();
                }


                String fileName = generateTimestampedFileName("gif");
                File outputFile = new File(filePath, fileName);
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    outputStream.write(bytes);
                }

                Log.d("lily", "GIF image saved to: " + outputFile.getAbsolutePath());
                return outputFile;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        future.whenComplete((outputFile, throwable) -> {
            if (throwable != null) {
                Log.d(TAG, "Error occurred: " + throwable.getMessage());
                error = throwable.getMessage();
                return ;
            }
            Log.d(TAG, "GIF image returned: " + outputFile.getAbsolutePath());

            outGIFPath = outputFile.getAbsolutePath();
        });

        // 等待异步任务执行完毕
        future.join();
        Log.d(TAG, "return: " + outGIFPath);
        if (outGIFPath != null) {
            Log.d(TAG, "returnSuccess: " + outGIFPath);
            return outGIFPath;
        }
        //if (error != null) throw new RuntimeException(error);
        return null;
    }

    public static String generateAnimatorNoSync(String imgPath, String moviePath, File filePath) {
        File referenceImageFile = new File(imgPath);
        File motionSequenceFile = new File(moviePath);

        RequestBody referenceImageBody = RequestBody.create(referenceImageFile, MEDIA_TYPE_JPEG);
        RequestBody motionSequenceBody = RequestBody.create(motionSequenceFile, MEDIA_TYPE_MP4);

        MultipartBody.Part referenceImagePart = MultipartBody.Part.createFormData("reference_image", referenceImageFile.getName(), referenceImageBody);
        MultipartBody.Part motionSequencePart = MultipartBody.Part.createFormData("motion_sequence", motionSequenceFile.getName(), motionSequenceBody);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(referenceImagePart)
                .addPart(motionSequencePart)
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://139.224.214.59:7861/animate/").newBuilder();
        urlBuilder.addQueryParameter("seed", "1");
        urlBuilder.addQueryParameter("steps", "25");
        urlBuilder.addQueryParameter("guidance_scale", "7.5");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorMessage = response.body().string();
                throw new IOException("Unexpected code " + response.code() + ": " + errorMessage);
            }

            InputStream inputStream = response.body().byteStream();
            byte[] bytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                bytes = inputStream.readAllBytes();
            }


            String fileName = generateTimestampedFileName("gif");
            File outputFile = new File(filePath, fileName);
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                outputStream.write(bytes);
            }
            Log.d("lily", "GIF image saved to: " + outputFile.getAbsolutePath());
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        File referenceImageFile = new File("D:\\Java\\shit\\src\\main\\resources\\luobowen.jpg");
        File motionSequenceFile = new File("D:\\Java\\shit\\src\\main\\resources\\com.mp4");

        RequestBody referenceImageBody = RequestBody.create(referenceImageFile, MEDIA_TYPE_JPEG);
        RequestBody motionSequenceBody = RequestBody.create(motionSequenceFile, MEDIA_TYPE_MP4);

        MultipartBody.Part referenceImagePart = MultipartBody.Part.createFormData("reference_image", referenceImageFile.getName(), referenceImageBody);
        MultipartBody.Part motionSequencePart = MultipartBody.Part.createFormData("motion_sequence", motionSequenceFile.getName(), motionSequenceBody);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(referenceImagePart)
                .addPart(motionSequencePart)
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://139.224.214.59:7861/animate/").newBuilder();
        urlBuilder.addQueryParameter("seed", "1");
        urlBuilder.addQueryParameter("steps", "25");
        urlBuilder.addQueryParameter("guidance_scale", "7.5");
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        CompletableFuture<File> future = CompletableFuture.supplyAsync(() -> {
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorMessage = response.body().string();
                    throw new IOException("Unexpected code " + response.code() + ": " + errorMessage);
                }

                InputStream inputStream = response.body().byteStream();
                byte[] bytes = new byte[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    bytes = inputStream.readAllBytes();
                }

                String fileName = generateTimestampedFileName("gif");
                File outputFile = new File(fileName);
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    outputStream.write(bytes);
                }

                System.out.println("GIF image saved to: " + outputFile.getAbsolutePath());
                return outputFile;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        future.whenComplete((outputFile, throwable) -> {
            if (throwable != null) {
                return;
            }
        });

        // 等待异步任务执行完毕
        future.join();
    }
}

