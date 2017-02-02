package com.ivanhoecambridge.mall.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.mall.activities.MainActivity;

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
