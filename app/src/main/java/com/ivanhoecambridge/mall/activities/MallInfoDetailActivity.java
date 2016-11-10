package com.ivanhoecambridge.mall.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.AdditionalInfo;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.InfoList;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.constants.Constants;

import constants.MallConstants;
import factory.HeaderFactory;
import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;
import com.ivanhoecambridge.mall.views.CTA;
import com.ivanhoecambridge.mall.views.ExpandableTextView;
import com.ivanhoecambridge.mall.views.HtmlTextView;

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
//                    tvToolbar.setTextColor(Color.argb(alpha, 255, 255, 255));
                    tvToolbar.setTextColor(Utility.getColorWithAlpha(MallInfoDetailActivity.this, R.color.toolbarTextColor, alpha));
                    toolbar.getBackground().setAlpha(255 - alpha);
                }
            });

            ImageView ivMallInfoBanner = (ImageView) findViewById(R.id.ivMallInfoBanner);
            if(mMallInfoType.equals(getResources().getString(R.string.mall_info_gift_card))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_giftcard);
            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_about))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_main);
            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_guest_service))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_guest);
            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_amenities))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_amenities);
            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_shuttles))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_main);
            } else if(mMallInfoType.contains(getResources().getString(R.string.mall_info_social))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_social);
            } else if(mMallInfoType.contains(getResources().getString(R.string.mall_info_public_transportation))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_transportation);
            } else if(mMallInfoType.contains(getResources().getString(R.string.mall_info_tourist_information))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_tourist_info);
            } else if(mMallInfoType.contains(getResources().getString(R.string.mall_info_accessibility))){
                ivMallInfoBanner.setImageResource(R.drawable.img_mallinfo_accessibility);
            }

            ExpandableTextView etvMallInfoDetail = (ExpandableTextView) findViewById(R.id.etvMallInfoDetail);
            etvMallInfoDetail.setText(info.getDetails());

            TextView expandable_text = (TextView) findViewById(R.id.expandable_text);
            HtmlTextView.setHtmlTextView(this, expandable_text, info.getDetails(), R.color.html_link_text_color);

            TextView tvMallInfoDetailTitle = (TextView) findViewById(R.id.tvMallInfoDetailTitle);
            tvMallInfoDetailTitle.setText(mMallInfoType);


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
                            setResult(Activity.RESULT_OK, new Intent());
                            finish();
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
                    }, true);

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
                    }, true);

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

            CTA webpage = new CTA(
                    this,
                    mParentView,
                    R.layout.layout_detail_button,
                    R.drawable.icn_web,
                    info.getEmail() == null ? null : info.getLinkTitle(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                             Utility.openWebPage(MallInfoDetailActivity.this, info.getLinkURL());
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

            } else if(mMallInfoType.startsWith(getResources().getString(R.string.mall_info_shuttles))){

                cTAList.add(googleMap);
                cTAList.add(phone);

                webpage = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_button,
                        R.drawable.icn_web,
                        info.getLinkTitle(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utility.openWebPage(MallInfoDetailActivity.this, info.getLinkURL());
                            }
                        }, true);

                cTAList.add(webpage);

            } else if(mMallInfoType.contains(getResources().getString(R.string.mall_info_social))){
                CTA facebook = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button_mall_info,
                        R.drawable.icn_facebook,
                        info.getFacebook() == null ? "" : info.getFacebook().getTitle(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utility.openFacebook(MallInfoDetailActivity.this, info.getFacebook().getUrl());
                            }
                        }, true);

                CTA instagram = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button_mall_info,
                        R.drawable.icn_instagram,
                        info.getInstagram() == null ? "" : info.getInstagram().getTitle(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utility.openInstagramWithName(MallInfoDetailActivity.this, MallConstants.INSTAGRAM_USER_NAME);
                            }
                        }, true);

                CTA twiter = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button_mall_info,
                        R.drawable.icn_twitter,
                        info.getTwitter() == null ? "" : info.getTwitter().getTitle(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utility.openTwitter(MallInfoDetailActivity.this, info.getTwitter().getUrl());
                            }
                        }, true);

                CTA youtube = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button_mall_info,
                        R.drawable.icn_youtube,
                        info.getYoutube() == null ? "" : info.getYoutube().getTitle(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utility.openWebPage(MallInfoDetailActivity.this, info.getYoutube().getUrl());
                            }
                        }, true);

                CTA pinterest = new CTA(
                        this,
                        mParentView,
                        R.layout.layout_detail_social_button_mall_info,
                        R.drawable.icn_pinterest,
                        info.getPinterest() == null ? "" : info.getPinterest().getTitle(),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utility.openWebPage(MallInfoDetailActivity.this, info.getPinterest().getUrl());
                            }
                        }, true);


                cTAList.add(facebook);
                cTAList.add(instagram);
                cTAList.add(twiter);
                cTAList.add(youtube);
                cTAList.add(pinterest);


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
