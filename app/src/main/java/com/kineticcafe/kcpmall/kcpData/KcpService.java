package com.kineticcafe.kcpmall.kcpData;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Kay on 2016-05-13.
 */
public interface KcpService {
    @GET
    Call<KcpNavigationRoot> getNavigationRoot(@Url String url);

    @GET
    Call<KcpNavigationPage> getNavigationPage(@Url String url);

    @GET
    Call<KcpContentPage> getContentPage(
            @Url String url);

    @GET
    Call<KcpContentPage> getContentPage(
            @Url String url,
            @Query("perpage") String perpage);
}
