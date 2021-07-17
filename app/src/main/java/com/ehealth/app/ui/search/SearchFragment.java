package com.ehealth.app.ui.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.ehealth.app.Login;
import com.ehealth.app.MainActivity;
import com.ehealth.app.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;

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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.google.android.material.tabs.TabLayout.*;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    TabLayout tabLayout;
    TabItem tab1,tab2,tab3,tab4,tab5,tab6;
    ViewPager viewPager;
    fragmentmanager fragmentmanager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        SharedPreferences sp = this.getActivity().getSharedPreferences("logged_users", Context.MODE_PRIVATE);
        final String u_id = sp.getString("user_id","");
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        getActivity().setTitle("Search");

        tabLayout=root.findViewById(R.id.ctablayout);
        tab1=root.findViewById(R.id.ctab1);
        tab2=root.findViewById(R.id.ctab2);
        tab3=root.findViewById(R.id.ctab3);
        tab4=root.findViewById(R.id.ctab4);
        tab5=root.findViewById(R.id.ctab5);
        tab6=root.findViewById(R.id.ctab6);
        viewPager=root.findViewById(R.id.pageholder);

        //viewPager.setOffscreenPageLimit(3);
        setRetainInstance(true);

        fragmentmanager =new fragmentmanager(getActivity().getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT ,tabLayout.getTabCount());
        viewPager.setAdapter(fragmentmanager);
        //viewPager.setOffscreenPageLimit(6);  //Add this

        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(),true);
                //Toast.makeText(getContext(), "this is "+tab.getPosition(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(Tab tab) {
            }

            @Override
            public void onTabReselected(Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));

        searchViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }

}

class fragmentmanager extends FragmentPagerAdapter
{
    private int tabno;

    public fragmentmanager(@NonNull FragmentManager fm, int behavior, int tabno) {
        super(fm, behavior);
        this.tabno = tabno;
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        new hospitalFragment();
        switch (position)
        {
            case 0:return hospitalFragment.newInstance("hospital","");
            case 1:return doctorFragment.newInstance("doctor","");
            case 2:return OtherFragment.newInstance("pharmacy","");
            case 3:return OtherFragment.newInstance("bloodbank","");
            case 4:return OtherFragment.newInstance("laboratory","");
            case 5:return OtherFragment.newInstance("surgical","");

            default:return  null;
        }
    }

    @Override
    public int getCount() {
        return tabno;
    }

}
