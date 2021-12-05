package com.trackiness.fastpay;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.TransactionService;
import com.trackiness.services.models.Profile;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.trackiness.services.BalanceService;
import com.trackiness.services.ProfileService;
import com.trackiness.services.models.Balance;
import com.trackiness.services.models.TransactionData;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    LinearLayout FirstContainer;
    ViewTreeObserver vieTree;
    BottomSheetBehavior bottomSheetBehavior;
    List<Balance> balancesData = new ArrayList<Balance>();
    RelativeLayout BalanceList ;
    BalanceService balanceService;
    ProfileService profileService;
    View root;
    TransactionService transactionService;
    LinearLayout loader;
    CoordinatorLayout bottom_sh;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_homef, container,false);
        FirstContainer = (LinearLayout) root.findViewById(R.id.FirstContainer);
        transactionService = new TransactionService(getContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        root.getContext().registerReceiver(br, filter);
        Utility.authMiddleware(root.getContext());
        LinearLayout linearLayoutBSheet = (LinearLayout) root.findViewById(R.id.bottomSheet);
        bottomSheetBehavior  = BottomSheetBehavior.from(linearLayoutBSheet);
        BalanceList = root.findViewById(R.id.balanceMapContaner);
        balanceService = new BalanceService(getContext());
        profileService = new ProfileService(getContext());
        loader =((LinearLayout) root.findViewById(R.id.loader));
        bottom_sh =( root.findViewById(R.id.bottom_sh));

        (root.findViewById(R.id.transfer_button)).setOnClickListener(e->{
            startActivity(new Intent(getContext(), TransferActivity.class));
        });
        (root.findViewById(R.id.payNavigate)).setOnClickListener(e->{
            startActivity(new Intent(getContext(),QRCodeDisplayActivity.class));
        });
        changeBalance(root);


        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setBalancesDatos();
    }

    private void setBalancesDatos()
    {
        try {
                    balanceService.getBalances(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                if(balanceService!=null) {
                    List<Balance> tmp = balanceService.getBalancesData();
                    if (tmp != null)
                        balancesData = tmp;
                    balancesElements(root);
                }
            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void beforeSend() {
                showIndicator();
            }

            @Override
            public void onFinish() {
            }
        });

        }catch (Exception e){}
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vieTree = FirstContainer.getViewTreeObserver();
        // LinearLayout buttonsC = (LinearLayout) view.findViewById(R.id.buttons);
        LinearLayout TEContainer = (LinearLayout) view.findViewById(R.id.teContainer);


        vieTree.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int x = view.getHeight() - FirstContainer.getHeight();
                if(x>0) {
                    bottomSheetBehavior.setPeekHeight((x));
                    //buttonsC.setVisibility(View.VISIBLE);
                }
                else {
                    bottomSheetBehavior.setPeekHeight((int) (view.getWidth()/12.5+10));
                    //buttonsC.setVisibility(View.GONE);
                }
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        ((Button) view.findViewById(R.id.transactionHistory)).setOnClickListener(e->{startActivity(new Intent(getActivity(), AllTransactionsActivity.class));});
    }


    @Override
    public void onStart() {
        super.onStart();
        profileService.getProfileData(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                try {
                    Profile profile = profileService.getProfile();
                    ((TextView)root.findViewById(R.id.homeProfileName)).setText(
                            profile.getLastname().toUpperCase()+" "+ profile.getFirstname()+" \uD83D\uDC4B");
                }catch (Exception e){}
              }

            @Override
            public void onError(int error) {

            }

            @Override
            public void beforeSend() {
            }

            @Override
            public void onFinish() {

            }
        });
        transactionService.getTransaction(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                try {
                    LinearLayout TEContainer = (LinearLayout) root.findViewById(R.id.teContainer);
                    List<TransactionData> ts = transactionService.getTransactions().getData();
                    TEContainer.removeAllViews();
                    for(TransactionData td :ts){
                        CreateTransaction(TEContainer,new TransactionElementData(td.getIdPayment(),td.getName(),td.getStringDate(),td.generateLabel(),td.getBalance(),td.getTypeT()));
                    }
                    hideIndicator();
                }catch (Exception e){}
            }
            @Override
            public void onError(int error) {

            }

            @Override
            public void beforeSend() {

            }

            @Override
            public void onFinish() {

            }
        });
    }

    public  void CreateTransaction(LinearLayout TEContainer, TransactionElementData ted){
        LinearLayout TE = (LinearLayout) getLayoutInflater().inflate(R.layout.element_transaction,null);
        ((TextView) TE.findViewById(R.id.FullName)).setText(ted.fullName);
        ((TextView) TE.findViewById(R.id.date)).setText(ted.dateofTransaction);
        ((TextView) TE.findViewById(R.id.balance)).setText(ted.Balance);

        MaterialButton bt = TE.findViewById(R.id.button_transaction);
        TextView amount =((TextView) TE.findViewById(R.id.amount));
        int am = ted.type;
        if(am>0){
            amount.setText("+ "+ted.amount);
        }
        else if(am<0) {
            amount.setText("- "+ted.amount);
            bt.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.danger)));
            bt.setBackgroundColor(getResources().getColor(R.color.dangerLight));
            bt.setRotation(180);
        }
        else{
            amount.setText(ted.amount);
            bt.setIconTint(ColorStateList.valueOf(getResources().getColor(R.color.TextRegisterG)));
            bt.setBackgroundColor(getResources().getColor(R.color.grayLight));
            bt.setIcon(getResources().getDrawable(R.drawable.ic_baseline_remove_24));
        }
        MaterialCardView mcv = (MaterialCardView) TE.getChildAt(0);
        mcv.setPadding(0,0,0,0);
        OnclickElement(mcv, ted.id);
        TEContainer.addView(TE);
    }

    public void OnclickElement(MaterialCardView c,String id){
        c.setOnClickListener(e->{
            Intent in = new Intent(getActivity(), TransactionActivity.class);
            in.putExtra("id",id+"");
            startActivity(in);
        });
    }

    public void changeBalance(View v){
        Chip ch = v.findViewById(R.id.changeBalance);
        ch.setOnClickListener(e->{
            BalanceList.setVisibility(View.VISIBLE);
        });
    }
    public static class TransactionElementData {
        String  id;
        String fullName;
        String dateofTransaction;
        String amount;
        String Balance;
        int type;
        public TransactionElementData(String id,String fullName, String dateofTransaction, String amount, String balance,int type) {
            this.id = id;
            this.fullName = fullName;
            this.dateofTransaction = dateofTransaction;
            this.amount = amount;
            Balance = balance;
            this.type = type;
        }
    }
    public void balancesElements(View v){
        LinearLayout BlanacesContainer = v.findViewById(R.id.balanceelementContainer);
        BlanacesContainer.removeAllViews();
        Chip ch = v.findViewById(R.id.changeBalance);
        TextView bam = v.findViewById(R.id.balance_amount);
        bam.setText(balancesData.get(0).generateLabel());
        ch.setText(balancesData.get(0).getLabel());
        for(Balance i : balancesData) {
            String k = i.getLabel();
            String total = i.generateLabel();
            CardView TE = (CardView) getLayoutInflater().inflate(R.layout.balance_select_element,null);
            ((TextView) TE.findViewById(R.id.label_b)).setText(k);
            TE.setOnClickListener(e->{
                bam.setText(total);
                ch.setText(k);
                BalanceList.setVisibility(View.GONE);
            });
            BlanacesContainer.addView(TE);
        }
    }
    public void  showIndicator(){
        loader.setVisibility(View.VISIBLE);
        FirstContainer.setVisibility(View.INVISIBLE);
        bottom_sh.setVisibility(View.INVISIBLE);
    }
    public void hideIndicator(){
        loader.setVisibility(View.GONE);
        FirstContainer.setVisibility(View.VISIBLE);
        bottom_sh.setVisibility(View.VISIBLE);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        balanceService=null;
        profileService = null;
    }
}