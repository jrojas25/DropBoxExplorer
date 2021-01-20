package com.jmr.dropboxbrowser.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.dropbox.core.android.Auth
import com.jmr.dropboxbrowser.BuildConfig
import com.jmr.dropboxbrowser.R
import com.jmr.dropboxbrowser.databinding.ActivityLoginBinding
import com.jmr.dropboxbrowser.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        initObserver()

        binding.loginButton.setOnClickListener {
            loginViewModel.isLoginFlow = true
            Auth.startOAuth2Authentication(this, BuildConfig.DROPBOX_APP_KEY)
        }
    }

    override fun onResume() {
        super.onResume()
        loginViewModel.checkTokenStatus()

    }

    private fun initObserver() {
        loginViewModel.state.observe(this, Observer { state ->
            when(state){
                LoginViewModel.State.Error -> Toast.makeText(this, R.string.login_token_error, Toast.LENGTH_SHORT).show()
                LoginViewModel.State.Success -> navigateToHome()
            }
        })
    }

    private fun navigateToHome() {
        HomeActivity.start(this)
        finish()
    }


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}