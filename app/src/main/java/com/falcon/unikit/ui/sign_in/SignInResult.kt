package com.falcon.unikit.ui.sign_in

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData (
    val userId: String,
    val userName: String?,
    val profilePicUrl: String?
)