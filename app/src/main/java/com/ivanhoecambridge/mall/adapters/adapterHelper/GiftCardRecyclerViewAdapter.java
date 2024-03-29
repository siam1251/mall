package com.ivanhoecambridge.mall.adapters.adapterHelper;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.giftcard.GiftCard;
import com.ivanhoecambridge.mall.managers.GiftCardManager;
import com.ivanhoecambridge.mall.views.BadgeView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Kay on 2016-05-05.
 */
public class GiftCardRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private HashMap<String, GiftCard> giftCards;
    private ArrayList<GiftCard> mGiftCards = new ArrayList<>();
    private ArrayList<GiftCard> footerOnlyList = new ArrayList<>();
    private View.OnClickListener mFooterOnClickListener;
    private int mBadgeTextColor = Color.WHITE;
    private int mGeneralTextColor = Color.BLACK;
    private boolean mShowCheckBox = false;

    private boolean[] checkedStatus;

    private final int ITEM_TYPE_GC = 0;
    private final int ITEM_TYPE_FOOTER = 1;

    //showing rv in side menu (remove checkbox disabled)
    public GiftCardRecyclerViewAdapter(Context context, HashMap<String, GiftCard> giftCards, View.OnClickListener footerOnClickListener) {
        mContext = context;
        mGiftCards = new ArrayList<GiftCard>(giftCards.values());
        mFooterOnClickListener = footerOnClickListener;
        addFooter();
    }

    //showing rv from alertDialog (remove checkbox enabled)
    public GiftCardRecyclerViewAdapter(Context context, HashMap<String, GiftCard> giftCards) {
        mContext = context;
        mGiftCards = new ArrayList<GiftCard>(giftCards.values());
        mShowCheckBox = true;
        checkedStatus = new boolean[mGiftCards.size()];
    }

    public void addFooter(){
        GiftCard giftCard = new GiftCard();
        mGiftCards.add(giftCard);
        footerOnlyList.add(giftCard);
    }


    public void updateData() {
        mGiftCards = new ArrayList<>(GiftCardManager.getInstance(mContext).getGiftCards().values());
        if(mFooterOnClickListener != null) addFooter();
        notifyDataSetChanged();
    }

    public class GiftCardHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView  tvGCNumber;
        public BadgeView bvGCBalance;
        public View separator;
        public CheckBox cbGC;

        public GiftCardHolder(View v) {
            super(v);
            mView = v;
            tvGCNumber = (TextView)  v.findViewById(R.id.tvGCNumber);
            bvGCBalance = (BadgeView)  v.findViewById(R.id.bvGCBalance);
            separator = (View)  v.findViewById(R.id.separator);
            cbGC = (CheckBox)  v.findViewById(R.id.cbGC);
        }
    }

    public class GiftCardFooterHolder extends RecyclerView.ViewHolder {
        public View mView;
        public TextView  tvAddGC;

        public GiftCardFooterHolder(View v) {
            super(v);
            mView = v;
            tvAddGC = (TextView)  v.findViewById(R.id.tvAddGC);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case ITEM_TYPE_GC:
                return new GiftCardHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_gift_card, parent, false));
            case ITEM_TYPE_FOOTER:
                return new GiftCardFooterHolder(
                        LayoutInflater.from(mContext).inflate(R.layout.list_item_gift_card_footer, parent, false));
        }
        return null;
    }

    public void setBadgeColor(int generalTextColor, int badgeColor){
        mGeneralTextColor = generalTextColor;
        mBadgeTextColor = badgeColor;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            if (getItemViewType(position) == ITEM_TYPE_GC) {

                GiftCard giftCard = mGiftCards.get(position);
                final GiftCardHolder gcHolder = (GiftCardHolder) holder;

                gcHolder.tvGCNumber.setText(giftCard.getCardNumber());
                gcHolder.tvGCNumber.setTextColor(mGeneralTextColor);

                gcHolder.bvGCBalance.setBadgeText("$" + String.format("%.2f", giftCard.getCardBalance()));
                gcHolder.bvGCBalance.setBadgeTextColor(mBadgeTextColor);

                if(mShowCheckBox) {
                    gcHolder.separator.setVisibility(View.VISIBLE);
                    gcHolder.cbGC.setVisibility(View.VISIBLE);
                    gcHolder.cbGC.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                            checkedStatus[position] = b;
                        }
                    });
                    gcHolder.cbGC.setChecked(checkedStatus[position]);
                }

                gcHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        gcHolder.cbGC.performClick();
                    }
                });

            } else if (getItemViewType(position) == ITEM_TYPE_FOOTER) {
                GiftCardFooterHolder gcFooterHolder = (GiftCardFooterHolder) holder;
                gcFooterHolder.mView.setOnClickListener(mFooterOnClickListener);
                gcFooterHolder.tvAddGC.setTextColor(mGeneralTextColor);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mGiftCards.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(mFooterOnClickListener != null) {
            if (mGiftCards.size() == position + 1) return ITEM_TYPE_FOOTER;
            else return ITEM_TYPE_GC;
        } else return ITEM_TYPE_GC;
    }

    public GiftCard getGiftCard(int position){
        return mGiftCards.get(position);
    }

    public boolean[] getCheckedStatus() {
        if(checkedStatus == null) checkedStatus = new boolean[0];
        return checkedStatus;
    }
}
