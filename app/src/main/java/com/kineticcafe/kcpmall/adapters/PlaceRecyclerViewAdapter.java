package com.kineticcafe.kcpmall.adapters;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;

import java.util.ArrayList;

/**
 * Created by Kay on 2016-05-05.
 */
public class PlaceRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private ArrayList<KcpPlaces> mKcpPlaceList;

    public PlaceRecyclerViewAdapter(Context context, ArrayList<KcpPlaces> kcpPlaces) {
        mContext = context;
        mKcpPlaceList = kcpPlaces;
    }

    public void updateData(ArrayList<KcpPlaces> kcpPlaces) {
        mKcpPlaceList.clear();
        mKcpPlaceList.addAll(kcpPlaces);
        notifyDataSetChanged();
    }

    public class PlaceHolder extends RecyclerView.ViewHolder {
        public View mView;
        public CardView cvAncmt;
        public ImageView  ivCategory;
        public TextView  tvCategory;

        public PlaceHolder(View v) {
            super(v);
            mView = v;
            cvAncmt = (CardView)  v.findViewById(R.id.cvAncmt);
            ivCategory = (ImageView)  v.findViewById(R.id.ivCategory);
            tvCategory = (TextView)  v.findViewById(R.id.tvCategory);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE:
                return new PlaceHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_place, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder.getItemViewType() == KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE) {
            final PlaceHolder placeHolder = (PlaceHolder) holder;
            final KcpPlaces kcpPlace = mKcpPlaceList.get(position);


        }
    }

    @Override
    public int getItemCount() {
        return mKcpPlaceList == null ? 0 : mKcpPlaceList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return KcpContentTypeFactory.PREF_ITEM_TYPE_ALL_PLACE;
    }
}
