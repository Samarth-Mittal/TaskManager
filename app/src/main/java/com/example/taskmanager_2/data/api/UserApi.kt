package com.example.taskmanager_2.data.api

import com.example.taskmanager_2.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("/login")
    suspend fun doLogin(@Body loginUser: LoginUser): Response<LoginResponse>

    @POST("/registration")
    suspend fun doRegister(@Body signUpUser: SignUpUser): Response<JSONResponse>

    @GET("/admin/user/{userID}")
    suspend fun getUser(@Path("userID") id: String?): Response<User>

    @GET("/admin/{userID}/teams")
    suspend fun getTeamIDs(@Path("userID") id: String?): Response<Teams>

    @GET("/admin/team/{teamID}")
    suspend fun getTeamDetails(@Path("teamID") id: String?): Response<TeamDetails>
}