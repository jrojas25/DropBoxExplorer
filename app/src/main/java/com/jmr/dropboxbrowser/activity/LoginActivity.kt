package com.jmr.dropboxbrowser.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dropbox.core.android.Auth
import com.jmr.domain.usecases.HasTokenUseCase
import com.jmr.domain.usecases.SaveTokenUseCase
import com.jmr.dropboxbrowser.BuildConfig
import com.jmr.dropboxbrowser.R
import com.jmr.dropboxbrowser.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var hasTokenUseCase: HasTokenUseCase

    @Inject
    lateinit var saveTokenUseCase: SaveTokenUseCase

    private lateinit var binding: ActivityLoginBinding

    private var isLoginFlow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            isLoginFlow = true
            Auth.startOAuth2Authentication(this, BuildConfig.DROPBOX_APP_KEY)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasTokenUseCase()) {
            val authToken = Auth.getOAuth2Token()
            when{
                !authToken.isNullOrEmpty() && isLoginFlow -> {
                    saveTokenUseCase(authToken)
                    isLoginFlow = false
                    navigateToHome()
                }
                authToken.isNullOrEmpty() && isLoginFlow -> {
                    isLoginFlow = false
                    Toast.makeText(this, R.string.login_token_error, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        NavHostActivity.start(this)
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