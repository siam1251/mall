package com.ivanhoecambridge.mall.managers;

import android.app.Activity;
import android.content.Context;

import com.ivanhoecambridge.mall.views.BadgeView;

/**
 * Created by Kay on 2016-07-05.
 */
public class SidePanelManagers {

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
                SidePanelManagers.this.badgeDeals.setBadgeText(FavouriteManager.getInstance(context).getDealFavSize());
                SidePanelManagers.this.badgeEvents.setBadgeText(FavouriteManager.getInstance(context).getEventAnnouncementFavSize());
                SidePanelManagers.this.badgeStores.setBadgeText(FavouriteManager.getInstance(context).getStoreFavSize());
                SidePanelManagers.this.badgeInterests.setBadgeText(FavouriteManager.getInstance(context).getInterestFavSize());
            }
        });
    }

    public interface FavouriteListener {
        void onFavouriteChanged();
    }
}
