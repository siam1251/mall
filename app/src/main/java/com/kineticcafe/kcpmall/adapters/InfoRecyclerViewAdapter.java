package com.kineticcafe.kcpmall.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.fragments.InfoFragment;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class InfoRecyclerViewAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private ArrayList<Info> mInfoList;
    private final InfoFragment.OnListFragmentInteractionListener mListener;

    public static class Info {
        public String title;
        public String subTitle;

        public Info(String title, String subTitle){
            this.title = title;
            this.subTitle = subTitle;
        }
    }

    public InfoRecyclerViewAdapter(Context context, ArrayList<Info> infoList, InfoFragment.OnListFragmentInteractionListener listener) {
        mContext = context;
        mInfoList = infoList;
        mListener = listener;
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView tvInfoTitle;
        public TextView tvInfoSubTitle;

        public InfoViewHolder(View v) {
            super(v);
            mView = v;

            tvInfoTitle = (TextView) v.findViewById(R.id.tvInfoTitle);
            tvInfoSubTitle = (TextView) v.findViewById(R.id.tvInfoSubTitle);

            v.setTag(this);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InfoViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.list_item_info, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Info info = (Info) mInfoList.get(position);
        final InfoViewHolder infoViewHolder = (InfoViewHolder) holder;

        infoViewHolder.tvInfoTitle.setText(info.title);
        infoViewHolder.tvInfoSubTitle.setText(info.subTitle);

        infoViewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInfoList == null ? 0 : mInfoList.size();
    }
}
