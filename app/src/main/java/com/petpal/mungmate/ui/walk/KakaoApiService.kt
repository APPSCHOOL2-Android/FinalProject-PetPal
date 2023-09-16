package com.petpal.mungmate.ui.walk

import com.petpal.mungmate.model.KakaoSearchResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface KakaoApiService {
    @Headers("Authorization: KakaoAK ${api.KAKAO_API_KEY}")
    @GET("/v2/local/search/keyword.json")
    suspend fun searchPlacesByKeyword(
        @Query("y") latitude: Double,
        @Query("x") longitude: Double,
        @Query("radius") radius: Int = 3000,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        //@Query("size") size: Int=30
    ): KakaoSearchResponse
}