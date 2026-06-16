package com.klodit.almizan.data.auth

import okhttp3.MultipartBody
import okhttp3.ResponseBody

class AuthRepository(private val api: AuthApi) {

    suspend fun register(request: RegisterRequest): RegisterResponse =
        api.register(request)

    //suspend fun login(email: String, password: String): LoginResponse =
    //    api.login(LoginRequest(email, password))
    suspend fun login(email: String, password: String): Pair<LoginResponse, String> {
        val response = api.loginRaw(LoginRequest(email, password))

        // Log tous les headers set-cookie
        response.headers().values("set-cookie").forEach {
            android.util.Log.d("AUTH_DEBUG", "set-cookie header: $it")
        }

        val cookie = response.headers().values("set-cookie")
            .firstOrNull { it.contains("access_token=") }  // ← contains au lieu de startsWith
        val token = cookie
            ?.substringAfter("access_token=")
            ?.substringBefore(";")
            ?: ""

        android.util.Log.d("AUTH_DEBUG", "extracted token = '${token.take(30)}'")
        return Pair(response.body()!!, token)
    }

    suspend fun forgotPassword(email: String): MessageResponse =
        api.forgotPassword(ForgotPasswordRequest(email))

    suspend fun verifyToken(token: String): MessageResponse =
        api.verifyToken(VerifyTokenRequest(token))

    suspend fun resetPassword(token: String, newPassword: String): MessageResponse =
        api.resetPassword(ResetPasswordRequest(
            token               = token,
            newPassword         = newPassword,
            confirmeNewPassword = newPassword
        ))


    suspend fun uploadDocument(token: String, file: MultipartBody.Part): ResponseBody =
        api.uploadDocument("Bearer $token", file)


    suspend fun logout() {
        api.logout()
    }

    suspend fun sendOtp(email: String): OtpResponse =
        api.sendOtp(SendOtpRequest(email))

    suspend fun verifyOtp(email: String, code: String): OtpResponse =
        api.verifyOtp(VerifyOtpRequest(email, code))


}