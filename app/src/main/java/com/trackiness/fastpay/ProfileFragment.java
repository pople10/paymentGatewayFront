package com.trackiness.fastpay;


import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.ProfileService;
import com.trackiness.services.UserService;
import com.trackiness.services.models.Profile;
import com.trackiness.utility.CallBack;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;
import com.trackiness.utility.VolleyMultipartRequest;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class ProfileFragment extends Fragment {

    private static final String TAG = "profileUpdate";
    LinearLayout ResetPassword,Logout;
    LinearLayout UpdateDetails;
    LinearLayout Balances;
    LinearLayout AddBalance;
    LinearLayout verify;
    LinearLayout crypto;
    LinearLayout qrwallet;
    LinearLayout help;
    View root;
    RequestQueue queue;
    ProfileService profileService;
    UserService userService;
    Loader loader;
    Profile profile;
    ImageView photoContainer;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;


    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_profile, container,false);
        queue = Volley.newRequestQueue(root.getContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        root.getContext().registerReceiver(br, filter);
        Utility.authMiddleware(root.getContext());
        init(root);
        setProfile();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void init(View root){
        profileService = new ProfileService(root.getContext());
        userService = new UserService(root.getContext());
        loader = new Loader(root.getContext());
        ResetPassword = root.findViewById(R.id.reset_password);
        UpdateDetails = root.findViewById(R.id.update_details);
        Balances = root.findViewById(R.id.balances);
        AddBalance = root.findViewById(R.id.add_balance);
        verify = root.findViewById(R.id.verifyNav);
        crypto = root.findViewById(R.id.walletNav);
        qrwallet = root.findViewById(R.id.walletQRNav);
        Logout = root.findViewById(R.id.logout);
        help = root.findViewById(R.id.helpNav);
        photoContainer = ((ImageView)root.findViewById(R.id.photoProfile));
        ResetPassword.setOnClickListener(e->{
          startActivity(new Intent(getContext(),ResetPasswordActivity.class));
        });
        help.setOnClickListener(e->{
            startActivity(new Intent(getContext(),ContactUsActivity.class));
        });
        Logout.setOnClickListener(e->{
            unsubscribeFireBase(Utility.extractToken(root.getContext()));
            logout(Utility.extractToken(root.getContext()));
            Utility.removeToken(getContext());
            startActivity(new Intent(getActivity(),FirstPage.class));
            getActivity().finish();
        });
        UpdateDetails.setOnClickListener(e->{
            startActivity(new Intent(getContext(),UpdateDetailsMainActivity.class));
        });
        AddBalance.setOnClickListener(e->{
            startActivity(new Intent(getContext(),NewBalanceActivity.class));
        });
        Balances.setOnClickListener(e->{
            startActivity(new Intent(getContext(), BalanceManagementActivity.class));
        });
        crypto.setOnClickListener(e->{
            startActivity(new Intent(getContext(), CryptoActivity.class));
        });
        qrwallet.setOnClickListener(e->{
            startActivity(new Intent(getContext(), WalletQRActivity.class));
        });
        verify.setOnClickListener(e->{
            if(profile.getVerified().equals("1")) {
                new Alert(root.getContext(), Alert.WARNING)
                        .setContentText("Your are already verified!")
                        .setConfirmText("Continue")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                startActivity(new Intent(getContext(), VerifyActivity.class));
                            }
                        })
                        .setCancelText("No")
                        .show();
                return;
            }
            startActivity(new Intent(getContext(), VerifyActivity.class));
        });
        photoContainer.setOnClickListener(e->{
            if (root.getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            }
            else
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
    }

    private void logout(String extractToken) {{
        (new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                userService.logout(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        System.out.println("logout");
                    }

                    @Override
                    public void onError(int error) {
                        System.out.println("error logout "+error);
                    }

                    @Override
                    public void beforeSend() {
                        System.out.println("before logout");
                    }

                    @Override
                    public void onFinish() {

                    }
                },extractToken);
                return null;
            }
        }).execute();
    }
    }

    private void unsubscribeFireBase(String token) {
        (new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {
                userService.fireBaseUnsubscribe(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        System.out.println("removed token");
                    }

                    @Override
                    public void onError(int error) {
                        System.out.println("unremoved token "+error);
                    }

                    @Override
                    public void beforeSend() {
                        System.out.println("before removed token");
                    }

                    @Override
                    public void onFinish() {

                    }
                },token);
                return null;
            }
        }).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent,CAMERA_REQUEST);
            }
            else
            {
                new Alert(root.getContext(),Alert.WARNING)
                        .setContentText("You need to allow us access your camera")
                        .show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            photoContainer.setImageBitmap(photo);
            String url = getString(R.string.server)+"/account/profile/update/photo";
            loader.show();
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            if(loader.isShowing())
                                loader.dismiss();
                            try {
                                JSONObject data = new JSONObject(new String(response.data));
                                new Alert(root.getContext(),Alert.SUCCESS)
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
                            if(loader.isShowing())
                                loader.dismiss();
                            String body;
                            int statusCode = error.networkResponse.statusCode;
                            if(error.networkResponse.data!=null&&(statusCode==422
                                    ||statusCode == 401||statusCode == 404
                                    ||statusCode==433)) {
                                if(statusCode==401)
                                {
                                    //code here
                                }
                                try {
                                    body = new String(error.networkResponse.data,"UTF-8");
                                    Map data = new Gson().fromJson(body,Map.class);
                                    new Alert(root.getContext(),Alert.ERROR)
                                            .setContentText(data.get("error").toString())
                                            .show();
                                } catch (UnsupportedEncodingException e) {

                                }
                            }
                            else {
                                new Alert(root.getContext(),Alert.ERROR)
                                        .setContentText(root.getContext().getString(R.string.internal_error))
                                        .show();
                            }
                        }

                    }) {

                        @Override
                        protected Map<String, DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            long imagename = System.currentTimeMillis();
                            params.put("photo", new DataPart(imagename + ".png", Utility.getFileDataFromDrawable(photo)));
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Bearer "+ Utility.extractToken(getContext()));
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
    }

    public void setProfile()
    {
        profileService.getProfileData(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                profile = profileService.getProfile();
                ((TextView)root.findViewById(R.id.fullNameProfile)).setText(profile.generateFullname());
                ((TextView)root.findViewById(R.id.userNameProfile)).setText("@"+profile.getUsername());
                String accType = profile.getTypeAccount();
                ((TextView)root.findViewById(R.id.accTypeProfile)).setText(
                        accType+" Account"
                                +(accType.equals("Business")?"\nCompany Name: "+profile.getCompany():"")
                );
                if(profile.getVerified().equals("1"))
                    ((ImageView)root.findViewById(R.id.verifiedBadge)).setImageDrawable(
                            getResources().getDrawable(R.drawable.verified)
                    );
                (new AsyncTask<String, Void, Bitmap>()
                {
                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        photoContainer.setImageBitmap(bitmap);
                    }

                    @Override
                    protected Bitmap doInBackground(String... strings) {
                        String imageURL=strings[0];
                        return Utility.getImageBitmapFromURL(imageURL);
                    }
                }).execute(profile.getPhotoUrl());
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void beforeSend() {
                loader.show();
            }

            @Override
            public void onFinish() {
                if(loader.isShowing())
                    loader.dismiss();
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}