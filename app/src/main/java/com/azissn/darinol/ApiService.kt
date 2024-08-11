package com.azissn.darinol

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("getBankAccount")
    fun getBankAccountData(
        @Query("bankCode") bankCode: String,
        @Query("accountNumber") accountNumber: String
    ): Call<ApiResponse>

    @GET("getEwalletAccount") // Endpoint untuk akun e-wallet
    fun getEwalletAccountData(
        @Query("bankCode") bankCode: String,
        @Query("accountNumber") accountNumber: String
    ): Call<ApiResponse>

    @GET("listBank")
    fun getBankList(): Call<BankListResponse>

    @GET("listEwallet")
    fun getEwalletList(): Call<ElectronicWalletListResponse>

}

