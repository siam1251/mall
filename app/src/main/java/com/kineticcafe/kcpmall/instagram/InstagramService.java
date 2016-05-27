package com.kineticcafe.kcpmall.instagram;

import com.kineticcafe.kcpmall.instagram.model.Feed;
import com.kineticcafe.kcpmall.instagram.model.Liked;
import com.kineticcafe.kcpmall.instagram.model.Profile;
import com.kineticcafe.kcpmall.instagram.model.Recent;
import com.kineticcafe.kcpmall.instagram.model.SearchUserResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kay on 2016-05-13.
 */
public interface InstagramService {
    @GET("/users/{user_id}")
    public Call<Profile> getUser(
            @Path("user_id") String userId,
            @Query("access_token") String accessToken);

    @GET("/users/self/feed")
    public Call<Feed> getFeed(
            @Query("access_token") String accessToken,
            @Query("count") Integer count,
            @Query("min_id") String minId,
            @Query("max_id") String maxId);

    @GET("users/{user_id}/media/recent")
    public Call<Recent> getRecentWithAccessToken(
            @Path("user_id") String userId,
            @Query("access_token") String accessToken,
            @Query("count") Integer count,
            @Query("min_id") String minId,
            @Query("max_id") String maxId,
            @Query("min_timestamp") Long minTimestamp,
            @Query("max_timestamp") Long maxTimestamp);

    @GET("users/{user_id}/media/recent")
    public Call<Recent> getRecentWithClientId(
            @Path("user_id") String userId,
            @Query("client_id") String clientId,
            @Query("count") Integer count,
            @Query("min_id") String minId,
            @Query("max_id") String maxId,
            @Query("min_timestamp") Long minTimestamp,
            @Query("max_timestamp") Long maxTimestamp);

    @GET("/users/self/media/liked")
    public Call<Liked> getLiked(
            @Query("access_token") String accessToken,
            @Query("count") Integer count,
            @Query("max_like_id") String maxLikeId);

    @GET("/users/search")
    public Call<SearchUserResponse> search(
            @Query("access_token") String accessToken,
            @Query("q") String query,
            @Query("count") Integer count);
}
