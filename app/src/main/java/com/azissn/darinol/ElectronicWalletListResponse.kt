package com.azissn.darinol

data class ElectronicWalletListResponse(
    val status: Boolean,
    val msg: String,
    val data: List<ElectronicWallet>

)
