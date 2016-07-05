package com.kineticcafe.kcpmall.managers;

import android.app.Activity;
import android.content.Context;

import com.kineticcafe.kcpmall.views.BadgeView;

/**
 * Created by Kay on 2016-07-05.
 */
public class SidePanelManagers {

//    private static SidePanelManagers sSidePanelManager = new SidePanelManagers();
//    public static SidePanelManagers getInstance() {
//        return sSidePanelManager;
//    }


    private BadgeView badgeDeals;
    private BadgeView badgeEvents;
    private BadgeView badgeStores;
    private BadgeView badgeInterests;

    public SidePanelManagers(final Context context, BadgeView badgeDeals, BadgeView badgeEvents, BadgeView badgeStores, BadgeView badgeInterests){
        this.badgeDeals = badgeDeals;
        this.badgeEvents = badgeEvents;
        this.badgeStores = badgeStores;
        this.badgeInterests = badgeInterests;
        setUpBadges(context);

        FavouriteManager.getInstance(context).setFavouriteListener(new FavouriteListener() {
            @Override
            public void onFavouriteChanged() {
                setUpBadges(context);
            }
        });
    }

    private void setUpBadges(final Context context){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SidePanelManagers.this.badgeDeals.setBadgeText(FavouriteManager.getInstance(context).getDealFavSize(context));
                SidePanelManagers.this.badgeEvents.setBadgeText(FavouriteManager.getInstance(context).getEventAnnouncementFavSize(context));
                SidePanelManagers.this.badgeStores.setBadgeText(FavouriteManager.getInstance(context).getStoreFavSize(context));
                SidePanelManagers.this.badgeInterests.setBadgeText(FavouriteManager.getInstance(context).getInterestFavSize(context));
            }
        });
    }

    public interface FavouriteListener {
        void onFavouriteChanged();
    }
}
