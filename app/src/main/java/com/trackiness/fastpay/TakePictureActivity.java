package com.trackiness.fastpay;

import static androidx.camera.core.CameraX.getContext;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyMultipartRequest;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class TakePictureActivity extends AppCompatActivity implements View.OnClickListener,ImageAnalysis.Analyzer {
    private static final String TAG = "verify";
    private ListenableFuture<ProcessCameraProvider> cameraProvider;

    private PreviewView previewView;
    private ImageCapture capture;
    private ImageButton btn;
    private ImageButton confirm;
    private ImageButton cancel;
    private Bitmap bitmap;
    Loader loader;
    RequestQueue queue;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takepicture);
        getSupportActionBar().hide();
        Utility.authMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btn = (ImageButton) findViewById(R.id.capture);
        previewView = (PreviewView)findViewById(R.id.previewView);
        cameraProvider = ProcessCameraProvider.getInstance(this);
        confirm = ((ImageButton)findViewById(R.id.confirm));
        cancel = ((ImageButton)findViewById(R.id.cancelPicture));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                take();
            }
        });
        loader = new Loader(getActivity(), R.drawable.loaderblue);
        queue = Volley.newRequestQueue(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();

            }
        });
        confirm.setOnClickListener(this);
        cameraProvider.addListener(() -> {
            try {
                ProcessCameraProvider cameraProviderProc = cameraProvider.get();
                startService(cameraProviderProc);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, getExecutor());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(queue!=null)
            queue.cancelAll(TAG);
    }

    @Override
    public void onClick(View v) {
        if(bitmap!=null) {
            String url = getString(R.string.server)+"/account/verify";
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//                            Toast.makeText(TakePicture.this, response.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    String body;
//                    //get status code here
//                    String statusCode = String.valueOf(error.networkResponse.statusCode);
//                    //get response body and parse with appropriate encoding
//                    if(error.networkResponse.data!=null) {
//                        try {
//                            body = new String(error.networkResponse.data,"UTF-8");
//                            Toast.makeText(TakePicture.this, body, Toast.LENGTH_SHORT).show();
//                            Map data = new Gson().fromJson(body,Map.class);
//
//                            new Alert(TakePicture.this,Alert.ERROR)
//                                    .setContentText(data.get("error").toString())
//                                    .show();
//
//                        } catch (UnsupportedEncodingException e) {
//
//                        }
//                    }
//                    }
//            }){
//                @Nullable
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    return super.getParams();
//                }
//            };
            loader.show();
            startRequestCallBack();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            stopRequestCallBack();
                            if(loader.isShowing())
                                loader.dismiss();
                            try {
                                JSONObject data = new JSONObject(new String(response.data));
                                new Alert(TakePictureActivity.this,Alert.SUCCESS)
                                    .setContentText(data.getString("done"))
                                    .show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            stopRequestCallBack();
                            if(loader.isShowing())
                                loader.dismiss();
                            String body;
                            System.out.println(error);
                            if(error.networkResponse!=null&&error.networkResponse.data!=null) {
                                try {
                                    body = new String(error.networkResponse.data,"UTF-8");
                                    Map data = new Gson().fromJson(body,Map.class);

                                    new Alert(TakePictureActivity.this,Alert.ERROR)
                                            .setContentText(data.get("error").toString())
                                            .show();

                                } catch (UnsupportedEncodingException e) {

                                }
                            }
                        }

                    }) {


                @Override
                protected Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();
                    long imagename = System.currentTimeMillis();
                    params.put("id", new DataPart(imagename + ".png", Utility.getFileDataFromDrawable(bitmap)));
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer "+ Utility.extractToken(TakePictureActivity.this));
                    return headers;
                }
            };
            volleyMultipartRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 500000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 500000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {

                }
            });
            volleyMultipartRequest.setTag(TAG);
            queue.add(volleyMultipartRequest);
        }
        else
        {

        }
    }



    @SuppressLint("RestrictedApi")
    private void startService(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();
        Preview preview = new Preview.Builder()
                .build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        capture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        imageAnalysis.setAnalyzer(getExecutor(), this);
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, capture);
    }

    @Override
    public void analyze(@NonNull ImageProxy image) {
        System.out.println("analyzing");
        image.close();
    }

    @SuppressLint("RestrictedApi")
    private void capturePhoto() {
         File photoFile = new File(getContext().getExternalCacheDir() + File.separator + System.currentTimeMillis() + ".png");
        capture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        btn.setEnabled(false);
                        String filePath = photoFile.getPath();
                        bitmap = BitmapFactory.decodeFile(filePath);
                        photoFile.delete();
                        taken();
                        btn.setEnabled(true);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(TakePictureActivity.this, "Error saving photo: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );

    }

    private void startRequestCallBack()
    {
        btn.setEnabled(false);
        confirm.setEnabled(false);
    }

    private void stopRequestCallBack()
    {
        btn.setEnabled(true);
        confirm.setEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    private void taken() {
        new AsyncTask<Void, Void, Void>() {

            @SuppressLint("StaticFieldLeak")
            @Override
            protected Void doInBackground(Void... voids) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                System.out.println(bitmap.getByteCount());
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
                bitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()),null,options);
                System.out.println(bitmap.getByteCount());
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                if(loader.isShowing())
                    loader.dismiss();
                ((ImageView) findViewById(R.id.previews)).setImageBitmap(bitmap);
                cancel.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loader.show();
                ((ImageView) findViewById(R.id.previews)).setVisibility(View.VISIBLE);
                previewView.setVisibility(View.INVISIBLE);
                confirm.setVisibility(View.VISIBLE);
                btn.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private Context getActivity() {
        return TakePictureActivity.this;
    }

    private void take()
    {
        ((ImageView)findViewById(R.id.previews)).setVisibility(View.INVISIBLE);
        ((ImageView)findViewById(R.id.previews)).setImageBitmap(null);
        previewView.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.INVISIBLE);
        btn.setVisibility(View.VISIBLE);
        cancel.setVisibility(View.INVISIBLE);
    }

    Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }
}
