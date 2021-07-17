package com.ehealth.app.ui.medic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MedicViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MedicViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is medic fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}