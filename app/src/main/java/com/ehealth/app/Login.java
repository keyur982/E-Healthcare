package com.ehealth.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    ImageView iv_login;
    Spinner sp_userType;
    TextInputLayout tl_email, tl_password;
    TextInputEditText et_email,et_password;
    TextView tv_register;
    String email,password,Uname;
    Button btn_login;
    boolean init=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        iv_login = findViewById(R.id.img_login);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        sp_userType = findViewById(R.id.sp_userType);
        tv_register = findViewById(R.id.tv_register);
        btn_login = findViewById(R.id.btn_login);
        tl_email = findViewById(R.id.text_input_email);
        tl_password = findViewById(R.id.text_input_password);


        /*String[] types = {"User","Doctor","Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, types);
        sp_userType.setAdapter(adapter);

        sp_userType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //iv_login.setImageResource(R.drawable.user);
                        if(!init)
                            ImageViewAnimatedChange(Login.this,iv_login,R.drawable.user);
                        init=false;
                        break;
                    case 1:
                        //iv_login.setImageResource(R.drawable.doctor);
                        ImageViewAnimatedChange(Login.this,iv_login,R.drawable.doctor);
                        break;
                    case 2:
                        //iv_login.setImageResource(R.drawable.admin);
                        ImageViewAnimatedChange(Login.this,iv_login,R.drawable.admin);
                        break;
                    default:
                        iv_login.setImageResource(R.drawable.user);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //iv_login.setImageDrawable(Drawable.createFromPath("user.png"));
            }
        });*/

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = Objects.requireNonNull(et_email.getText()).toString().trim();
                password = Objects.requireNonNull(et_password.getText()).toString();
                if(validate()) {
                    //String encid= null;
                    try {
                        String encemail = "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                        String encpass = "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                        if(isConnected())
                            new Login.insertData().execute(encemail, encpass);
                        else
                            Toast.makeText(Login.this, "Please turn on Internet !", Toast.LENGTH_LONG).show();

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(Login.this, "Enter valid Inputs.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_register.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                Intent i = new Intent(Login.this,Register.class);
                startActivity(i);
            }
        });

    }

    private boolean validate() {
        int err = 0;

        String emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

        if (email.matches(emailPattern))
            tl_email.setError(null);
        else {
            tl_email.setError("Invalid email address");
            err = err + 1;
        }

        if (password.length() < 8){
            tl_password.setError("Enter minimum 8 characters");
            err+=1;
        }
        else {
            tl_password.setError(null);
            Pattern pattern;
            Matcher matcher;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);

            if (!matcher.matches()) {
                tl_password.setError("Use special character & capital letter");
                err+=1;
            } else {
                tl_password.setError(null);
            }
        }

        return err < 1;
    }

    class insertData extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            String msg ="";
            try {
                URL u = new URL("https://e-healthcare2021.000webhostapp.com/logeen.php");
                HttpURLConnection con = (HttpURLConnection) u.openConnection();

                con.setRequestMethod("POST");
                OutputStream os = new BufferedOutputStream(con.getOutputStream());
                BufferedWriter bw;
                bw = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
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
            if(s.equals("User is not registered")) {
                Toast.makeText(Login.this, "Invalid credentials, OR "+s, Toast.LENGTH_SHORT).show();
            }else{
                JSONObject object = null;
                try {
                    object = new JSONObject(s);
                    Uname =object.getString("fname")+" "+object.getString("lname");
                    email=object.getString("email");
                    Log.e("Uname",Uname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences sp = getSharedPreferences("logged_users", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("status","Logged In");
                ed.putString("user_id", email);
                ed.putString("Uname",Uname);
                //ed.apply();
                ed.commit();
                Intent i = new Intent(Login.this, MainActivity.class);
                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                startActivity(i);
            }

        }
    }

    public static void ImageViewAnimatedChange(Context c, final ImageView v, final int new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.slide_out_right);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.slide_in_left);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageResource(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
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
