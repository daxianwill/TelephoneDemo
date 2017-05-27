package com.phone;

import com.base.BasePresenter;
import com.base.BaseView;

/**
 * The mvp of the phone
 */
public class PhoneMvp {
    public interface View extends BaseView<Presenter> {
    }
    public interface Presenter extends BasePresenter {
        void acceptCall();// 接听电话
        void rejectCall();// 拒接电话
    }
}
