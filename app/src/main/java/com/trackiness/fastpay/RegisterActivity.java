package com.trackiness.fastpay;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.trackiness.broadcastReceiver.NetworkBroadCastReceiver;
import com.trackiness.services.UserService;
import com.trackiness.services.models.User;
import com.trackiness.utility.Utility;
import com.trackiness.utility.VolleyCallBack;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    public int page = 1;
    private RequestQueue mRequestQueue;
    Map data;
    int typeV=1;
    EditText fname;
    EditText lname;
    EditText phone;
    EditText email;
    EditText uname;
    EditText cin;
    EditText birthday;
    AutoCompleteTextView type;
    AutoCompleteTextView title;
    EditText company;
    EditText password;
    int typeAccount = 0;

    UserService userService ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        Utility.loginMiddleware(getApplicationContext());
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver br = new NetworkBroadCastReceiver();
        getApplicationContext().registerReceiver(br, filter);
        final Handler h = new Handler(Looper.getMainLooper());


        userService = new UserService(RegisterActivity.this);
        userService.setTAG("Register");
        mRequestQueue = Volley.newRequestQueue(this);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {init();}}, 2200);
    }

    public  void init(){
        fname = (EditText) findViewById(R.id.fname);
        lname = (EditText) findViewById(R.id.lname);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        uname = (EditText) findViewById(R.id.uname);
        cin = (EditText) findViewById(R.id.cin);
        birthday = (EditText) findViewById(R.id.birthday);
        type = (AutoCompleteTextView) findViewById(R.id.type);
        company = (EditText) findViewById(R.id.company);
        password = (EditText) findViewById(R.id.password);
        title = (AutoCompleteTextView) findViewById(R.id.title);

        //Containers
        TextInputLayout fnameC = (TextInputLayout) findViewById(R.id.fnamecontainer);
        TextInputLayout lnameC = (TextInputLayout) findViewById(R.id.lnamecontainer);
        TextInputLayout emailC = (TextInputLayout) findViewById(R.id.emailcontainer);
        TextInputLayout phoneC = (TextInputLayout) findViewById(R.id.phonecontainer);
        TextInputLayout unameC = (TextInputLayout) findViewById(R.id.unamecontainer);
        TextInputLayout cinC = (TextInputLayout) findViewById(R.id.cincontainer);
        TextInputLayout birthdayC = (TextInputLayout) findViewById(R.id.birthdaycontainer);
        TextInputLayout typeC = (TextInputLayout) findViewById(R.id.typecontainer);
        TextInputLayout titleC = (TextInputLayout) findViewById(R.id.titlecontainer);
        TextInputLayout companyC = (TextInputLayout) findViewById(R.id.companycontainer);
        TextInputLayout passwordC = (TextInputLayout) findViewById(R.id.passwordcontainer);
        TextInputLayout[] Containers = {fnameC, lnameC, emailC, phoneC,titleC, unameC, cinC, birthdayC, typeC, companyC, passwordC};
        companyC.setVisibility(View.GONE);
        //Button
        Button save = (Button) findViewById(R.id.register);
        Button cancel = (Button) findViewById(R.id.cancel);
        onClickSave(save, cancel, Containers);
        onClickCancel(save, cancel, Containers);
        birthdayInput(birthday);
        typeAccount(type);
        hideIndicator();
        typeTitle(title);
        title.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { title.setError(null);}
        });
        type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                type.setError(null);
                companyC.setVisibility(View.GONE);
                typeV = i+1;
                if(i==1){
                    typeAccount =1;
                    companyC.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    public void onClickSave(Button save,Button cancel,TextInputLayout[] Containers){
        save.setOnClickListener(e->{
            hidetKeyboard();
            switch (page){
                case 1 :
                    if(!validatePage1()) break;
                    gotoPage(++page,save,cancel,Containers);
                    break;
                case 2 :
                    if(!validatePage2()) break;
                    gotoPage(++page,save,cancel,Containers);
                    break;
                case 3 :
                    if(!validatePage3()) break;
                    sendRequest();
                    break;
            }
        });
    }
    public String getValueE(EditText e){
        return e.getText().toString();
    }

    private void sendRequest() {
        showIndicator();
        userService.setUser(new User(getValueE(fname),getValueE(lname),getValueE(uname),getValueE(phone),title.getText().toString(),getValueE(cin),getValueE(company),"",getValueE(email),getValueE(birthday),getValueE(password),typeV+"","00:0:0:0","1"));
        userService.Register(new VolleyCallBack() {
            @Override
            public void onSuccess() {
                System.out.println("Done");
                (new Handler(Looper.getMainLooper())).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                },1000);
            }

            @Override
            public void onError(int error) {
                hideIndicator();
                System.out.println("Error : "+error);
            }

            @Override
            public void beforeSend() {

            }

            @Override
            public void onFinish() {
                hideIndicator();
            }
        });

    }

    public boolean validatePage1(){
        boolean f = true;
        if(!ValidateName(fname)) {fname.setError("Invalid First name");f=false;}
        if(!ValidateName(lname)) {lname.setError("Invalid Last name");f=false;}
        if(!Emailvalidation(email.getText().toString())) {email.setError("Invalid Email");f=false;}
        if(!Phonevalidation(phone.getText().toString())) {phone.setError("Invalid phone number");f=false;}
        if(title.getText().toString().length()<2) {title.setError("Invalid title");f=false;}
        return f;
    }
    public boolean validatePage2(){
        boolean f = true;
        if(uname.getText().toString().length()<2) {uname.setError("Invalid user name");f=false;}
        if(cin.getText().toString().length()<4) {cin.setError("Invalid CIN");f=false;}
        if((birthday.getText().toString()).equals("")) {birthday.setError("Invalid date of birthday");f=false;}
        return f;
    }
    public boolean validatePage3(){
        boolean f = true;
        if(typeAccount == 1 && company.getText().toString().length()<3) {company.setError("Invalid company name");f=false;}
        if(type.getText().toString().length()<2) {type.setError("Invalid Type");f=false;}
        if(!Passwordvalidation(password.getText().toString())) {password.setError("Invalid password");f=false;}
        return f;
    }
    public boolean ValidateName(EditText e){
        String s = e.getText().toString();
        if(s.length()<2) return false;
        Pattern pattern = Pattern.compile("^[a-z A-Z]+$");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
    public boolean Emailvalidation(String email){
        Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public boolean Phonevalidation(String s){
        if(s.length()<8) return false;
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(s);
        return matcher.matches();
    }
    public boolean Passwordvalidation(String e){
       return e.length()>7;
    }
    public void onClickCancel(Button save,Button cancel,TextInputLayout[] Containers){
        cancel.setOnClickListener(e->{
            switch (page){
                case 1 :
                    finish();
                    break;
                case 2 :
                    gotoPage(--page,save,cancel,Containers);
                    break;
                case 3 :
                    gotoPage(--page,save,cancel,Containers);
                    break;
            }
        });
    }
    public void gotoPage(int d,Button save,Button cancel,TextInputLayout[] Containers){
        switch (d){
            case 1 :
                showContainer(-1,5,Containers);
                save.setText("Next");
                cancel.setText("Cancel");
                break;
            case 2 :
                showContainer(4,8,Containers);
                save.setText("Next");
                cancel.setText("Previous");
                break;
            case 3 :
                showContainer(7,Containers.length,Containers);
                save.setText("Register");
                cancel.setText("Previous");
                break;
        }
    }
    public void showContainer(int start,int end,TextInputLayout[] Containers){
        for(int i =0; i<Containers.length;i++){
            if(start<i && i<end)  Containers[i].setVisibility(View.VISIBLE);
            else  Containers[i].setVisibility(View.GONE);
        }
    }
    public void birthdayInput(EditText edittext){

        Calendar Cal = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Cal.set(Calendar.YEAR, year);
                Cal.set(Calendar.MONTH, month);
                Cal.set(Calendar.DAY_OF_MONTH, day);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                edittext.setText(sdf.format(Cal.getTime()));
            }
        };
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edittext.setError(null);
                new DatePickerDialog(RegisterActivity.this, date, Cal
                        .get(Calendar.YEAR), Cal.get(Calendar.MONTH),
                        Cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
    public void typeAccount(AutoCompleteTextView textView){
        String[] data = new String[] {"Personal", "Business"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, data);
        textView.setAdapter(adapter);
    }
    public void typeTitle(AutoCompleteTextView textView){
        String[] data = new String[] {"Mr.", "Mme."};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, data);
        textView.setAdapter(adapter);
    }
    public void  showIndicator(){
        ((ScrollView) findViewById(R.id.scrollView)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.Loader)).setVisibility(View.VISIBLE);
    }
    public void hideIndicator(){
        ((ScrollView) findViewById(R.id.scrollView)).setVisibility(View.VISIBLE);
        ((LinearLayout) findViewById(R.id.Loader)).setVisibility(View.GONE);
    }
    public void hidetKeyboard(){
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(((ScrollView) findViewById(R.id.scrollView)).getWindowToken(), 0);
    }

}