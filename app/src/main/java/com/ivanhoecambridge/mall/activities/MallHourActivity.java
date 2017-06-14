package com.ivanhoecambridge.mall.activities;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.ivanhoecambridge.kcpandroidsdk.constant.KcpConstants;
import com.ivanhoecambridge.kcpandroidsdk.logger.Logger;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpOverrides;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlaces;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpPlacesRoot;
import com.ivanhoecambridge.kcpandroidsdk.models.MallInfo.KcpMallInfoRoot;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpTimeConverter;
import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.R;
import com.ivanhoecambridge.mall.analytics.Analytics;
import com.ivanhoecambridge.mall.constants.Constants;
import factory.HeaderFactory;

import com.ivanhoecambridge.mall.utility.Utility;
import com.ivanhoecambridge.mall.views.ActivityAnimation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Kay on 2016-06-24.
 */
public class MallHourActivity extends AppCompatActivity {

    protected final Logger logger = new Logger(getClass().getName());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mall_hour);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        showContentsWithCTL();
    }

    public void showContentsWithCTL(){
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
                    tvToolbar.setTextColor(Utility.getColorWithAlpha(MallHourActivity.this, R.color.toolbarTextColor, alpha));
                    toolbar.getBackground().setAlpha(255 - alpha);
                }
            });


            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlaces = kcpPlacesRoot.getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL);
            if(kcpPlaces == null) return;

            //HOLIDAY HOURS
            LinearLayout llHolidayHours = (LinearLayout) findViewById(R.id.llHolidayHours);
            TextView tvHolidayName = (TextView) findViewById(R.id.tvHolidayName);
            TextView tvHolidayHoursPeriod = (TextView) findViewById(R.id.tvHolidayHoursPeriod);
            TextView tvHolidayhoursDescription = (TextView) findViewById(R.id.tvHolidayhoursDescription);
            TextView tvHolidayhoursList = (TextView) findViewById(R.id.tvHolidayhoursList);

            List<KcpOverrides.ContinuousOverride> comingHolidays = kcpPlaces.getHolidaysWithin(Constants.NUMB_OF_DAYS);
            KcpUtility.sortHoursList((ArrayList) comingHolidays);

            if(comingHolidays.size() != 0){
                llHolidayHours.setVisibility(View.VISIBLE);

                String holidayNames = "";
                String holidayPeriod = "";
                String holidayDay = "";
                for(int i = 0; i < comingHolidays.size(); i++) {

                    holidayNames = holidayNames + comingHolidays.get(i).getName();
                    if(i != comingHolidays.size() - 1 ) holidayNames = holidayNames + " • ";

                    String holidayStartDate = KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(comingHolidays.get(i).getStartDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, Constants.DATE_FORMAT_HOLIDAY_DATE);
                    String holidayEndDate = KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(comingHolidays.get(i).getEndDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, Constants.DATE_FORMAT_HOLIDAY_DATE);

                    String newHolidayPeriod = holidayStartDate.equals(holidayEndDate) ? holidayStartDate : holidayStartDate + " - " + holidayEndDate;
                    holidayPeriod = holidayPeriod + newHolidayPeriod;
                    if(i != comingHolidays.size() - 1 ) holidayPeriod = holidayPeriod + "\n";

                    holidayDay = holidayDay + KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(comingHolidays.get(i).getEndDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, Constants.DATE_FORMAT_HOLIDAY);
                    boolean isOpen = comingHolidays.get(i).getStatus().equals("open") ? true : false;


                    String holidayStartTime = KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(comingHolidays.get(i).getStartDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, KcpPlaces.STORE_HOUR_FORMAT);
                    String holidayEndTime = KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(comingHolidays.get(i).getEndDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, KcpPlaces.STORE_HOUR_FORMAT);

                    if(isOpen) holidayDay = holidayDay + " (" + holidayStartTime + " - " + holidayEndTime + ")";
                    else holidayDay = holidayDay + " (" + getString(R.string.closed_in_mall_hour) +")";
                    if(i != comingHolidays.size() - 1 ) holidayDay = holidayDay + "\n";

                }

                tvHolidayName.setText(holidayNames);
                tvHolidayHoursPeriod.setText(holidayPeriod);

                Resources res = getResources();
                String holidayHour = String.format(res.getString(R.string.holiday_reminder_in_mall_hour), HeaderFactory.MALL_NAME);

                tvHolidayhoursDescription.setText(holidayHour);
                tvHolidayhoursList.setText(holidayDay);
            }


            //MALL HOURS
            LinearLayout llMallHour = (LinearLayout) findViewById(R.id.llMallHour);
            ((ViewGroup) llMallHour).removeAllViews();
            for(int i = 0; i < Constants.NUMB_OF_DAYS; i++){
                ((ViewGroup) llMallHour).addView(getMallHourListItem(i, comingHolidays));
            }


            //DISCLAIMER - HOURS VARYING STORES
            LinearLayout layoutDisclaimer = (LinearLayout) findViewById(R.id.layoutDisclaimer);
            LinearLayout llHoursVaryingStore = (LinearLayout) findViewById(R.id.llHoursVaryingStore);
            List<Object> hoursExceptionList = KcpMallInfoRoot.getInstance().getKcpMallInfo().getHoursExceptions();
            if(hoursExceptionList.size() != 0){
                layoutDisclaimer.setVisibility(View.VISIBLE);
                ((ViewGroup) llHoursVaryingStore).removeAllViews();
                for(int i = 0; i < hoursExceptionList.size(); i++) {
                    ((ViewGroup) llHoursVaryingStore).addView(getHoursExceptionListItem((String) hoursExceptionList.get(i)));
                }
            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    public View getMallHourListItem(int daysPastToday, List<KcpOverrides.ContinuousOverride> comingHolidays){
        View v = getLayoutInflater().inflate(R.layout.list_item_mall_hour, null, false);
        try {
            TextView tvDate = (TextView) v.findViewById(R.id.tvDate);
            TextView tvMallHour = (TextView) v.findViewById(R.id.tvHour);
            ImageView ivHolidayIndicator = (ImageView) v.findViewById(R.id.ivHolidayIndicator);
            View separator = (View) v.findViewById(R.id.separator);

            Calendar today = Calendar.getInstance();
            long todayInMillisPlusDays = daysPastToday * 24 * 60 * 60 * 1000 + today.getTimeInMillis();
            today.setTimeInMillis(todayInMillisPlusDays);

            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlaces = kcpPlacesRoot.getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL);

            String openAndClosingHour = kcpPlaces.getOpeningAndClosingHoursForThisDay(today.get(Calendar.DAY_OF_WEEK));
            tvMallHour.setText(openAndClosingHour);

            if(daysPastToday == 0) {
                tvDate.setText(getString(R.string.today_in_mall_hour));
                v.setBackgroundColor(getResources().getColor(R.color.mall_hour_selected_bg));
                tvDate.setTextColor(getResources().getColor(R.color.white));
                tvMallHour.setTextColor(getResources().getColor(R.color.white));

                Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
                tvDate.setTypeface(face);
                tvMallHour.setTypeface(face);

                separator.setVisibility(View.GONE);
            } else {
                String mallHourDate = KcpTimeConverter.convertDateFormat(today.getTime(), Constants.DATE_FORMAT_MALL_HOUR_DATE);
                tvDate.setText(mallHourDate);
                if(daysPastToday == Constants.NUMB_OF_DAYS - 1) separator.setVisibility(View.GONE);
            }

            //overriding holidays
            openAndClosingHour = kcpPlaces.getOpeningAndClosingHoursForThisDayWithOverrideHours(comingHolidays, today);
            if(!openAndClosingHour.equals("")){
                tvMallHour.setText(openAndClosingHour);
                ivHolidayIndicator.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            logger.error(e);
        }

        return v;
    }

    public View getHoursExceptionListItem(String holidayExceptionName){
        View v = getLayoutInflater().inflate(R.layout.list_item_holiday_exception, null, false);
        TextView tvHolidayException = (TextView) v.findViewById(R.id.tvHolidayException);
        tvHolidayException.setText(" • " + holidayExceptionName);

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

    @Override
    protected void onResume() {
        super.onResume();
        Analytics.getInstance(this).logScreenView(this, "Mall Information Screen - Mall Hours Section");
    }
}
