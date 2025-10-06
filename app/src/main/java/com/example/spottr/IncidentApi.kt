package com.example.spottr

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface IncidentApi {

    @POST("api/report-incident")
    fun reportIncident(@Body incident: Incident): Call<ApiResponse>

    @GET("api/incidents")
    fun getIncidents(): Call<List<Incident>>
}

data class ApiResponse(
    val message: String,
    val id: String? = null
)