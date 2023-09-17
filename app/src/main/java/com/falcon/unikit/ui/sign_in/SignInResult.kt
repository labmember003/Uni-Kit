package com.falcon.unikit.ui.sign_in

data class SignInResult(
    val data: UserDataBase?,
    val errorMessage: String?
)

data class UserDataBase (
    val userId: String,
    val userName: String?,
    val profilePicUrl: String?
)