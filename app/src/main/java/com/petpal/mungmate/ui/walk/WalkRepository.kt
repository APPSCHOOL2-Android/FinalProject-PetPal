package com.petpal.mungmate.ui.walk

import com.petpal.mungmate.model.KakaoSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WalkRepository {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://dapi.kakao.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService: KakaoApiService = retrofit.create(KakaoApiService::class.java)

    suspend fun searchPlacesByKeyword(latitude: Double, longitude: Double, query: String): KakaoSearchResponse {
        return apiService.searchPlacesByKeyword(latitude, longitude, query = query)
    }
}