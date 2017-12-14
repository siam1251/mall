package com.ivanhoecambridge.mall.fragments.uiviews;

import android.content.Context;

import com.ivanhoecambridge.kcpandroidsdk.managers.KcpSocialFeedManager;
import com.ivanhoecambridge.kcpandroidsdk.models.KcpContentPage;

import java.util.ArrayList;

/**
 * Created by petar on 2017-12-07.
 */

public interface HomeView extends BaseView {
    Context getContext();
    void onAdapterDataRefresh(String mode, ArrayList<KcpContentPage> kcpContentPages);
    void onNewsAndDealsUpdated(boolean isAddedData, String mode, ArrayList<KcpContentPage> kcpContentPages);
    void onSocialFeedUpdated(KcpSocialFeedManager kcpSocialFeedManager, int socialFeedType);
    void updateProfileData();
    void onAllDataDownloadSuccess();
    void onDataDownloadFailure(int failedOn);
}
