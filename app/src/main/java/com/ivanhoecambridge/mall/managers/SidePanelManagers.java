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
    public final static int BADGE_DEALS = 1;
    public final static int BADGE_EVENTS = 2;
    public final static int BADGE_STORES = 3;
    public final static int BADGE_INTERESTS = 4;

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

    public void updateInterests(int size) {
        badgeInterests.setBadgeText(size);
    }

    public void updateDeals(int size) {
        badgeDeals.setBadgeText(size);
    }

    public void updateStores(int size) {
        badgeStores.setBadgeText(size);
    }

    public void updateFavourites(int favouriteType, int favouriteSize) {
        switch (favouriteType) {
            case BADGE_DEALS:
                badgeDeals.setBadgeText(favouriteSize);
                break;
            case BADGE_EVENTS:
                badgeEvents.setBadgeText(favouriteSize);
                break;
            case BADGE_STORES:
                badgeStores.setBadgeText(favouriteSize);
                break;
            case BADGE_INTERESTS:
                badgeInterests.setBadgeText(favouriteSize);
                break;
        }
    }

    public interface FavouriteListener {
        void onFavouriteChanged();
    }
}
