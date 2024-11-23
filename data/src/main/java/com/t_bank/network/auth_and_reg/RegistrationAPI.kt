package com.t_bank.network.auth_and_reg

import com.t_bank.common_models.UserDataClass
import com.t_bank.network.ApiConstants
import com.t_bank.network.auth_and_reg.request_models.Request
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RegistrationAPI {

    @POST(ApiConstants.BASE_URL + "register")
    suspend fun registration(
        @Body request: Request
    ) : UserDataClass

}