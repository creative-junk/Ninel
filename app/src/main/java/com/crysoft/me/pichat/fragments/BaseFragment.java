package com.crysoft.me.pichat.fragments;


import android.support.v4.app.Fragment;
import android.view.View;


public class BaseFragment extends Fragment {
    protected View contentView;

    protected View findViewById(int resId){
        if (contentView != null){
            return contentView.findViewById(resId);
        }
        return null;
    }


}
