package com.kineticcafe.kcpmall.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.views.ThemedImageView;

public class DetailActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private Rect mRect = new Rect();


    //TODO: is this right

    public String getDetailType(KcpContentPage kcpContentPage){
        if(kcpContentPage == null){
            return Constants.TYPE_LOADING_TITLE;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_ANNOUNCEMENT)){
            return Constants.TYPE_ANNOUNCEMENT_TITLE;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_EVENT)){
            return Constants.TYPE_EVENT_TITLE;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_INTEREST)){
            return Constants.TYPE_SET_MY_INTEREST_TITLE;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_TWITTER)){
            return Constants.TYPE_TWITTER_TITLE;
        } else if(kcpContentPage.content_type.contains(Constants.CONTENT_TYPE_INSTAGRAM)){
            return Constants.TYPE_INSTAGRAM_TITLE;
        } else {
            return Constants.TYPE_ANNOUNCEMENT_TITLE;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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


        showContents();
    }

    public void showContents(){

        try {
            final KcpContentPage kcpContentPage = (KcpContentPage) getIntent().getSerializableExtra(Constants.ARG_CONTENT_PAGE);
            getSupportActionBar().setTitle(getDetailType(kcpContentPage));

            /*final CollapsingToolbarLayout collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.ctlDetail);
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
                        collapsingToolbar.setTitle(getDetailType(kcpContentPage));
                        isShow = true;
                    } else if(isShow) {
                        collapsingToolbar.setTitle("");
                        isShow = false;
                    }
                }
            });*/



            RelativeLayout rlDetailImage = (RelativeLayout) findViewById(R.id.rlDetailImage);
            String imageUrl = kcpContentPage.getImageUrl();
            if(imageUrl.equals("")){
                //TODO: if it's necessary to have toolbar in white, change the theme here (now it's in themeColor)
                rlDetailImage.setVisibility(View.GONE);
            } else {
                final ImageView ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);

                final ScrollView svDetail = (ScrollView) findViewById(R.id.svDetail);
                svDetail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ViewTreeObserver observer = svDetail.getViewTreeObserver();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            observer.removeOnGlobalLayoutListener(this);
                        } else {
                            observer.removeGlobalOnLayoutListener(this);
                        }
                        observer.addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                            @Override
                            public void onScrollChanged() {
                                ivDetailImage.getLocalVisibleRect(mRect);
                                if(mRect.bottom > 0){
                                    ivDetailImage.setY((float) (mRect.top / Constants.PARALLAX_PARAM));
                                }
                            }
                        });
                    }
                });


                new GlideFactory().glideWithDefaultRatio(
                        ivDetailImage.getContext(),
                        imageUrl,
                        ivDetailImage,
                        R.drawable.bg_splash);

                //TODO: daysLeft shows 1 less date (ex) 2016-05-27T00:00:00.000+00:00 shows date as 26 EST see if this is right
                TextView tvExpiryDate = (TextView) findViewById(R.id.tvExpiryDate);
                int daysLeftUntilEffectiveDate = kcpContentPage.getDaysLeftUntilEffectiveEndDate(kcpContentPage.effectiveEndTime);
                String daysLeft = kcpContentPage.getDaysLeftText(daysLeftUntilEffectiveDate, Constants.DAYS_LEFT_TO_SHOW_IN_EXPIRY_DATE);

                if(daysLeft.equals("")){
                    tvExpiryDate.setVisibility(View.GONE);
                } else {
                    tvExpiryDate.setText(daysLeft);
                    Animation anim = new ScaleAnimation(
                            1f, 1f, // Start and end values for the X axis scaling
                            0f, 1f, // Start and end values for the Y axis scaling
                            Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                            Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
                    anim.setFillAfter(true);
                    anim.setInterpolator(new AccelerateDecelerateInterpolator());
                    anim.setDuration(Constants.DURATION_DETAIL_EXPIRY_DATE_TEXT);
                    tvExpiryDate.setAnimation(anim);
                }
            }

            String logoUrl = kcpContentPage.getLogoFromFirstPlace();
            TextView tvDetailLogoText = (TextView) findViewById(R.id.tvDetailLogoText);
            ImageView ivDetailLogo = (ImageView) findViewById(R.id.ivDetailLogo);

            if(logoUrl.equals("")){
                String placeName = kcpContentPage.getNameFromFirstPlace();
                tvDetailLogoText.setVisibility(View.VISIBLE);
                ivDetailLogo.setVisibility(View.GONE);
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


            AppCompatButton btnDetailDate = (AppCompatButton) findViewById(R.id.btnDetailDate);


            TextView tvDetailBody = (TextView) findViewById(R.id.tvDetailBody);
            String body = kcpContentPage.getBody();
            tvDetailBody.setText(Html.fromHtml(body));

            View layoutLocation = (View) findViewById(R.id.layoutLocation);
            ImageView ivDetailBtnImageLocation = (ImageView) layoutLocation.findViewById(R.id.ivDetailBtnImage);
            ivDetailBtnImageLocation.setImageResource(R.drawable.icn_map);


            View layoutParking = (View) findViewById(R.id.layoutParking);
            ImageView ivDetailBtnImageParking = (ImageView) layoutParking.findViewById(R.id.ivDetailBtnImage);
            ivDetailBtnImageParking.setImageResource(R.drawable.icn_parking);

            View layoutPhone = (View) findViewById(R.id.layoutPhone);
            ImageView ivDetailBtnImagePhone= (ImageView) layoutPhone.findViewById(R.id.ivDetailBtnImage);
            ivDetailBtnImagePhone.setImageResource(R.drawable.icn_phone);

            layoutPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:"+ 1234567890));
                    startActivity(callIntent);
                }
            });


            TextView tvDetailDisclaimer = (TextView) findViewById(R.id.tvDetailDisclaimer);
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
