package com.ehealth.app.ui.home;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ehealth.app.HospitalPage;
import com.ehealth.app.Login;
import com.ehealth.app.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    String Uname,Uid;
    String state;
    LinearLayout ll1,ll2,ll3,ll4;
    ListView lst_appoint,lst_appoint2, lst_underReview, lst_medHistory;
    private RequestQueue mQueue;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);
        final TextView txtWelcome = root.findViewById(R.id.text_welcome);
        final Button btnLogin = root.findViewById(R.id.btn_signIn);
        lst_appoint = root.findViewById(R.id.lst_appoint);
        lst_appoint2 = root.findViewById(R.id.lst_appoint2);
        lst_underReview = root.findViewById(R.id.lst_underReview);
        lst_medHistory = root.findViewById(R.id.lst_medHistory);
        ll1 = root.findViewById(R.id.layout_appoint1);
        ll2 = root.findViewById(R.id.layout_appoint2);
        ll3 = root.findViewById(R.id.layout_appoint3);
        ll4 = root.findViewById(R.id.layout_appoint4);

        lst_appoint.setEmptyView(root.findViewById(R.id.emptyElement1));
        lst_appoint2.setEmptyView(root.findViewById(R.id.emptyElement2));
        lst_underReview.setEmptyView(root.findViewById(R.id.emptyElement3));
        lst_medHistory.setEmptyView(root.findViewById(R.id.emptyElement4));

        sp = this.getActivity().getSharedPreferences("logged_users", Context.MODE_PRIVATE);
        state = sp.getString("status","");
        if(state.equals("Logged In")) {
            Uname = sp.getString("Uname","");
            Uid = sp.getString("user_id","");
            btnLogin.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            ll1.setVisibility(View.VISIBLE);
            ll2.setVisibility(View.VISIBLE);
            ll3.setVisibility(View.VISIBLE);
            ll4.setVisibility(View.VISIBLE);
        }else{
            txtWelcome.setVisibility(View.GONE);
            ll1.setVisibility(View.GONE);
            ll2.setVisibility(View.GONE);
            ll3.setVisibility(View.GONE);
            ll4.setVisibility(View.GONE);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), Login.class);
                    startActivity(i);
                }
            });
        }
        //String email = sp.getString("user_id","");

        mQueue = Volley.newRequestQueue(getContext());

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                txtWelcome.setText("Welcome "+Uname);
                textView.setText("\n Please Login to View/Book Appointments");
            }
        });

        getData(Uid,"Nconsult");
        getData(Uid,"Yconsult");
        getData(Uid,"Pconsult");
        getData(Uid,"userdata");

        return root;
    }
    apList adapter;
    String[] cid;
    String[] ulno;
    String[] date;
    String[] docName;
    String[] hosName;
    String[] cMessage;
    String[] hMessage;

    private Context context;

    public class apList extends ArrayAdapter<String> {
        private final Activity context;
        private final String[] cid;
        private final String[] date;
        private final String[] docName;
        private final String[] hosName;
        private final String[] cMessage;
        private final String[] hMessage;
        //private final String[] note;


        public apList(Activity context, String[] cid, String[] date, String[] docName, String[] hosName, String[] cMessage, String[] hMessage) {
            super(context, R.layout.appoint_listrow, cid);
            this.context = context;
            this.cid = cid;
            this.date = date;
            this.docName = docName;
            this.hosName = hosName;
            this.cMessage= cMessage;
            this.hMessage= hMessage;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater=context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.appoint_listrow, null,true);

            TextView tv_Cid = (TextView) rowView.findViewById(R.id.tv_consId);
            TextView tv_Date = (TextView) rowView.findViewById(R.id.tv_consDate);
            TextView tv_docName = (TextView) rowView.findViewById(R.id.tv_consDoctor);
            TextView tv_hosName = (TextView) rowView.findViewById(R.id.tv_consHospital);
            TextView tv_cMessage = (TextView) rowView.findViewById(R.id.tv_consMessage);
            //TextView tv_Age = (TextView) rowView.findViewById(R.id.tv_Age);
            TextView tv_hMessage = (TextView) rowView.findViewById(R.id.tv_hMessage);
            //Button bookDoc = (Button)rowView.findViewById(R.id.btn_showAppoint);


            tv_Cid.setText(""+(position+1));
            tv_Date.setText(date[position]);
            tv_docName.setText(docName[position]);
            tv_hosName.setText(hosName[position]);
            tv_cMessage.setText(cMessage[position]);
            tv_hMessage.setText(hMessage[position]);

            /*bookDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

                    // create alert dialog
                    final AlertDialog alertDialog = alertDialogBuilder
                            .setMessage("Are you want to cancel appointment ?")
                            .setPositiveButton("Yes",null)
                            .setNegativeButton("No",null).show();

                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelAppointment(cid[position]);
                            alertDialog.dismiss();
                        }
                    });

                }
            });*/
            return rowView;
        }
    }

    public void cancelAppointment(String cid){

    }

    public void getData(String uid, String aptype) {
        final String Uid = uid;
        final String apType = aptype;
        StringRequest request = new StringRequest(Request.Method.POST, "https://e-healthcare2021.000webhostapp.com/fetchAppoint.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        try {
                            JSONObject response = new JSONObject(res);
                            JSONArray jsonArray = response.getJSONArray(apType);
                            if (jsonArray !=null) {
                                cid = new String[jsonArray.length()];
                                docName = new String[jsonArray.length()];
                                hosName = new String[jsonArray.length()];
                                date = new String[jsonArray.length()];
                                cMessage = new String[jsonArray.length()];
                                hMessage = new String[jsonArray.length()];

                                for (int n = 0; n < jsonArray.length(); n++) {
                                    JSONObject object = jsonArray.getJSONObject(n);
                                    cid[n] = object.getString("cid");
                                    date[n] = object.getString("cdate");
                                    docName[n] = object.getString("cdoctor");
                                    hosName[n] = object.getString("chospital");
                                    cMessage[n] = object.getString("cmessage");
                                    hMessage[n] = object.getString("hmessage");
                                }
                                adapter = new apList((Activity) getContext(), cid, date, docName, hosName, cMessage, hMessage);
                                if(apType.equals("Yconsult"))
                                    lst_appoint.setAdapter(adapter);
                                if(apType.equals("Nconsult"))
                                    lst_appoint2.setAdapter(adapter);
                                if(apType.equals("Pconsult"))
                                    lst_underReview.setAdapter(adapter);
                                if(apType.equals("userdata"))
                                    lst_medHistory.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("Appoints: ", String.valueOf(res));
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
                params.put("Uid",Uid);
                return params;
            }
        };
        mQueue.add(request);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_logout);
        item.setVisible(false);
    }
}