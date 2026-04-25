package com.klodit.almizan.data.auth

class AuthRepository(private val api: AuthApi) {

    suspend fun register(request: RegisterRequest): RegisterResponse =
        api.register(request)

    suspend fun login(email: String, password: String): LoginResponse =
        api.login(LoginRequest(email, password))

    suspend fun forgotPassword(email: String): MessageResponse =
        api.forgotPassword(ForgotPasswordRequest(email))

    suspend fun verifyToken(token: String): MessageResponse =
        api.verifyToken(VerifyTokenRequest(token))

    suspend fun resetPassword(token: String, newPassword: String): MessageResponse =
        api.resetPassword(ResetPasswordRequest(token, newPassword))
}