package com.ivanhoecambridge.mall.managers;

import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;

/**
 * Created by petar on 2018-01-10.
 */

public interface FavouritesListener {
    void onFavouriteAdded(KcpContentPage kcpContent);
    void onFavouriteRemoved(KcpContentPage kcpContent);
}
