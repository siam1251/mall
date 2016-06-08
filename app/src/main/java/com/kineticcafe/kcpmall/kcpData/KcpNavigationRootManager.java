package com.kineticcafe.kcpmall.kcpData;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.kineticcafe.kcpandroidsdk.logger.Logger;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;
import com.kineticcafe.kcpandroidsdk.service.ServiceFactory;
import com.kineticcafe.kcpmall.R;
import com.kineticcafe.kcpmall.activities.Constants;
import com.kineticcafe.kcpmall.factory.HeaderFactory;
import com.kineticcafe.kcpmall.fragments.HomeFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kay on 2016-05-31.
 */
public class KcpNavigationRootManager {

    private Context mContext;
    private KcpService mKcpService;
    protected Logger logger = null;
    private Handler mHandler;
    private ArrayList<KcpContentPage> mKcpContentPageList;

    public static final int DOWNLOAD_FAILED = -1;
    public static final int DOWNLOAD_STARTED = 1;
    public static final int DOWNLOAD_COMPLETE = 2;
    public static final int DATA_ADDED = 3;
    public static final int TASK_COMPLETE = 4;

    public KcpNavigationRootManager(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
        logger = new Logger(getClass().getName());
    }

    public KcpService getKcpService(){
        if(mKcpService == null) mKcpService = ServiceFactory.createRetrofitService(mContext, new HeaderFactory().getHeaders(), KcpService.class, Constants.URL_BASE);
        return mKcpService;
    }

    public void downloadNewsAndDeal(){
        Call<KcpNavigationRoot> call = getKcpService().getNavigationRoot(Constants.URL_NAVIGATION_ROOT);
        call.enqueue(new Callback<KcpNavigationRoot>() {
            @Override
            public void onResponse(Call<KcpNavigationRoot> call, Response<KcpNavigationRoot> response) {
                if(response.isSuccessful()){
                    KcpNavigationRoot kcpNavigationRoot = KcpNavigationRoot.getInstance();
                    kcpNavigationRoot = response.body();
                    for(int i = 0; i < kcpNavigationRoot.getNavigationPageListSize(); i++){
                        KcpNavigationPage kcpNavigationPage = kcpNavigationRoot.getNavigationPage(i);
                        String fullHref = kcpNavigationPage.getFullLink();
                        String mode = kcpNavigationPage.getMode();
                        downloadNavigationPage(fullHref, mode);
                    }
                } else handleState(DOWNLOAD_FAILED);
            }

            @Override
            public void onFailure(Call<KcpNavigationRoot> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    private void downloadNavigationPage(String url, final String mode){
        Call<KcpNavigationPage> call = getKcpService().getNavigationPage(url);
        call.enqueue(new Callback<KcpNavigationPage>() {
            @Override
            public void onResponse(Call<KcpNavigationPage> call, Response<KcpNavigationPage> response) {
                if(response.isSuccessful()){
                    KcpNavigationPage kcpNavigationPageResponse = response.body();
                    KcpNavigationRoot.getInstance().setNavigationpage(mode, kcpNavigationPageResponse);
                    String fullHref = kcpNavigationPageResponse.getSelfLink() + Constants.URL_VIEW_ALL_CONTENT;
                    downloadContents(fullHref, mode);
                } else handleState(DOWNLOAD_FAILED);
            }

            @Override
            public void onFailure(Call<KcpNavigationPage> call, Throwable t) {
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public void downloadContents(String url, final String mode){
        Call<KcpContentPage> call = getKcpService().getContentPage(url);
        call.enqueue(new Callback<KcpContentPage>() {
            @Override
            public void onResponse(Call<KcpContentPage> call, Response<KcpContentPage> response) {
                if (response.isSuccessful()) {
                    try {
                        final KcpContentPage kcpContentPageResponse = response.body();
                        KcpNavigationPage kcpNavigationPage = KcpNavigationRoot.getInstance().getNavigationpage(mode);
                        boolean isDataAdded = kcpNavigationPage.setKcpContentPage(kcpContentPageResponse);
                        if(isDataAdded) {
                            mKcpContentPageList = kcpContentPageResponse.getContentPageList();
                            handleState(DATA_ADDED);
                        } else {
                            handleState(DOWNLOAD_COMPLETE, mode);
                        }
                    } catch (Exception e) {
                        logger.error(e);
                    }
                } else handleState(DOWNLOAD_FAILED);
            }

            @Override
            public void onFailure(Call<KcpContentPage> call, Throwable t) {
                logger.error(t);
                handleState(DOWNLOAD_FAILED);
            }
        });
    }

    public ArrayList<KcpContentPage> getKcpContentPageList(){
        if(mKcpContentPageList == null) return new ArrayList<>();
        return mKcpContentPageList;
    }

    private void handleState(int state){
        handleState(state, null);
    }
    private void handleState(int state, @Nullable String mode){
        if(mHandler == null) return;
        Message message = new Message();
        message.arg1 = state;
        message.obj = mode;
        switch (state){
            case DOWNLOAD_FAILED:
                break;
            case DOWNLOAD_COMPLETE:
                break;
            case DATA_ADDED:
                break;
        }
        mHandler.sendMessage(message);
    }

}
