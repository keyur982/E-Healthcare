package com.ehealth.app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HospitalPage extends AppCompatActivity {

    ListView hpDoctorList;
    private RequestQueue mQueue;
    ImageView hospImage;
    String hid;
    TextView hospName,hospAddress,hospPhone,hospSince,hospTiming,hospBeds,hospNotes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_page);
        setTitle("Book Appointment");

        Intent i = getIntent();
        hid = i.getStringExtra("hid");

        ActionBar actionBar = getSupportActionBar();

        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.outline_arrow_back_24);

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);

        hospImage = findViewById(R.id.iv_hpImg);
        hospName = findViewById(R.id.tv_hpName);
        hospAddress = findViewById(R.id.tv_hpAddress);
        hospPhone = findViewById(R.id.tv_hpPhone);
        hospSince = findViewById(R.id.tv_hpSince);
        hospTiming = findViewById(R.id.tv_hpTiming);
        hospBeds = findViewById(R.id.tv_hpBeds);
        hospNotes = findViewById(R.id.tv_hpNote);

        hpDoctorList = findViewById(R.id.li_hpDoctorList);
        mQueue = Volley.newRequestQueue(this);

        setListViewHeightBasedOnChildren(hpDoctorList);

        getData(hid);


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    myList adapter;
    String hospitalName;
    SharedPreferences sp;
    String[] names = {"Hospital 1","Hospital 2","Hospital 3"};
    String[] did = {"1","10","2"};
    String[] spec = {"pak","Eng","Ind"};
    String[] exp = {"pak","Eng","Ind"};
    String[] timing = {"10:00 to 18:00","24x7","9:00 to 21:00"};
    String[] imgid = {"pic","pic2","pic3"};
    String[] nxtAv = {"None","None","None"};

    private Context context;

    public class myList extends ArrayAdapter<String> {
        private final Activity context;
        private final String[] names;
        private final String[] spec;
        private final String[] exp;
        private final String[] nxtAv;
        private final String[] timing;
        private final String[] imgid;
        //private final String[] note;


        public myList(Activity context, String[] names, String[] spec, String[] exp, String[] nxtAv, String[] timing, String[] imgid) {
            super(context, R.layout.doctor_listrow, names);
            this.context = context;
            this.names = names;
            this.spec = spec;
            this.exp = exp;
            this.nxtAv = nxtAv;
            this.timing = timing;
            this.imgid = imgid;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater=context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.doctor_listrow, null,true);

            TextView docName = (TextView) rowView.findViewById(R.id.doc_name);
            TextView docSpec = (TextView) rowView.findViewById(R.id.doc_spec);
            TextView docExp = (TextView) rowView.findViewById(R.id.doc_exp);
            TextView docTiming = (TextView) rowView.findViewById(R.id.doc_time);
            TextView docAvail = (TextView) rowView.findViewById(R.id.doc_nxtTime);
            ImageView docImg = (ImageView) rowView.findViewById(R.id.doc_image);
            Button bookDoc = (Button)rowView.findViewById(R.id.btn_consult);


            docName.setText(names[position]);
            docSpec.setText(spec[position]);
            docExp.setText(exp[position]);
            docTiming.setText(timing[position]);
            docAvail.setText(nxtAv[position]);
            //imageView.setImageBitmap();
            Picasso.with(context).load(imgid[position]).into(docImg);

            SharedPreferences login = getSharedPreferences("logged_users", Context.MODE_PRIVATE);
            String loginState = login.getString("status","");
            if(loginState.equals("Logged In")) {
                bookDoc.setVisibility(View.VISIBLE);
            }

            bookDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater li = LayoutInflater.from(context);
                    View promptsView = li.inflate(R.layout.consult_prompt, null);


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            HospitalPage.this);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    // create alert dialog
                    final AlertDialog alertDialog = alertDialogBuilder
                            .setTitle("Book Appointment")
                            .setPositiveButton("Confirm",null)
                            .setNegativeButton("Cancel",null).show();

                    final EditText Pname = promptsView.findViewById(R.id.ed_Pname);
                    final EditText Age = (EditText) promptsView.findViewById(R.id.ed_Age);
                    final EditText Weight = (EditText) promptsView.findViewById(R.id.ed_Weight);
                    final EditText Cnote = (EditText) promptsView.findViewById(R.id.ed_consultNote);
                    TextView Hname = promptsView.findViewById(R.id.con_tvHname);
                    TextView Dname = promptsView.findViewById(R.id.con_tvDname);

                    sp = getSharedPreferences("user_details", Context.MODE_PRIVATE);
                    String Uname = sp.getString("Name","");
                    String Uage = sp.getString("Age","");
                    String Uweight = sp.getString("Weight","");
                    Dname.setText(Dname.getText()+"\t"+names[position]);
                    Hname.setText(Hname.getText()+"\t"+hospitalName);
                    Pname.setText(""+Uname);
                    Age.setText(""+Uage);
                    Weight.setText(""+Uweight);

                    final Calendar myCalendar = Calendar.getInstance();
                    final EditText dateText= (EditText) promptsView.findViewById(R.id.ed_consultDate);

                    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            // TODO Auto-generated method stub
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateLabel();
                        }

                        private void updateLabel() {
                            String myFormat = "MM/dd/yy"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                            dateText.setText(sdf.format(myCalendar.getTime()));
                        }

                    };

                    dateText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            DatePickerDialog datePickerDialog = new DatePickerDialog(HospitalPage.this,date,myCalendar.get(Calendar.YEAR),
                                    myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                            datePickerDialog.show();
                        }
                    });

                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(dateText.getText().toString().length()<1){
                                Toast.makeText(HospitalPage.this, "Please select date", Toast.LENGTH_SHORT).show();
                            }else{
                                AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(HospitalPage.this);
                                final AlertDialog alertDialog2 = alertDialogBuilder2
                                        .setMessage("Verified all the data, and confirm appointment ?")
                                        .setPositiveButton("Yes",null)
                                        .setNegativeButton("No",null).show();

                                Button positiveButton = alertDialog2.getButton(AlertDialog.BUTTON_POSITIVE);
                                positiveButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bookAppointment(Pname.getText().toString(),Age.getText().toString(),Weight.getText().toString(),Cnote.getText().toString(),dateText.getText().toString(),did[position]);
                                        alertDialog2.dismiss();
                                        alertDialog.dismiss();
                                    }
                                });


                            }
                        }
                    });

                }
            });

            return rowView;
        }
    }

    private void bookAppointment(final String Pname, final String Age, final String Weight, final String Note, final String Date, final String did) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://e-healthcare2021.000webhostapp.com/bookAppointment.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        Toast.makeText(HospitalPage.this, "Status: "+res, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams()
            {
                SharedPreferences sp = getSharedPreferences("logged_users", Context.MODE_PRIVATE);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Uid",sp.getString("user_id",""));
                params.put("age",Age);
                params.put("weight", Weight);
                params.put("cdate",Date);
                params.put("cdoctor",did);
                params.put("cmessage",Note);
                params.put("chospital",hid);

                return params;
            }
        };
        mQueue.add(request);
    }

    public void getData(String hid) {
        final String id = hid;
        StringRequest request = new StringRequest(Request.Method.POST, "https://e-healthcare2021.000webhostapp.com/fetchHospDoctor.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        try {
                            JSONObject response = new JSONObject(res);
                            hospitalName = response.getString("name");
                            hospName.setText(hospitalName);
                            Picasso.with(context).load("https://e-healthcare2021.000webhostapp.com/"+response.getString("photo")).into(hospImage);
                            hospPhone.setText(response.getString("phone"));
                            hospAddress.setText(response.getString("address"));
                            hospSince.setText(response.getString("since"));
                            hospTiming.setText(response.getString("time"));
                            hospBeds.setText(response.getString("beds"));


                            JSONArray jsonArray = response.getJSONArray("doctors");
                            if (jsonArray !=null) {
                                did = new String[jsonArray.length()];
                                names = new String[jsonArray.length()];
                                spec = new String[jsonArray.length()];
                                exp = new String[jsonArray.length()];
                                timing = new String[jsonArray.length()];
                                nxtAv = new String[jsonArray.length()];
                                imgid = new String[jsonArray.length()];

                                for (int n = 0; n < jsonArray.length(); n++) {
                                    JSONObject object = jsonArray.getJSONObject(n);
                                    did[n] = object.getString("did");
                                    names[n] = object.getString("name");
                                    spec[n] = object.getString("speciality");
                                    exp[n] = object.getString("exp");
                                    timing[n] = object.getString("visiting");
                                    nxtAv[n] = object.getString("nxtAv");
                                    imgid[n] = "https://e-healthcare2021.000webhostapp.com/" + object.getString("photo");
                                }
                                myList adapter = new myList((Activity) HospitalPage.this, names, spec, exp, nxtAv, timing, imgid);
                                hpDoctorList.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("Doctors", String.valueOf(res));
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("hid", id);
                        return params;
                    }
                };
        mQueue.add(request);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}