package com.sonzaix.streaming.core.network

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.*

interface SonzaiApiService {

    @GET("/")
    suspend fun checkApi(): Response<JsonElement>

    @GET("{provider}/languages")
    suspend fun getLanguages(@Path("provider") provider: String): Response<JsonElement>

    @GET("{provider}/home")
    suspend fun getHome(
        @Path("provider") provider: String,
        @QueryMap queries: Map<String, String> = emptyMap()
    ): Response<JsonElement>

    @GET("{provider}/new")
    suspend fun getNew(
        @Path("provider") provider: String,
        @QueryMap queries: Map<String, String> = emptyMap()
    ): Response<JsonElement>

    @GET("{provider}/populer")
    suspend fun getPopular(
        @Path("provider") provider: String,
        @QueryMap queries: Map<String, String> = emptyMap()
    ): Response<JsonElement>

    @GET("{provider}/search")
    suspend fun search(
        @Path("provider") provider: String,
        @QueryMap queries: Map<String, String>
    ): Response<JsonElement>

    @GET("{provider}/detail")
    suspend fun getDetail(
        @Path("provider") provider: String,
        @QueryMap queries: Map<String, String>
    ): Response<JsonElement>

    @GET("{provider}/stream")
    suspend fun getStream(
        @Path("provider") provider: String,
        @QueryMap queries: Map<String, String>
    ): Response<JsonElement>

    @GET("dramanova/play")
    suspend fun dramanovaPlay(@QueryMap queries: Map<String, String>): Response<JsonElement>

    @GET("{provider}/{endpoint}")
    suspend fun getGeneric(
        @Path("provider") provider: String,
        @Path("endpoint") endpoint: String,
        @QueryMap queries: Map<String, String> = emptyMap()
    ): Response<JsonElement>
}
