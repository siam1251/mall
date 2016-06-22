package com.kineticcafe.kcpmall.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.models.MallInfo.InfoList;
import com.kineticcafe.kcpandroidsdk.models.MallInfo.KcpMallInfo;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.fragments.InfoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-05-05.
 */
public class InfoRecyclerViewAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<InfoList> mInfoList;
    private final InfoFragment.OnListFragmentInteractionListener mListener;

    public InfoRecyclerViewAdapter(Context context, List<InfoList> infoList, InfoFragment.OnListFragmentInteractionListener listener) {
        mContext = context;
        mInfoList = infoList;
        mListener = listener;
    }

    public void updateData(List<InfoList> infoList){
        mInfoList.clear();
        mInfoList.addAll(infoList);
        notifyDataSetChanged();
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
        final InfoList info = (InfoList) mInfoList.get(position);
        final InfoViewHolder infoViewHolder = (InfoViewHolder) holder;

        infoViewHolder.tvInfoTitle.setText(info.getTitle());
        infoViewHolder.tvInfoSubTitle.setText(info.getSubtitle());

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
