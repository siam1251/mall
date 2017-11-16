package com.ivanhoecambridge.mall.rating;

import android.content.Context;
import android.util.Log;

import com.ivanhoecambridge.kcpandroidsdk.utils.KcpUtility;
import com.ivanhoecambridge.mall.BuildConfig;
import com.ivanhoecambridge.mall.constants.Constants;

import java.util.Calendar;
import java.util.Date;

import constants.MallConstants;


/**
 * Created by petar on 2017-11-02.
 */
public class AppRatingManager{

    private final static String TAG = "AppRate";
    private final static String GSON_KEY_USER_RATED   = "ar_user_rated";
    private final static String GSON_KEY_LAUNCH_TIMES = "ar_launch_times";
    private final static String GSON_KEY_LATER_WAIT   = "ar_later_wait";
    private final static String GSON_KEY_CRASHED_DATE = "ar_crashed_date";
    private final static String GSON_KEY_LAST_INC_DATE = "ar_last_incremented_day";

    private final int dayThreshold;
    private final int launchTimes;
    private final int waitDays;
    private boolean   shouldKeepTrackOfCrashes;
    private boolean   shouldResetOnUpgrade;

    private static AppRatingManager INSTANCE;

    public static void init(Context context) {
      if (INSTANCE == null) {
          synchronized (AppRatingManager.class) {
              INSTANCE = new AppRatingManager.Builder()
                      .launchTimes(3)
                      .laterWaits(2)
                      .delayRatingIfAppCrashes(true).minDaysToWait(0)
                      .resetOnUpgrade(true)
                      .build();
          }

      }
      updateUsageStats(context);
    }

    public static AppRatingManager getInstance() {
        return INSTANCE;
    }

    private AppRatingManager(AppRatingManager.Builder builder) {
        this.dayThreshold = builder.dayThreshold;
        this.shouldKeepTrackOfCrashes = builder.shouldKeepTrackOfCrashes;
        this.launchTimes = builder.launchTimes;
        this.waitDays = builder.waitDays;
        this.shouldResetOnUpgrade = builder.resetOnUpgrade;
    }


    private static void updateUsageStats(Context context) {
        if (INSTANCE.shouldResetOnUpgrade && KcpUtility.isNewVersion(context, Constants.KEY_APP_VERSION, BuildConfig.VERSION_NAME)) {
            KcpUtility.removeFromCache(context, GSON_KEY_USER_RATED);
        }
        if (isNewDay(context)) {
            KcpUtility.saveGson(context, GSON_KEY_LAST_INC_DATE, Calendar.getInstance().getTime());
            KcpUtility.incrementIntCacheValue(context, GSON_KEY_LAUNCH_TIMES);
        }
        //only increment the later wait value if exists aka the user has already seen the dialog and pressed LATER
        if (KcpUtility.loadIntFromCache(context, GSON_KEY_LATER_WAIT, -1) >= 0) {
            KcpUtility.incrementIntCacheValue(context, GSON_KEY_LATER_WAIT);
        }
    }

    public void getUsage(final Context context, final RatingListener ratingListener) {
        if (!KcpUtility.loadFromSharedPreferences(context, GSON_KEY_USER_RATED, false)
                && (!shouldKeepTrackOfCrashes || isCrashDatePastMinDays(context))) {

            int amountOfTimesOpened = KcpUtility.loadIntFromCache(context, GSON_KEY_LAUNCH_TIMES, 0);
            int laterWaitDays = KcpUtility.loadIntFromCache(context, GSON_KEY_LATER_WAIT, -1);
            boolean pastWaitDays = false;
            if (laterWaitDays >= waitDays) {
                pastWaitDays = true;
                KcpUtility.removeFromCache(context, GSON_KEY_LATER_WAIT);
            }
            //allow the dialog to be created every 3 days or if the user pressed LATER after being shown the dialog initially
            //and the later wait time has passed.
            if (amountOfTimesOpened % launchTimes == 0 || pastWaitDays) {
                ratingListener.onCreateRatingDialog(new RatingListener.DialogActionListener() {
                    @Override
                    public void onDialogOK() {
                        KcpUtility.cacheToPreferences(context, Constants.KEY_APP_VERSION, BuildConfig.VERSION_NAME);
                        KcpUtility.cacheToPreferences(context, AppRatingManager.GSON_KEY_USER_RATED, true);
                        ratingListener.onDestroyRatingDialog();
                    }

                    @Override
                    public void onDialogDeferred() {
                        KcpUtility.cacheToPreferences(context, AppRatingManager.GSON_KEY_LATER_WAIT, 0);
                    }
                });
            }
        }
    }

