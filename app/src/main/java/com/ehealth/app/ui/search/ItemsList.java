package com.ehealth.app.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ehealth.app.HospitalPage;
import com.ehealth.app.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemsList {
    static ListView list;
    static myList adapter;
    static Button btn_book;
    static String[] id = {"0","0"};
    static List<String> names;
    static String[] contact = {"9876543210","1234567890","1651659813"};
    static String[] area = {"pak","Eng","Ind"};
    static String[] timing = {"10:00 to 18:00","24x7","9:00 to 21:00"};
    static String[] imgid = {"pic","pic2","pic3"};
    static String[] note = {"None","None","None"};

    private static Context context;
    static private RequestQueue mQueue;

    public ItemsList(Context context,ListView list){
        this.context =context;
        this.list = list;
    }


    public static class myList extends ArrayAdapter<String> implements Filterable {
        private final Activity context;
        private List<String> names;
        private final String[] contact;
        private final String[] area;
        private final String[] timing;
        private final String[] imgid;
        private final String[] note;
        private List<String>originalData = null;
        private List<String>filteredData = null;
        private LayoutInflater mInflater;
        private ItemFilter mFilter = new ItemFilter();


        public myList(Activity context, List<String> names, String[] contact, String[] area, String[] timing, String[] imgid, String[] note) {
            super(context, R.layout.listrow, names);
            this.context = context;
            this.filteredData = names;
            this.originalData = names;
            mInflater = LayoutInflater.from(context);
            this.names = names;
            this.contact = contact;
            this.area = area;
            this.timing = timing;
            this.imgid = imgid;
            this.note = note;
        }

        public int getCount() {
            return filteredData.size();
        }

        public String getItem(int position) {
            return filteredData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.listrow, null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView.findViewById(R.id.item_name);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.text.setText(filteredData.get(position));

           /* LayoutInflater inflater=context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.listrow, null,true);*/

            TextView titleText = convertView.findViewById(R.id.item_name);
            TextView contactText = convertView.findViewById(R.id.item_contact);
            TextView addressText = convertView.findViewById(R.id.item_address);
            TextView timingText = convertView.findViewById(R.id.item_timing);
            ImageView imageView = convertView.findViewById(R.id.item_image);
            TextView noteText = convertView.findViewById(R.id.item_note);
            btn_book = convertView.findViewById(R.id.btn_book);

            int p = names.indexOf(filteredData.get(position));

            titleText.setText(names.get(p));
            contactText.setText(contact[p]);
            addressText.setText(area[p]);
            timingText.setText(timing[p]);
            //imageView.setImageBitmap();
            Picasso.with(context).load(imgid[p]).into(imageView);
            noteText.setText(note[p]);
            if(!tableName.equals("hospital")){
                btn_book.setVisibility(View.INVISIBLE);
            }else{
                btn_book.setVisibility(View.VISIBLE);
            }

            btn_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), HospitalPage.class);
                    i.putExtra("hid",id[position]);
                    context.startActivity(i);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView text;
        }

        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<String> list = originalData;

                int count = list.size();
                final ArrayList<String> nlist = new ArrayList<String>(count);

                String filterableString, filterableString2;

                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i);
                    filterableString2 = area[i];

                    if (filterableString.toLowerCase().contains(filterString) ||
                            filterableString2.toLowerCase().contains(filterString)) {
                        nlist.add(filterableString);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData = (ArrayList<String>) results.values;
                hospitalFragment.simpleProgressBar.setVisibility(View.GONE);
                doctorFragment.simpleProgressBar.setVisibility(View.GONE);
                OtherFragment.simpleProgressBar.setVisibility(View.GONE);
                notifyDataSetChanged();
            }

        }
    }

    static String tableName = "";
    public static void getData(final String table) {
        tableName = table;
        mQueue = Volley.newRequestQueue(context);
        StringRequest request = new StringRequest(Request.Method.POST, "https://e-healthcare2021.000webhostapp.com/fetchData.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String res) {
                        try {
                            JSONObject response = new JSONObject(res);
                            JSONArray objArray = response.getJSONArray("data");
                            if(objArray!=null) {
                                names = Arrays.asList(new String[objArray.length()]);
                                contact = new String[objArray.length()];
                                area = new String[objArray.length()];
                                note = new String[objArray.length()];
                                timing = new String[objArray.length()];
                                imgid = new String[objArray.length()];
                                id = new String[objArray.length()];

                                for (int n = 0; n < objArray.length(); n++) {
                                    JSONObject object = null;
                                    try {
                                        object = objArray.getJSONObject(n);
                                        names.set(n, object.getString("name"));
                                        contact[n] = object.getString("phone");
                                        area[n] = object.getString("area");
                                        note[n] = object.getString("note");
                                        timing[n] = object.getString("time");
                                        imgid[n] = "https://e-healthcare2021.000webhostapp.com/" + object.getString("photo");
                                        id[n] = object.getString("id");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                adapter = new myList((Activity) context, names, contact, area, timing, imgid, note);
                                list.setAdapter(adapter);
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
                params.put("table", table);
                return params;
            }
        };
        mQueue.add(request);
    }
}
