package com.kineticcafe.kcpmall.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpmall.activities.MainActivity;

/**
 * Created by Kay on 2016-05-19.
 */
public class BaseFragment extends Fragment {
    protected final Logger logger = new Logger(getClass().getName());
    protected MainActivity mMainActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mMainActivity = (MainActivity) context;
            if(mListener != null) mListener.onFragmentInteraction();
        }
    }

    private OnFragmentInteractionListener mListener;
    public void setOnFragmentInteractionListener(OnFragmentInteractionListener listener){
        mListener = listener;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }
}
