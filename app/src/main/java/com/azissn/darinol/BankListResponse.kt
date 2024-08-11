package com.azissn.darinol

data class BankListResponse(
    val status: Boolean,
    val msg: String,
    val data: List<Bank>

)
