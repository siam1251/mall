package com.kineticcafe.kcpmall.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.MallInfo.AdditionalInfo;
import com.kineticcafe.kcpandroidsdk.models.MallInfo.InfoList;
import com.kineticcafe.kcpandroidsdk.utils.KcpUtility;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.utility.Utility;
import com.kineticcafe.kcpmall.views.ActivityAnimation;
import com.kineticcafe.kcpmall.views.CTA;
import com.kineticcafe.kcpmall.views.ExpandableTextView;
import com.kineticcafe.kcpmall.views.HtmlTextView;
import com.kineticcafe.kcpmall.views.MyTagHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kay on 2016-06-28.
 */
public class MallInfoDetailActivity extends AppCompatActivity{
    protected final Logger logger = new Logger(getClass().getName());
    private ViewGroup mParentView;
    private String mMallInfoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InfoList info = (InfoList) getIntent().getSerializableExtra(Constants.ARG_CONTENT_PAGE);

        setContentView(R.layout.activity_mall_info_detail);
        mParentView = (ViewGroup) findViewById(R.id.svDetail);
        mMallInfoType = info.getTitle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        showContentsWithCTL(info);
        setUpCTA(info);
        setDisclaimer(info);
    }


    public void showContentsWithCTL(final InfoList info){
        try {
            final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ctlDetail);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            final TextView tvToolbar = (TextView) toolbar.findViewById(R.id.tvToolbar);
            getSupportActionBar().setTitle("");

            final View backdrop = (View) findViewById(R.id.backdrop);
            int height = (int) (KcpUtility.getScreenWidth(this) / KcpUtility.getFloat(this, R.dimen.ancmt_image_ratio));
            CollapsingToolbarLayout.LayoutParams lp = (CollapsingToolbarLayout.LayoutParams) backdrop.getLayoutParams();
            lp.height = height;
            backdrop.setLayoutParams(lp);

            final String toolbarTitle = getResources().getString(R.string.title_mall_information);
            tvToolbar.setText(toolbarTitle);

            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.ablDetail);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                private int mToolBarHeight;
                private int mAppBarHeight;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(mToolBarHeight == 0) mToolBarHeight = toolbar.getMeasuredHeight();
                    if(mAppBarHeight == 0) mAppBarHeight = appBarLayout.getMeasuredHeight();

                    Float f = ((((float) mAppBarHeight - mToolBarHeight) + verticalOffset) / ( (float) mAppBarHeight - mToolBarHeight)) * 255;
                    int alpha = 255 - Math.round(f);
                    backdrop.getBackground().setAlpha(alpha);
                    tvToolbar.setTextColor(Color.argb(alpha, 255, 255, 255));
                    toolbar.getBackground().setAlpha(255 - alpha);
                }
            });

            ImageView ivMallInfoBanner = (ImageView) findViewById(R.id.ivMallInfoBanner);
            if(mMallInfoType.equals(getResources().getString(R.string.mall_info_gift_card))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_giftcard);
            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_about))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_about);
            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_guest_service))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_guest);
            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_amenities))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_amenities);
            }

            ExpandableTextView etvMallInfoDetail = (ExpandableTextView) findViewById(R.id.etvMallInfoDetail);
            etvMallInfoDetail.setText(info.getDetails());

            TextView expandable_text = (TextView) findViewById(R.id.expandable_text);
            HtmlTextView.setHtmlTextView(this, expandable_text, info.getDetails(), R.color.html_link_text_color);

        } catch (Exception e) {
            logger.error(e);
        }
    }


    public void setUpCTA(final InfoList info){
        try {
            List<CTA> cTAList = new ArrayList<>();
            CTA location = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_menu_map,
                    getResources().getString(R.string.cta_find_on_map),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(MallInfoDetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }, false);

            CTA phone = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_phone,
                    info.getPhoneNumber(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.makeCallWithAlertDialog(
                                    MallInfoDetailActivity.this,
                                    getResources().getString(R.string.title_make_calls),
                                    getResources().getString(R.string.warning_make_call) + info.getPhoneNumber() + "?",
                                    getResources().getString(R.string.action_call),
                                    getResources().getString(R.string.action_cancel),
                                    info.getPhoneNumber()
                            );
                        }
                    }, false);

            CTA checkBalance = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_gcard,
                    getResources().getString(R.string.cta_check_your_balance),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.openWebPage(
                                    MallInfoDetailActivity.this,
                                    getResources().getString(R.string.cta_check_your_balance_url)
                            );
                        }
                    }, false);

            CTA googleMap = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_menu_map,
                    info.getLocations().size() == 0 ? null : info.getLocations().get(0).getName(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.openGoogleMapWithAddress(MallInfoDetailActivity.this, HeaderFactory.MALL_NAME);
                        }
                    }, false);

            CTA email = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_email,
                    info.getEmail() == null ? null : info.getEmail().getTitle(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utility.sendEmail(MallInfoDetailActivity.this, info.getEmail().getTitle(), "");
                        }
                    }, false);

            if(mMallInfoType.equals(getResources().getString(R.string.mall_info_gift_card))){
                cTAList.add(location);
                cTAList.add(phone);
                cTAList.add(checkBalance);
            } else if (mMallInfoType.startsWith(getResources().getString(R.string.mall_info_about))){
                cTAList.add(googleMap);
                cTAList.add(phone);
            } else if (mMallInfoType.startsWith(getResources().getString(R.string.mall_info_guest_service))){
                cTAList.add(location);
//                cTAList.add(phone);
                cTAList.add(phone);
                cTAList.add(email);
            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_amenities))){

            }

            View llCTA = findViewById(R.id.llCTA);
            ((ViewGroup) llCTA).removeAllViews();
            for(int i = 0; i < cTAList.size(); i++){
                ((ViewGroup) llCTA).addView(cTAList.get(i).getView());
            }
        } catch (Resources.NotFoundException e) {
            logger.error(e);
        } catch (Exception e){
            logger.error(e);
        }
    }

    public void setDisclaimer(final InfoList info){

        List<AdditionalInfo> additionalInfoList = info.getAdditionalInfo();
        if(info.getAdditionalInfoTitle() == null || additionalInfoList.size() == 0) return;

        LinearLayout llAdditionalInfo = (LinearLayout) findViewById(R.id.llAdditionalInfo);
        llAdditionalInfo.setVisibility(View.VISIBLE);

        TextView tvAdditionalInfoHeader = (TextView) findViewById(R.id.tvAdditionalInfoHeader);
        if(info.getAdditionalInfoTitle() != null) tvAdditionalInfoHeader.setText(info.getAdditionalInfoTitle());

        LinearLayout llAdditionalInfoField = (LinearLayout) findViewById(R.id.llAdditionalInfoField);

        ((ViewGroup) llAdditionalInfoField).removeAllViews();
        for(AdditionalInfo additionalInfo : additionalInfoList){
            ((ViewGroup) llAdditionalInfoField).addView(getAdditionalInfoListItem(additionalInfo.getTitle(), additionalInfo.getDetails()));
        }
    }


    public View getAdditionalInfoListItem(String title, String detail){
        View v = getLayoutInflater().inflate(R.layout.list_item_mall_info_additional, null, false);

        TextView tvAdditionalInfoTitle = (TextView) v.findViewById(R.id.tvAdditionalInfoTitle);
        tvAdditionalInfoTitle.setText(title);

        TextView tvAdditionalInfoDetail = (TextView) v.findViewById(R.id.tvAdditionalInfoDetail);
        HtmlTextView.setHtmlTextView(this, tvAdditionalInfoDetail, detail, R.color.html_link_text_color);

        return v;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        ActivityAnimation.exitActivityAnimation(this);
    }
}
