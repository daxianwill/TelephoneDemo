package com.phone;
import android.database.Observable;
import android.databinding.ObservableField;
import android.view.View;

/**
 * The model of the phone
 */
public class PhoneViewModel extends Observable {
    private PhoneMvp.Presenter presenter;
    public PhoneViewModel(PhoneMvp.Presenter presenter){
        this.presenter=presenter;
    }
    public ObservableField<String> name=new ObservableField<>();
    public void acceptCall(View view){
        presenter.acceptCall();
    }
    public void rejectCall(View view){
        presenter.rejectCall();
    }
}
