package com.kontrakanprojects.tyreom.view.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.kontrakanprojects.tyreom.databinding.ActivityAuthBinding
import com.kontrakanprojects.tyreom.db.Login
import com.kontrakanprojects.tyreom.db.User
import com.kontrakanprojects.tyreom.session.UserPreference
import com.kontrakanprojects.tyreom.utils.hideKeyboard
import com.kontrakanprojects.tyreom.view.main.MainActivity

class AuthActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel>()
    private lateinit var binding: ActivityAuthBinding

    private val loginValid = true

    companion object {
        private const val NIK_NOT_NULL = "NIK tidak boleh kosong!"
        private const val PASSWORD_NOT_NULL = "Pas  sword tidak boleh kosong!"
        private const val MIN_COUNTER_LENGTH_PASS = "Minimal 5 karakter password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUp()
        textWatcher()
    }

    private fun setUp() {
        with(binding) {
            btnLogin.setOnClickListener {
                val nik = edtNIK.text.toString().trim()
                val password = edtPassword.text.toString().trim()

                when {
                    nik.isEmpty() -> tiNIK.error = NIK_NOT_NULL
                    password.isEmpty() -> tiPassword.error = PASSWORD_NOT_NULL
                    else -> {
                        isLogin(true)

                        val params = HashMap<String, String>()
                        params["nik"] = nik
                        params["password"] = password

                        login(params)
                        hideKeyboard(this@AuthActivity)
                    }
                }
            }
        }
    }

    private fun login(params: HashMap<String, String>) {
        viewModel.login(params).observe(this@AuthActivity, { result ->
            isLogin(false)
            if (result != null) {
                if (result.status == 200) {
                    // cek id role
                    val resultAuth = result.results
                    if (resultAuth?.idRole == MainActivity.ROLE_ADMIN) {
                        UserPreference(this@AuthActivity).apply {
                            setUser(
                                User(
                                    idUser = resultAuth.idAdmin,
                                    idRole = resultAuth.idRole,
                                    namaUser = resultAuth.namaAdmin
                                )
                            )
                            setLogin(Login(loginValid))
                        }
                    } else {
                        UserPreference(this@AuthActivity).apply {
                            setUser(
                                User(
                                    idUser = resultAuth?.nik?.toInt(),
                                    idRole = resultAuth?.idRole,
                                    namaUser = resultAuth?.namaKaryawan
                                )
                            )
                            setLogin(Login(loginValid))
                        }
                    }

                    startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                    finish()
                } else {
                    with(binding) {
                        tvFailLogin.text = result.message
                        tvFailLogin.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun textWatcher() {
        with(binding) {
            edtNIK.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length!! == 0) {
                        binding.tiNIK.error = NIK_NOT_NULL
                    } else {
                        binding.tiNIK.error = null
                    }
                }
            })

            edtPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    when {
                        s?.length!! < 5 -> {
                            binding.tiPassword.error = MIN_COUNTER_LENGTH_PASS
                        }
                        s.isNullOrEmpty() -> {
                            binding.tiPassword.error = PASSWORD_NOT_NULL
                        }
                        else -> {
                            binding.tiPassword.error = null
                        }
                    }
                }
            })
        }
    }

    private fun isLogin(state: Boolean) {
        with(binding) {
            if (state) {
                progressBar.visibility = View.VISIBLE
                btnLogin.visibility = View.GONE

                // clear message error
                tvFailLogin.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                btnLogin.visibility = View.VISIBLE
            }
        }
    }
}