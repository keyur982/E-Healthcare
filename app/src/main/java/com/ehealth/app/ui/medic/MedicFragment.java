package com.ehealth.app.ui.medic;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.ehealth.app.R;

public class MedicFragment extends Fragment {

    private MedicViewModel medicViewModel;
    EditText ed_Name,ed_Age,ed_Weight,ed_bloodGroup, ed_Height, ed_Allergies;
    CheckBox cb_Diabetes,cb_BP;
    Button save;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        medicViewModel =
                new ViewModelProvider(this).get(MedicViewModel.class);
        View root = inflater.inflate(R.layout.fragment_medic, container, false);
        medicViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        ed_Name = root.findViewById(R.id.ed_Name);
        ed_Age = root.findViewById(R.id.ed_Age);
        ed_bloodGroup = root.findViewById(R.id.ed_BG);
        ed_Height = root.findViewById(R.id.ed_Height);
        ed_Weight = root.findViewById(R.id.ed_Weight);
        ed_Allergies = root.findViewById(R.id.ed_Allergies);
        cb_Diabetes = root.findViewById(R.id.cb_diabetes);
        cb_BP = root.findViewById(R.id.cb_bloodPressure);
        save = root.findViewById(R.id.btn_saveMedic);

        sp = getActivity().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        ed_Name.setText(sp.getString("Name",""));
        ed_Age.setText(sp.getString("Age",""));
        ed_bloodGroup.setText(sp.getString("BG",""));
        ed_Weight.setText(sp.getString("Weight",""));
        ed_Height.setText(sp.getString("Height",""));

        String disease = sp.getString("Disease","");

        if(disease.equals("Blood Pressure")){
            cb_BP.setChecked(true);
        }else if(disease.equals("BloodPressure, Diabetes")){
            cb_Diabetes.setChecked(true);
            cb_BP.setChecked(true);
        }else if(disease == "Diabetes"){
            cb_Diabetes.setChecked(true);
        }else{
            //Toast.makeText(getContext(), " "+disease, Toast.LENGTH_SHORT).show();
        }

        ed_Allergies.setText(sp.getString("Allergies",""));

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = sp.edit();
                ed.putString("Name",ed_Name.getText().toString());
                ed.putString("Age",ed_Age.getText().toString());
                ed.putString("BG",ed_bloodGroup.getText().toString());
                ed.putString("Weight", ed_Weight.getText().toString());
                ed.putString("Height", ed_Height.getText().toString());

                if(cb_Diabetes.isChecked() && !cb_BP.isChecked()){
                    ed.putString("Disease",cb_Diabetes.getText().toString());
                }else if(cb_BP.isChecked() && !cb_Diabetes.isChecked()){
                    ed.putString("Disease",cb_BP.getText().toString());
                }else if(cb_Diabetes.isChecked()==true && cb_BP.isChecked()==true){
                    ed.putString("Disease","BloodPressure, Diabetes");
                }else{
                    ed.putString("Disease","No pre-Disease");
                }

                ed.putString("Allergies",ed_Allergies.getText().toString());
                //ed.apply();
                ed.commit();
                Toast.makeText(getContext(), "Details Saved..", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }
}