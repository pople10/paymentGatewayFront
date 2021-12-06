package com.trackiness.fastpay;

import android.content.Context;
import android.content.Intent;

import com.trackiness.utility.Utility;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Alert extends SweetAlertDialog {
    public static int WARNING = SweetAlertDialog.WARNING_TYPE;
    public static int ERROR = SweetAlertDialog.ERROR_TYPE;
    public static int SUCCESS = SweetAlertDialog.SUCCESS_TYPE;

    public Alert(Context context) {
        super(context);
        super.setTitle("Information");
    }

    public Alert(Context context, int alertType) {
        super(context, alertType);
        if(super.getAlerType()==WARNING)
            super.setTitle("Warning");
        if(super.getAlerType()==ERROR)
            super.setTitle("Error");
        if(super.getAlerType()==SUCCESS)
            super.setTitle("Success");
    }

    @Override
    public void show() {
        if(super.getAlerType()==Alert.ERROR&&super.getContentText()!=null&&super.getContentText().equals(getContext().getString(R.string.internal_error)))
        {
            setConfirmClickListener(new OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    getContext().startActivity(new Intent(getContext(),ContactUsActivity.class));
                    sweetAlertDialog.dismiss();
                }
            });
        }
        if(super.getAlerType()==Alert.ERROR&&super.getContentText()!=null&&super.getContentText().equals("Your account is not verified yet"))
        {
            setCancelText("Later");
            setConfirmText("Verify");
            setConfirmClickListener(new OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    getContext().startActivity(new Intent(getContext(),VerifyActivity.class));
                    sweetAlertDialog.dismiss();
                }
            });
        }
        if(super.getAlerType()==Alert.ERROR&&super.getContentText()!=null
                &&(super.getContentText().equals("Unauthentificated")||
                super.getContentText().equals("Your account is deleted")||
                super.getContentText().equals("Your account is disabled")))
        {
            setConfirmText("Logout");
            setCancelable(false);
            setConfirmClickListener(new OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    Utility.removeToken(getContext());
                    getContext().startActivity(new Intent(getContext(),LoginActivity.class));
                    sweetAlertDialog.dismiss();
                }
            });
        }
        super.show();
    }
}
