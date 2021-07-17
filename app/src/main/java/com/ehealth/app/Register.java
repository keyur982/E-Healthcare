package com.ehealth.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    Spinner sp_userType;
    TextInputLayout tl_fname, tl_lname, tl_email, tl_phone, tl_password, tl_cpass;
    TextInputEditText et_fname,et_lname,et_email,et_phone, et_password, et_cfmpassword;
    Button btn_register;
    String fname,lname,email,phone,password,cfmpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sp_userType = findViewById(R.id.sp_userType);
        tl_fname = findViewById(R.id.text_input_fname);
        tl_lname = findViewById(R.id.text_input_lname);
        tl_email = findViewById(R.id.text_input_email);
        tl_phone = findViewById(R.id.text_input_contact);
        tl_password = findViewById(R.id.text_input_password);
        tl_cpass = findViewById(R.id.text_input_cfmpassword);

        et_fname = findViewById(R.id.et_fname);
        et_lname = findViewById(R.id.et_lname);
        et_email = findViewById(R.id.et_email);
        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        et_cfmpassword = findViewById(R.id.et_cfmpassword);
        btn_register = findViewById(R.id.btn_register);

        String[] types = {"User","Doctor","Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,types);
        sp_userType.setAdapter(adapter);
        sp_userType.setEnabled(false);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fname = et_fname.getText().toString();
                lname = et_lname.getText().toString();
                email = et_email.getText().toString().trim();
                phone = et_phone.getText().toString();
                password = et_password.getText().toString();
                cfmpassword = et_cfmpassword.getText().toString();
                if(validate()) {
                    String encid= null;
                    try {
                        String encuser = "&" + URLEncoder.encode("firstname", "UTF-8") + "=" + URLEncoder.encode(fname, "UTF-8");
                        String enclname = "&" + URLEncoder.encode("lastname", "UTF-8") + "=" + URLEncoder.encode(lname, "UTF-8");
                        String encphone = "&" + URLEncoder.encode("contact", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
                        String encemail = "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                        String encpass = "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                        String enccpass = "&" + URLEncoder.encode("cpassword", "UTF-8") + "=" + URLEncoder.encode(cfmpassword, "UTF-8");

                        if(isConnected())
                            new insertData().execute(encuser, enclname, encphone, encemail, encpass, enccpass);
                        else
                            Toast.makeText(Register.this, "You are fool, Please turn on Internet.", Toast.LENGTH_LONG).show();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(Register.this, "Enter valid Inputs.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validate() {
        int err = 0;
        //First Name
        if (fname.length() < 1) {
            tl_fname.setError("Enter firstname");
            err++;
        } else
            tl_fname.setError(null);

        //Last name
        if (lname.length() < 1) {
            tl_lname.setError("Enter your lastname");
            err += 1;
        } else
            tl_lname.setError(null);

        //Phone no.
        if (phone.length() < 10) {
            tl_phone.setError("Enter a valid 10 digit mobile number");
            err -= -1;
        } else
            tl_phone.setError(null);

        //Email
        if (email.length() < 1) {
            tl_email.setError("Enter an Email");
            err -= -1;
        } else {
            String emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

            if (email.matches(emailPattern))
                tl_email.setError(null);
            else {
                tl_email.setError("Invalid email address");
                err = err + 1;
            }
        }

        //Password
        if (password.length() < 8){
            tl_password.setError("Enter minimum 8 characters");
            err +=1;
        } else {
            tl_password.setError(null);
            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);

            if (!matcher.matches()) {
                tl_password.setError("Use special character, numbers & capital letter");
                err+=1;
            } else {
                tl_password.setError(null);
            }
        }

        if (!password.equals(cfmpassword)) {
            tl_cpass.setError("Password not matched");
            err++;
        } else {
            tl_cpass.setError(null);
        }
       return err < 1;
    }

    class insertData extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            String msg ="";
            try {
                URL u = new URL("https://e-healthcare2021.000webhostapp.com/registration.php");
                HttpURLConnection con = (HttpURLConnection) u.openConnection();

                con.setRequestMethod("POST");
                OutputStream os = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter bw;
                bw = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
                for (String s:strings) {
                    bw.write(s);
                }
                bw.flush();
                bw.close();
                os.close();
                con.connect();

                InputStream is = con.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                msg = br.readLine();
            } catch (IOException e) {
                msg = e.toString();
            }
            return Html.fromHtml(msg).toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("Registered successfully")) {
                Toast.makeText(Register.this, s, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Register.this, Login.class);
                startActivity(i);
            }else{
                Toast.makeText(Register.this, s, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
}
