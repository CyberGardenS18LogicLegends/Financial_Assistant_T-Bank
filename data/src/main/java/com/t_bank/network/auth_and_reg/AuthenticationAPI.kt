package com.t_bank.network.auth_and_reg

import com.t_bank.common_models.UserDataClass
import com.t_bank.network.ApiConstants
import com.t_bank.network.auth_and_reg.request_models.Request
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationAPI {

    @POST(ApiConstants.BASE_URL + "login")
    suspend fun authenticate(
        @Body request: Request
    ): UserDataClass
}