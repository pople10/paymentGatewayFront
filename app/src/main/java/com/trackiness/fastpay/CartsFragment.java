package com.trackiness.fastpay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import  androidx.biometric.BiometricManager.Authenticators;
import  androidx.biometric.BiometricManager;


import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.trackiness.fastpay.databinding.FragmentCartsBinding;
import com.trackiness.services.CreditCardService;
import com.trackiness.services.models.Balance;
import com.trackiness.services.models.CreditCard;
import com.trackiness.utility.VolleyCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
public class CartsFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {

    List<CreditCard> list_creditCard = new ArrayList<>();
    private FragmentCartsBinding binding;
    private boolean test = false;
    Loader loader;

    //for fingerprint athentification
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    CardFrontAdapter frontAdapter;
    CardFrontFragment front;
    CardBackFragment back;
    boolean front_or_back = true;
    List<Boolean> show_hide = new ArrayList<>();
    //for slide view
    ViewPager2 viewPager2;
    static int c = 0;
    CreditCardService cardService;
    private boolean toggle = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        loader = new Loader(getActivity());
        binding = FragmentCartsBinding.inflate(inflater, container,false);
        // ((Button) root.findViewById(R.id.button)).setOnClickListener(e->{startActivity(new Intent(getActivity(), Register.class));});
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        executor = ContextCompat.getMainExecutor(this.getActivity());
        biometricAuthentification();


        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();


        front = new CardFrontFragment();
        back = new CardBackFragment();


        int limit = retrieveData();


/*        if(list_creditCard.size() < 1){
            limit = 2;
        }*/




        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                int postion = binding.pager.getCurrentItem();
                if(c != 0){
                    if(!show_hide.get(postion)){
                        binding.btnShow.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
                    }
                    else{
                        binding.btnShow.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_remove_red_eye_24));
                    }
                }



            }
        });

        binding.btnDelete.setOnClickListener((event)->{
            if(list_creditCard.size() > 0){
                int position = binding.pager.getCurrentItem();
                cardService.deleteVCC(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        list_creditCard.remove(position);
                        frontAdapter.removeFragment(position);
                        frontAdapter.notifyDataSetChanged();
                        retrieveData();
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
                },list_creditCard.get(position).getNormalNumber());
            }
        });

        binding.btnShow.setOnClickListener((event)->{
            c++;
            int position = binding.pager.getCurrentItem();
            binding.btnShow.setClickable(false);
            if(!show_hide.get(position)){
                biometricPrompt.authenticate(promptInfo);
                show_hide.set(position, true);
                binding.btnShow.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_visibility_off_24));
            }else{
                show_hide.set(position, false);
                binding.btnShow.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_remove_red_eye_24));
                frontAdapter.showInfo(list_creditCard.get(position), position, !show_hide.get(position));
            }
            //int id = (int)frontAdapter.getItemId(binding.pager.getCurrentItem());
            if(list_creditCard.size() > 0 && c == 1){
                //frontAdapter.showInfo(list_creditCard.get(position), position, !show_hide.get(position));
            }
            binding.btnShow.setClickable(true);
        });

        binding.btnAdd.setOnClickListener((event)->{
            System.out.println("adding vcc");
                cardService.createCreditCard(new VolleyCallBack() {
                    @Override
                    public void onSuccess() {
                        CreditCard vcc = cardService.getVCC();
                        retrieveData();
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
        });

        binding.btnNext.setOnClickListener((event)->{
            int position = binding.pager.getCurrentItem();
            int size = frontAdapter.replaceFragment(position);

            if(toggle)
                binding.btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_upward_24));
            else
                binding.btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_arrow_downward_24));
            toggle=!toggle;
            frontAdapter.notifyDataSetChanged();


        });

    }


    @Override
    public void onResume() {
        super.onResume();
        //retrieveData();
    }

    public int retrieveData(){

        cardService = new CreditCardService(this.getContext());
        cardService.getCreditCards(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                binding.creditCardEmpty.setVisibility(View.GONE);
                binding.creditcards.setVisibility(View.VISIBLE);
                list_creditCard = cardService.getCreditCardsData();

                frontAdapter = new CardFrontAdapter(getActivity());
                int size = list_creditCard.size();
                binding.pager.setOffscreenPageLimit(size);
                for(int i=0; i<size; ++i){
                    frontAdapter.createFragment(i);
                    show_hide.add(false);
                }
                binding.pager.setAdapter(frontAdapter);

                binding.btnNext.setVisibility(View.VISIBLE);
                binding.btnShow.setVisibility(View.VISIBLE);
                binding.btnDelete.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(int error) {
                if(error == 404){
                    binding.creditCardEmpty.setVisibility(View.VISIBLE);
                    binding.btnNext.setVisibility(View.GONE);
                    binding.btnShow.setVisibility(View.GONE);
                    binding.btnDelete.setVisibility(View.GONE);
                }
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
        return list_creditCard.size();
    }


    public void showInfo(String cardNumber, int vcc, String expDate, Fragment front, Fragment back){
        Bundle bundle = new Bundle();
        bundle.putString("name", cardNumber);
        bundle.putInt("vcc", vcc);
        bundle.putString("expDate", expDate);

        getActivity().getSupportFragmentManager().beginTransaction().
                add(R.id.fragment_container, CardFrontFragment.class, bundle)
                .commit();
    }


    @Override
    public void onBackStackChanged() {
        if(binding.pager.getCurrentItem() == 0){
            //TODO
        }else{
            binding.pager.setCurrentItem(binding.pager.getCurrentItem()-1);
        }
    }

    public void biometricAuthentification(){
        BiometricManager biometricManager = BiometricManager.from(this.getActivity());
        switch (biometricManager.canAuthenticate(Authenticators.BIOMETRIC_STRONG | Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
/*                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        Authenticators.BIOMETRIC_STRONG | Authenticators.DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);*/

                break;
        }



        biometricPrompt = new BiometricPrompt(getActivity(),
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                int position = binding.pager.getCurrentItem();
                frontAdapter.showInfo(list_creditCard.get(position), position, !show_hide.get(position));

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getActivity().getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}