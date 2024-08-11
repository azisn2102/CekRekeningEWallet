package com.azissn.darinol

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var editTextAccountNumber: EditText
    private lateinit var buttonFetch: Button
    private lateinit var textViewResult: TextView
    private lateinit var radioGroupAccountType: RadioGroup
    private lateinit var radioBankAccount: RadioButton
    private lateinit var radioEWalletAccount: RadioButton
    private lateinit var autoCompleteTextView: AutoCompleteTextView

    private lateinit var bankList: List<Bank>
    private lateinit var eWalletList: List<ElectronicWallet>

    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Installation of UI components
        radioGroupAccountType = findViewById(R.id.radioGroupAccountType)
        radioBankAccount = findViewById(R.id.radioBankAccount)
        radioEWalletAccount = findViewById(R.id.radioEWalletAccount)
        editTextAccountNumber = findViewById(R.id.editTextAccountNumber)
        buttonFetch = findViewById(R.id.buttonFetch)
        textViewResult = findViewById(R.id.textViewResult)
        autoCompleteTextView = findViewById(R.id.choose_autocomplete)

        radioBankAccount.isChecked
        fetchBankList()

        // Set listener for button
        buttonFetch.setOnClickListener {
            val selectedItem = autoCompleteTextView.text.toString()
            val bankCode = if (radioBankAccount.isChecked) {
                bankList.find { it.namaBank == selectedItem }?.kodeBank ?: ""
            } else {
                eWalletList.find { it.namaBank == selectedItem }?.kodeBank ?: ""
            }
            val accountNumber = editTextAccountNumber.text.toString()
            fetchAccountData(bankCode, accountNumber)
        }

        // Set listener for RadioGroup
        radioGroupAccountType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioBankAccount -> {
                    // If choose Bank Account
                    autoCompleteTextView.hint = getString(R.string.input_bank_name)
                    editTextAccountNumber.hint = getString(R.string.input_account_number)
                    autoCompleteTextView.text.clear()
                    editTextAccountNumber.text.clear()
                    textViewResult.text = null
                    fetchBankList()
                }

                R.id.radioEWalletAccount -> {
                    // If choose E-Wallet
                    autoCompleteTextView.hint = getString(R.string.input_e_wallet_name)
                    editTextAccountNumber.hint = getString(R.string.input_e_wallet_number)
                    autoCompleteTextView.text.clear()
                    editTextAccountNumber.text.clear()
                    textViewResult.text = null
                    fetchEWalletList()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.exit_toast), Toast.LENGTH_SHORT).show()

        // Reset to start condition after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

    private fun fetchBankList() {
        // Call API to get List of Banks
        RetrofitClient.instance.getBankList().enqueue(object : Callback<BankListResponse> {
            override fun onResponse(
                call: Call<BankListResponse>,
                response: Response<BankListResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    bankList = response.body()!!.data

                    if (bankList.isNotEmpty()) {
                        // Set up adapter
                        val adapter = BankArrayAdapter(
                            this@MainActivity,
                            bankList
                        )
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                        spinnerBank.adapter = adapter
                        autoCompleteTextView.setAdapter(adapter)
                    } else {
                        textViewResult.text = getString(R.string.no_bank_found)
                    }
                } else {
                    textViewResult.text = response.body()?.msg
                }
            }

            override fun onFailure(call: Call<BankListResponse>, t: Throwable) {
                handleError(t)
            }
        })
    }

    private fun fetchEWalletList() {
        // Call API to get List of E-Wallets
        RetrofitClient.instance.getEwalletList()
            .enqueue(object : Callback<ElectronicWalletListResponse> {
                override fun onResponse(
                    call: Call<ElectronicWalletListResponse>,
                    response: Response<ElectronicWalletListResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        eWalletList = response.body()!!.data

                        if (eWalletList.isNotEmpty()) {
                            // Set up adapter
                            val adapter = ElectronicWalletArrayAdapter(
                                this@MainActivity,
                                eWalletList
                            )
                            autoCompleteTextView.setAdapter(adapter)
                        } else {
                            textViewResult.text = getString(R.string.no_e_wallet_found)
                        }
                    } else {
                        textViewResult.text = response.body()?.msg
                    }
                }

                override fun onFailure(call: Call<ElectronicWalletListResponse>, t: Throwable) {
                    handleError(t)
                }
            })
    }

    private fun fetchAccountData(bankCode: String, accountNumber: String) {

        if (radioBankAccount.isChecked) {
            // If choose Bank Account
            RetrofitClient.instance.getBankAccountData(bankCode, accountNumber)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {
                        handleResponse(response)
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        handleError(t)
                    }
                })
        } else if (radioEWalletAccount.isChecked) {
            // If choose E-wallet Account
            RetrofitClient.instance.getEwalletAccountData(bankCode, accountNumber)
                .enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(
                        call: Call<ApiResponse>,
                        response: Response<ApiResponse>
                    ) {
                        handleResponse(response)
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        handleError(t)
                    }
                })
        }
    }


    private fun handleResponse(response: Response<ApiResponse>) = if (response.isSuccessful) {
        // If success, then get data from response
        val apiResponse = response.body()

        if (apiResponse?.status == true) {

            apiResponse.data.let { bankData ->
                // Show result to the TextView
                val result: String
                if (radioBankAccount.isChecked) {
                    result = """
                ${apiResponse.msg}
                
                Bank Code: ${bankData.bankcode}
                Bank Name: ${bankData.bankname}
                Account Number: ${bankData.accountnumber}
                Account Name: ${bankData.accountname}
            """.trimIndent()
                } else {
                    result = """
                ${apiResponse.msg}
                
                E-Wallet Code: ${bankData.bankcode}
                E-Wallet Name: ${bankData.bankcode}
                E-Wallet Number: ${bankData.accountnumber}
                E-Wallet User ID: ${bankData.accountname}
            """.trimIndent()
                }

                textViewResult.text = result
            }
        } else {
            // If response is not success
            textViewResult.text = apiResponse?.msg
        }
    } else {
        // If was an error in response
        if (response.body()?.status != null) {
            textViewResult.text =
                getString(R.string.response_error, response.body()?.status.toString())
        } else {
            if (autoCompleteTextView.text.toString() == "" && editTextAccountNumber.text.toString() == "") {
                textViewResult.text = getString(R.string.do_not_empty)
            } else {
                textViewResult.text = getString(R.string.please_fill_correctly)
            }
        }
    }

    private fun handleError(t: Throwable) {
        // If was an error while call API
        textViewResult.text = getString(R.string.network_error, t.message)
    }
}
