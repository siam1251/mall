package com.kineticcafe.kcpmall.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.GlideFactory;
import com.kineticcafe.kcpmall.factory.KcpContentTypeFactory;
import com.kineticcafe.kcpmall.kcpData.KcpCategoryManager;
import com.kineticcafe.kcpmall.kcpData.KcpPlaceManager;
import com.kineticcafe.kcpmall.views.AlertDialogForInterest;
import com.kineticcafe.kcpmall.views.ThemeColorImageView;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());
    private Rect mRect = new Rect();
    private final static String STORE_HOUR_FORMAT = "h:mm aa"; //10:00:00 EDT
    private ViewGroup mParentView;
    private ImageView ivDetailImage; //transition image
    private ImageView ivDetailLogo; //transition logo

    private String mDetailPageSource;
    private int mContentPageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailPageSource = getIntent().getStringExtra(Constants.ARG_DETAIL_PAGE_ORIGIN);
        init();
        KcpContentPage kcpContentPage = (KcpContentPage) getIntent().getSerializableExtra(Constants.ARG_CONTENT_PAGE);

        mContentPageType = KcpContentTypeFactory.getContentType(kcpContentPage);
        if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlace = kcpPlacesRoot.getPlaceById(kcpContentPage.getStoreId());
            if(kcpPlace != null){
                kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);
            }
        }

        downloadIfNecessary(kcpContentPage);
        showContentsWithCTL(kcpContentPage);
        setUpCTA(kcpContentPage);
    }

    public void init(){
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
                ((ThemeColorImageView) ivFav).onClick(v);
            }
        });
    }

    public void downloadIfNecessary(KcpContentPage kcpContentPage) {
        if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_LOADING){
        } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT){
        } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_EVENT){
            if(kcpContentPage.getStoreNumber().equals("")){
                downloadPlace(kcpContentPage.getStoreId());
            }
        } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_DEAL){
            if(kcpContentPage.getStoreNumber().equals("")){
                downloadPlace(kcpContentPage.getStoreId());
            }
        } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
            if(kcpContentPage.getStoreNumber().equals("")){
                downloadPlace(kcpContentPage.getStoreId());
            }
        }
    }

    public void setUpCTA(final KcpContentPage kcpContentPage){
        List<CTA> cTAList = new ArrayList<>();

        //Store Location
        CTA location = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_menu_map,
                kcpContentPage.getStoreLevel(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                }, false);

        //Store Parking
        CTA parking = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_parking,
                kcpContentPage.getStoreParking(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "clicked ", Toast.LENGTH_SHORT).show();
                    }
                }, true);


        //Store Phone number
        CTA phone = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_phone,
                kcpContentPage.getStoreNumber(),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!kcpContentPage.getStoreNumber().equals("")){
                            AlertDialogForInterest alertDialogForInterest = new AlertDialogForInterest();
                            alertDialogForInterest.getAlertDialog(
                                    DetailActivity.this,
                                    R.string.title_make_calls,
                                    R.string.warning_make_call,
                                    R.string.action_ok,
                                    R.string.action_cancel,
                                    new AlertDialogForInterest.DialogAnsweredListener() {
                                        @Override
                                        public void okClicked() {
                                            Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                            callIntent.setData(Uri.parse("tel:"+ kcpContentPage.getStoreNumber()));
                                            startActivity(callIntent);
                                        }
                                    }).show();
                        }
                    }
                }, false);

        //Store Info
        CTA info = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_storeinfo,
                "Store Information",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mDetailPageSource.equals(KcpContentTypeFactory.ITEM_TYPE_STORE)) onBackPressed();
                        KcpPlaces kcpPlace = KcpPlacesRoot.getInstance().getPlaceById(kcpContentPage.getStoreId());
                        if(kcpPlace != null) {
                            KcpContentPage kcpContentPage = new KcpContentPage();
                            kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);
                            Intent intent = new Intent(DetailActivity.this, DetailActivity.class);
                            intent.putExtra(Constants.ARG_CONTENT_PAGE, kcpContentPage);
                            intent.putExtra(Constants.ARG_DETAIL_PAGE_ORIGIN, KcpContentTypeFactory.getContentType(kcpContentPage));

                            String imageUrl = kcpContentPage.getImageUrl();
                            String logoUrl = kcpContentPage.getStoreLogo();

                            ActivityOptionsCompat options = null;
                            if(ivDetailImage != null && !imageUrl.equals("")){
                                String transitionNameImage = DetailActivity.this.getResources().getString(R.string.transition_news_image);
                                options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        DetailActivity.this,
                                        Pair.create((View)ivDetailImage, transitionNameImage));
                            } else if(ivDetailLogo.getVisibility() == View.VISIBLE && !logoUrl.equals("")){
                                String transitionNameLogo = DetailActivity.this.getResources().getString(R.string.transition_news_logo);
                                options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                        DetailActivity.this,
                                        Pair.create((View)ivDetailLogo, transitionNameLogo));
                            }

                            ActivityCompat.startActivity(DetailActivity.this, intent, options.toBundle());
                            DetailActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {

                            //download it

                        }
                    }
                }, false);

        //Add to Calendar
        CTA addToCalendar = new CTA(
                R.layout.layout_detail_button,
                R.drawable.icn_addcal,
                "Add to Calendar",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    }
                }, false);


        if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_LOADING){

        } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_ANNOUNCEMENT){
            cTAList.add(location);
            cTAList.add(parking);
            cTAList.add(info);
        } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_EVENT){
            cTAList.add(location);
            cTAList.add(parking);
            cTAList.add(info);
            cTAList.add(phone);
            cTAList.add(addToCalendar);

        } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_DEAL){
            cTAList.add(location);
            cTAList.add(parking);
            cTAList.add(info);
            cTAList.add(phone);

        } else if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
            cTAList.add(location);
            cTAList.add(parking);
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
                    }, false);

            CTA twiter = new CTA(
                    R.layout.layout_detail_social_button,
                    R.drawable.icn_twitter,
                    kcpContentPage.getStoreName(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }, false);

            CTA webpage = new CTA(
                    R.layout.layout_detail_social_button,
                    R.drawable.icn_menu_mall_info,
                    kcpContentPage.getStoreName(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(DetailActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                        }
                    }, false);


            //SOCIAL SHARING
            LinearLayout llSharing = (LinearLayout) findViewById(R.id.llSharing);
            RelativeLayout layoutSocialSharing = (RelativeLayout) findViewById(R.id.layoutSocialSharing);
            TextView tvDetailSocialSharingBtnHeader = (TextView) layoutSocialSharing.findViewById(R.id.tvDetailSocialSharingBtnHeader);
            tvDetailSocialSharingBtnHeader.setText("Follow " + kcpContentPage.getStoreName() + " on...");
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


    public void downloadPlace(final int placeId){
        KcpPlaceManager kcpPlaceManager = new KcpPlaceManager(DetailActivity.this, new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                switch (inputMessage.arg1) {
                    case KcpCategoryManager.DOWNLOAD_FAILED:
                        break;
                    case KcpCategoryManager.DOWNLOAD_COMPLETE:
                        KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
                        KcpPlaces kcpPlace = kcpPlacesRoot.getPlaceById(placeId);
                        KcpContentPage kcpContentPage = new KcpContentPage();
                        kcpContentPage.setPlaceList(KcpContentTypeFactory.CONTENT_TYPE_STORE, kcpPlace);
                        setUpCTA(kcpContentPage);

                        break;
                    default:
                        super.handleMessage(inputMessage);
                }
            }
        });
        kcpPlaceManager.downloadPlace(placeId);
    }

    public class CTA {
        private View mView;
        private ProgressBar pb;
        private View.OnClickListener mOnClickListener;
        public String title;
        private TextView tvDetailBtnTitle;
        private ImageView ivDetailBtnImage;

        public CTA(int layout, int drawable, String title, View.OnClickListener onClickListener, boolean hideIfEmpty) {
            mView = DetailActivity.this.getLayoutInflater().inflate(
                    layout,
                    mParentView,
                    false);
            this.title = title;
            pb = (ProgressBar) mView.findViewById(R.id.pb);
            tvDetailBtnTitle= (TextView) mView.findViewById(R.id.tvDetailBtnTitle);
            ivDetailBtnImage= (ImageView) mView.findViewById(R.id.ivDetailBtnImage);

            if(hideIfEmpty){
                mView.setVisibility(View.GONE);
            } else {
                if(title.equals("")) {
                    tvDetailBtnTitle.setVisibility(View.GONE);
                    ivDetailBtnImage.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                } else {
                    tvDetailBtnTitle.setVisibility(View.VISIBLE);
                    ivDetailBtnImage.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }
            }

            ivDetailBtnImage.setImageResource(drawable);
            tvDetailBtnTitle.setText(title);
            mOnClickListener = onClickListener;
            mView.setOnClickListener(mOnClickListener);
        }

        public void setTitle(String title){
            if(!title.equals("")){
                tvDetailBtnTitle.setText(title);
                tvDetailBtnTitle.setVisibility(View.VISIBLE);
                ivDetailBtnImage.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
            }
        }
        public View getView(){
            return mView;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
    public void setToolbarBackground(Toolbar toolbar, @Nullable Drawable drawable){
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            toolbar.setBackgroundDrawable(drawable);
        } else {
            toolbar.setBackground(drawable);
        }
    }

    public void showContentsWithCTL(final KcpContentPage kcpContentPage){
        try {
            final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.ctlDetail);
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            final TextView tvToolbar = (TextView) toolbar.findViewById(R.id.tvToolbar);
            getSupportActionBar().setTitle("");

            final String toolbarTitle = KcpContentTypeFactory.getContentTypeTitle(kcpContentPage);


            final String imageUrl = kcpContentPage.getImageUrl();
            if(imageUrl.equals("")){
                //TODO: if it's necessary to have toolbar in white, change the theme here (now it's in themeColor)
                if(toolbarTitle.equals(KcpContentTypeFactory.TYPE_DEAL_STORE)) tvToolbar.setText(kcpContentPage.getStoreName());
                else tvToolbar.setText(toolbarTitle);
                setToolbarBackground(toolbar, null);

                RelativeLayout rlDetailImage = (RelativeLayout) findViewById(R.id.rlDetailImage);
                rlDetailImage.setVisibility(View.GONE);
            } else {
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
                            if(toolbarTitle.equals(KcpContentTypeFactory.TYPE_DEAL_STORE)) tvToolbar.setText(kcpContentPage.getStoreName());
                            else tvToolbar.setText(toolbarTitle);
                            setToolbarBackground(toolbar, null);
                            isShow = true;
                        } else if(isShow) {
                            tvToolbar.setText("");
                            setToolbarBackground(toolbar, getResources().getDrawable(R.drawable.view_shadow));
                            isShow = false;
                        }
                    }
                });

                ivDetailImage = (ImageView) findViewById(R.id.ivDetailImage);
                ivDetailImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailActivity.this, ZoomableImage.class);
                        intent.putExtra(Constants.ARG_IMAGE_URL, imageUrl);
                        DetailActivity.this.startActivity(intent);
                        DetailActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });
                new GlideFactory().glideWithDefaultRatio(
                        ivDetailImage.getContext(),
                        imageUrl,
                        ivDetailImage,
                        R.drawable.placeholder);


                TextView tvExpiryDate = (TextView) findViewById(R.id.tvExpiryDate);
                //TODO: daysLeft shows 1 less date (ex) 2016-05-27T00:00:00.000+00:00 shows date as 26 EST see if this is right
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
            ivDetailLogo = (ImageView) findViewById(R.id.ivDetailLogo);

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
            String title = "";
            if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
                title = kcpContentPage.getStoreName();
            } else {
                title = kcpContentPage.getTitle();
            }
            if(title.equals("")) tvDetailTitle.setVisibility(View.INVISIBLE);
            else tvDetailTitle.setText(title);

            TextView tvDetailDate = (TextView) findViewById(R.id.tvDetailDate);
            String time = "";
            if(mContentPageType == KcpContentTypeFactory.ITEM_TYPE_STORE){
                time = kcpContentPage.getStoreHourForToday(STORE_HOUR_FORMAT);
            } else {
                time =
                        kcpContentPage.getFormattedDate(kcpContentPage.effectiveStartTime, Constants.DATE_FORMAT_EFFECTIVE) +
                                " - " +
                                kcpContentPage.getFormattedDate(kcpContentPage.effectiveEndTime, Constants.DATE_FORMAT_EFFECTIVE);
            }

            if(time.equals(" - ")) tvDetailDate.setVisibility(View.GONE);
            tvDetailDate.setText(time);

            TextView tvDetailBody = (TextView) findViewById(R.id.tvDetailBody);
            String body = kcpContentPage.getBody();
            if(body.equals("")) tvDetailBody.setVisibility(View.GONE);
            else tvDetailBody.setText(Html.fromHtml(body)); //sometimes adds extra space in between


        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
