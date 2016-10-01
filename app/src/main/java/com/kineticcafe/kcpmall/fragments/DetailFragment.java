package com.kineticcafe.kcpmall.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kineticcafe.kcpmall.constants.Constants;
import com.kineticcafe.kcpmall.R;

public class DetailFragment extends Fragment {

    private Constants.DetailType mDetailType;
    private View mView;

    public DetailFragment() {
    }

    public static DetailFragment newInstance(Constants.DetailType detailType) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_BUNDLE_DEAL_TYPE, detailType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mode = getArguments().getString(Constants.KEY_BUNDLE_DEAL_TYPE);
            if(mode.equals(Constants.DetailType.DEAL.toString())){
                mDetailType = Constants.DetailType.DEAL;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_detail, container, false);
        return mView;

    }



}