    public static void trackCrashReport(Context context) {
        if (INSTANCE.dayThreshold == 0) {
            resetLastLaunchCycle(context);
        }
        KcpUtility.saveGson(context, AppRatingManager.GSON_KEY_CRASHED_DATE, Calendar.getInstance().getTime());
    }

    /**
     * Resets the last launch cycle and removes the LATER wait flag as if the user has never seen the dialog before.
     * @param context Context object.
     */
    private static void resetLastLaunchCycle(Context context) {
        int currentLaunchCycle = KcpUtility.loadIntFromCache(context, GSON_KEY_LAUNCH_TIMES, 0);
        if (currentLaunchCycle % INSTANCE.launchTimes == 0) {
            currentLaunchCycle = currentLaunchCycle - INSTANCE.launchTimes + 1;
        } else {
            currentLaunchCycle = currentLaunchCycle - (currentLaunchCycle % INSTANCE.launchTimes) + 1;
        }
        KcpUtility.cacheToPreferences(context, GSON_KEY_LAUNCH_TIMES, currentLaunchCycle);
        KcpUtility.removeFromCache(context, GSON_KEY_LATER_WAIT);

    }

    /**
     * Checks to see if the current usage day is a new day.
     * @param context Context object.
     * @return true if the day is new, false otherwise.
     */
    private static boolean isNewDay(Context context) {
        Date lastUsedDate = KcpUtility.getObjectFromCache(context, GSON_KEY_LAST_INC_DATE, Date.class);
        if (lastUsedDate == null) return true;
        Calendar todaysCal = Calendar.getInstance();
        Calendar lastUsedCal = Calendar.getInstance();
        lastUsedCal.setTime(lastUsedDate);

        return (todaysCal.get(Calendar.DAY_OF_YEAR) != lastUsedCal.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * Checks to see if the date of the last crash is past the minimum waiting day as set by <br />{@link com.ivanhoecambridge.mall.rating.AppRatingManager.Builder#minDaysToWait(int)}. The default is 0.
     * @param context Context object.
     * @return true if date has cleared the new day threshold cycle, false otherwise.
     */
    private static boolean isCrashDatePastMinDays(Context context) {
        Date lastCrashedDate = KcpUtility.getObjectFromCache(context, GSON_KEY_CRASHED_DATE, Date.class);
        if (lastCrashedDate == null || INSTANCE.dayThreshold == 0) return true;
        Date todaysDate = Calendar.getInstance().getTime();
        Calendar lastCrashedPlusMin = Calendar.getInstance();
        lastCrashedPlusMin.setTime(lastCrashedDate);
        lastCrashedPlusMin.add(Calendar.DAY_OF_MONTH, INSTANCE.dayThreshold);
        return (todaysDate.after(lastCrashedPlusMin.getTime()));

    }


    public static class Builder {
        private int     dayThreshold;
        private int     launchTimes;
        private int     waitDays;
        private boolean resetOnUpgrade;
        private boolean shouldKeepTrackOfCrashes;

        /**
         * Number of days to wait after a crash has occurred. If the specified amount is 0
         * rating will be delayed until a new launch cycle has passed.
         * @param days Number of days.
         */
        private Builder minDaysToWait(int days) {
            this.dayThreshold = days;
            return this;
        }

        /**
         * Number of app launch times to wait before showing the dialog.
         * @param launchTimes Number of launch times.
         */
        private Builder launchTimes(int launchTimes) {
            this.launchTimes = launchTimes;
            return this;
        }

        /**
         * When the user presses LATER on the dialog. The number of days to wait before showing the dialog again.
         * @param waitDays Number of wait days.
         */
        private Builder laterWaits(int waitDays) {
            this.waitDays = waitDays;
            return this;
        }

        /**
         * Set to true if the user should be shown the app rate dialog again when the app has upgraded.
         * @param resetOnUpgrade boolean flag, true if dialog should be shown again, false if not.
         * @return
         */
        private Builder resetOnUpgrade(boolean resetOnUpgrade) {
            this.resetOnUpgrade = resetOnUpgrade;
            return this;
        }

        /**
         * Set to true to avoid showing the app rating dialog if the app has crashed and has not past the minWaitDays cycle.
         * <br /> The default is 0.
         * @param shouldKeepTrackOfCrashes true if you it should keep track of crashes, false if not.
         */
        private Builder delayRatingIfAppCrashes(boolean shouldKeepTrackOfCrashes) {
            this.shouldKeepTrackOfCrashes = shouldKeepTrackOfCrashes;
            return this;
        }
        public AppRatingManager build() {
            return new AppRatingManager(this);
        }
       
    }



}
