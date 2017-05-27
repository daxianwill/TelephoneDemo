package com.phone;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.example.admin.telephonedemo.R;
import com.example.admin.telephonedemo.databinding.ActivityIncomingCallBinding;

/**
 * The view of the phone
 */
public class PhoneView extends Activity implements PhoneMvp.View{
    public static final String INCOMING_CALL_NAME="incoming_call_name";

    private PhoneMvp.Presenter presenter;
    private ActivityIncomingCallBinding binding;
    private PhoneViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.activity_incoming_call);
        presenter=new PhonePresenter(this);
        viewModel=new PhoneViewModel(presenter);

        Intent intent=getIntent();
        String name=intent.getStringExtra(INCOMING_CALL_NAME);
        viewModel.name.set(name);
        binding.setViewModel(viewModel);
        presenter.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void setPresenter(PhoneMvp.Presenter presenter) {
        this.presenter=presenter;
    }

    @Override
    public Activity getActivity() {
        return this;
    }



}