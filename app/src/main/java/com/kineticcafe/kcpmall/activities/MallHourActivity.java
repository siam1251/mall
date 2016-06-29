package com.kineticcafe.kcpmall.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kineticcafe.kcpandroidsdk.constant.KcpConstants;
import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpOperatingHour;
import com.kineticcafe.kcpandroidsdk.models.KcpOverrides;
import com.kineticcafe.kcpandroidsdk.models.KcpPlaces;
import com.kineticcafe.kcpandroidsdk.models.KcpPlacesRoot;
import com.kineticcafe.kcpandroidsdk.models.MallInfo.KcpMallInfoRoot;
import com.kineticcafe.kcpandroidsdk.utils.KcpTimeConverter;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.utility.Utility;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Kay on 2016-06-24.
 */
public class MallHourActivity extends AppCompatActivity {


    protected final Logger logger = new Logger(getClass().getName());
    private final int NUMB_OF_DAYS = 7;

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

            final String toolbarTitle = getResources().getString(R.string.title_mall_information);
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
                        tvToolbar.setText(toolbarTitle);
                        Utility.setToolbarBackground(toolbar, null);
                        isShow = true;
                    } else if(isShow) {
                        tvToolbar.setText("");
                        Utility.setToolbarBackground(toolbar, getResources().getDrawable(R.drawable.view_shadow));
                        isShow = false;
                    }
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

            List<KcpOverrides.ContinuousOverride> comingHolidays = kcpPlaces.getHolidaysWithin(NUMB_OF_DAYS);

            if(comingHolidays.size() != 0){
                llHolidayHours.setVisibility(View.VISIBLE);

                String holidayNames = "";
                String holidayPeriod = "";
                String holidayDay = "";
                for(int i = 0; i < comingHolidays.size(); i++) {

                    holidayNames = holidayNames + comingHolidays.get(i).getName();
                    if(i != comingHolidays.size() - 1 ) holidayNames = holidayNames + "\n";

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
                    else holidayDay = holidayDay + " (CLOSED)";
                    if(i != comingHolidays.size() - 1 ) holidayDay = holidayDay + "\n";

                }

                tvHolidayName.setText(holidayNames);
                tvHolidayHoursPeriod.setText(holidayPeriod);

                String holidayHour = "This is a reminder that " + HeaderFactory.MALL_NAME + " will have adjusted hours on " + "\n"
                                    + holidayDay;


                tvHolidayhoursDescription.setText(holidayHour);
            }


            //MALL HOURS
            LinearLayout llMallHour = (LinearLayout) findViewById(R.id.llMallHour);
            ((ViewGroup) llMallHour).removeAllViews();
            for(int i = 0; i < NUMB_OF_DAYS; i++){
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
            TextView tvMallHour = (TextView) v.findViewById(R.id.tvMallHour);
            View separator = (View) v.findViewById(R.id.separator);

            Calendar today = Calendar.getInstance();
            long todayInMillisPlusDays = daysPastToday * 24 * 60 * 60 * 1000 + today.getTimeInMillis();
            today.setTimeInMillis(todayInMillisPlusDays);

            KcpPlacesRoot kcpPlacesRoot = KcpPlacesRoot.getInstance();
            KcpPlaces kcpPlaces = kcpPlacesRoot.getPlaceByPlaceType(KcpPlaces.PLACE_TYPE_MALL);

            KcpOperatingHour.KcpDay kcpDay = kcpPlaces.operatingHour.getDayMap().get(today.get(Calendar.DAY_OF_WEEK));
            String openTime = KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(kcpDay.startTime, KcpOperatingHour.HOUR_FORMAT, KcpPlaces.STORE_HOUR_FORMAT);
            String endTime = KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(kcpDay.endTime, KcpOperatingHour.HOUR_FORMAT, KcpPlaces.STORE_HOUR_FORMAT);

            tvMallHour.setText(openTime + " - " + endTime);

            if(daysPastToday == 0) {
                tvDate.setText("Today");
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
                if(daysPastToday == NUMB_OF_DAYS - 1) separator.setVisibility(View.GONE);
            }

            for(int i = 0; i < comingHolidays.size(); i++){
                KcpOverrides.ContinuousOverride comingHoliday = comingHolidays.get(i);
                if(comingHoliday != null && KcpTimeConverter.isTwoDateSame(comingHoliday.getStartDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, today)){
                    tvDate.setTextColor(getResources().getColor(R.color.info_mall_hour_holiday_stroke));
                    String holidayStartTime = KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(comingHoliday.getStartDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, KcpPlaces.STORE_HOUR_FORMAT);
                    String holidayEndTime = KcpTimeConverter.convertDateFormatWithYearMonthDateGiven(comingHoliday.getEndDatetime(), KcpConstants.OVERRIDE_HOUR_FORMAT, KcpPlaces.STORE_HOUR_FORMAT);

                    boolean isOpen = comingHoliday.getStatus().equals("open") ? true : false;
                    if(isOpen) tvMallHour.setText(holidayStartTime + " - " + holidayEndTime);
                    else tvMallHour.setText("CLOSED");
                }
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

}
