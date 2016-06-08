package com.kineticcafe.kcpmall.kcpData;

import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpContentPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationPage;
import com.kineticcafe.kcpandroidsdk.models.KcpNavigationRoot;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
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

    @GET
    Call<KcpContentPage> getCategories(
            @Url String url,
            @Query("page") String page,
            @Query("perpage") String perpage);

    @GET
    Call<KcpContentPage> getCorePage(
            @Url String url);

    @GET
    Call<KcpContentPage> getPlacesWithCategories(
            @Url String url,
            @Query("page") String page,
            @Query("perpage") String perpage,
            @Query("category_ids") String categoryIds);

    @POST
    Call<KcpContentPage> postInterestedStores(
            @Url String url,
            @Body String multi_like);

    @POST
    Call<KcpContentPage> postInterestedStores(
            @Url String url,
            @Body HashMap multiLike);

    @POST
    Call<KcpContentPage> postInterestedStores(
            @Url String url,
            @Body KcpCategoryManager.MultiLike multiLike);



    /*@POST
    Call<KcpContentPage> postInterestedStores(
            @Url String url,
            @Field("multi_like") String body);*/

}
