package com.kineticcafe.kcpmall.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.views.ExpiryDateAnimation;
import com.kineticcafe.kcpmall.views.ThemedImageView;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private Rect mRect = new Rect();
    private final static String STORE_HOUR_FORMAT = "h:mm aa"; //10:00:00 EDT
    private ViewGroup mParentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_with_ctl);
        mParentView = (ViewGroup) findViewById(R.id.svDetail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        final ImageView ivFav = (ImageView) toolbar.findViewById(R.id.ivFav);
        ivFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: implement fav functionality
                Toast.makeText(DetailActivity.this, "fav clicked", Toast.LENGTH_SHORT).show();
                ivFav.setSelected(!ivFav.isSelected());
                ((ThemedImageView) ivFav).onClick(v);
            }
        });

        setUpCTA();
        showContentsWithCTL();
    }

    public void setUpCTA(){
        final KcpContentPage kcpContentPage = (KcpContentPage) getIntent().getSerializableExtra(Constants.ARG_CONTENT_PAGE);
        int contentPageType = KcpContentTypeFactory.getContentType(kcpContentPage);


        List<CTA> cTAList = new ArrayList<>();

        //Store Location
        CTA location = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_map,
                kcpContentPage.getStoreLevel(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

        //Store Parking
        CTA parking = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_parking,
                kcpContentPage.getStoreParking(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

        //Store hour
        CTA hours = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_parking,
                kcpContentPage.getStoreHourForToday(STORE_HOUR_FORMAT),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

        //Store Phone number
        CTA phone = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_phone,
                kcpContentPage.getStoreNumber(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:"+ kcpContentPage.getStoreNumber()));
                        startActivity(callIntent);
                    }
                });

        //Store Info
        CTA info = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_home,
                "Store Information",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

        //Add to Calendar
        CTA addToCalendar = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_home,
                "Add to Calendar",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });


        if(contentPageType == KcpContentTypeFactory.ITEM_TYPE_LOADING){

        } else if(contentPageType == KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT){
            cTAList.add(location);
            cTAList.add(parking);
            cTAList.add(info);
        } else if(contentPageType == KcpContentTypeFactory.ITEM_TYPE_EVENT){
            cTAList.add(location);
            cTAList.add(parking);
            cTAList.add(info);
            cTAList.add(addToCalendar);

        } else if(contentPageType == KcpContentTypeFactory.ITEM_TYPE_DEAL){
            cTAList.add(location);
            cTAList.add(parking);
            cTAList.add(info);

        } else if(contentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
            cTAList.add(location);
            cTAList.add(parking);
            cTAList.add(hours);
            cTAList.add(phone);


            CTA facebook = new CTA(
                    R.layout.layout_detail_social_button,
                    R.drawable.icn_instagram,
                    kcpContentPage.getStoreName(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });

            CTA twiter = new CTA(
                    R.layout.layout_detail_social_button,
                    R.drawable.icn_twitter,
                    kcpContentPage.getStoreName(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });

            CTA webpage = new CTA(
                    R.layout.layout_detail_social_button,
                    R.drawable.icn_mall_info,
                    kcpContentPage.getStoreName(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    });


            //SOCIAL SHARING
            LinearLayout llSharing = (LinearLayout) findViewById(R.id.llSharing);
            RelativeLayout layoutSocialSharing = (RelativeLayout) findViewById(R.id.layoutSocialSharing);
            llSharing.setVisibility(View.VISIBLE);
            layoutSocialSharing.setVisibility(View.VISIBLE);

            ((ViewGroup) llSharing).removeAllViews();
            ((ViewGroup) llSharing).addView(facebook.getView());
            ((ViewGroup) llSharing).addView(twiter.getView());
            ((ViewGroup) llSharing).addView(webpage.getView());
        }

        View llCTA = findViewById(R.id.llCTA);
        ((ViewGroup) llCTA).removeAllViews();
        for(int i = 0; i < cTAList.size(); i++){
            ((ViewGroup) llCTA).addView(cTAList.get(i).getView());
        }
    }

    public class CTA {
        private View mView;
        private View.OnClickListener mOnClickListener;

        public CTA(int layout, int drawable, String title, View.OnClickListener onClickListener){
            mView = DetailActivity.this.getLayoutInflater().inflate(
                    layout,
                    mParentView,
                    false);
            if(!title.equals("")) mView.setVisibility(View.VISIBLE);

            ImageView ivDetailBtnImage= (ImageView) mView.findViewById(R.id.ivDetailBtnImage);
            TextView tvDetailBtnTitle= (TextView) mView.findViewById(R.id.tvDetailBtnTitle);

            ivDetailBtnImage.setImageResource(drawable);
            tvDetailBtnTitle.setText(title);
            mOnClickListener = onClickListener;
            mView.setOnClickListener(mOnClickListener);
        }
        public View getView(){
            return mView;
        }
    }



    public void showContentsWithCTL(){
        try {
            final KcpContentPage kcpContentPage = (KcpContentPage) getIntent().getSerializableExtra(Constants.ARG_CONTENT_PAGE);
            getSupportActionBar().setTitle("");

            final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ctlDetail);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            //to show toolbar title only when collapsed
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.ablDetail);
            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        collapsingToolbar.setTitle(KcpContentTypeFactory.getContentTypeTitle(kcpContentPage));
                        toolbar.setBackground(null);
                        isShow = true;
                    } else if(isShow) {
                        collapsingToolbar.setTitle("");
                        toolbar.setBackground(getResources().getDrawable(R.drawable.view_shadow));
                        isShow = false;
                    }
                }
            });

            RelativeLayout rlDetailImage = (RelativeLayout) findViewById(R.id.rlDetailImage);
            String imageUrl = kcpContentPage.getImageUrl();
            if(imageUrl.equals("")){
                //TODO: if it's necessary to have toolbar in white, change the theme here (now it's in themeColor)
                rlDetailImage.setVisibility(View.GONE);
            } else {
                final ImageView ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
                new GlideFactory().glideWithDefaultRatio(
                        ivDetailImage.getContext(),
                        imageUrl,
                        ivDetailImage,
                        R.drawable.placeholder);

                //TODO: daysLeft shows 1 less date (ex) 2016-05-27T00:00:00.000+00:00 shows date as 26 EST see if this is right
                TextView tvExpiryDate = (TextView) findViewById(R.id.tvExpiryDate);
                int daysLeftUntilEffectiveDate = kcpContentPage.getDaysLeftUntilEffectiveEndDate(kcpContentPage.effectiveEndTime);
                String daysLeft = kcpContentPage.getDaysLeftText(daysLeftUntilEffectiveDate, Constants.DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE);

                if(daysLeft.equals("")){
                    tvExpiryDate.setVisibility(View.GONE);
                } else {
                    tvExpiryDate.setVisibility(View.VISIBLE);
                    tvExpiryDate.setText(daysLeft);
                }
            }

            String logoUrl = kcpContentPage.getStoreLogo();
            TextView tvDetailLogoText = (TextView) findViewById(R.id.tvDetailLogoText);
            ImageView ivDetailLogo = (ImageView) findViewById(R.id.ivDetailLogo);

            if(logoUrl.equals("")){
                String placeName = kcpContentPage.getStoreName();
                ivDetailLogo.setVisibility(View.GONE);

                if(!placeName.equals("")) tvDetailLogoText.setVisibility(View.VISIBLE);
                tvDetailLogoText.setText(placeName);

            } else {
                tvDetailLogoText.setVisibility(View.GONE);
                new GlideFactory().glideWithDefaultRatio(
                        ivDetailLogo.getContext(),
                        logoUrl,
                        ivDetailLogo);
            }

            TextView tvDetailTitle = (TextView) findViewById(R.id.tvDetailTitle);
            String title = kcpContentPage.getTitle();
            tvDetailTitle.setText(title);

            TextView tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
            String time =
                    kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_EFFECTIVE) +
                            " - " +
                            kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.DATE_FORMAT_EFFECTIVE);
            if(time.equals(" - ")) tvDetailDate.setVisibility(View.GONE);
            tvDetailDate.setText(time);

            TextView tvDetailBody = (TextView) findViewById(R.id.tvDetailBody);
            String body = kcpContentPage.getBody();
            tvDetailBody.setText(Html.fromHtml(body));


        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
