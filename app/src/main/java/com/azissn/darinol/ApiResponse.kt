package com.azissn.darinol

data class ApiResponse(
    val status: Boolean,
    val msg: String,
    val data: BankAccountData
)

