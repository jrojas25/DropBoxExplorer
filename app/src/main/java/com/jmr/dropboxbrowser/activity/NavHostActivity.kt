package com.jmr.dropboxbrowser.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationView
import com.jmr.domain.usecases.HasTokenUseCase
import com.jmr.dropboxbrowser.R
import com.jmr.dropboxbrowser.databinding.ActivityNavHostBinding
import com.jmr.dropboxbrowser.util.FileThumbnailRequestHandler
import com.jmr.dropboxbrowser.viewmodel.LogoutViewModel
import com.jmr.dropboxbrowser.viewmodel.NavHostSharedViewModel
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NavHostActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityNavHostBinding
    private val logoutViewModel: LogoutViewModel by viewModels()
    private val navHostSharedViewModel: NavHostSharedViewModel by viewModels()

    private val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }
    private var folder: String? = null

    @Inject
    lateinit var hasTokenUseCase: HasTokenUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initObserver()

        navHostSharedViewModel.picassoInstance = Picasso.Builder(applicationContext)
            .downloader(OkHttp3Downloader(applicationContext))
            .addRequestHandler(FileThumbnailRequestHandler(navHostSharedViewModel.dbxClientV2))
            .build()

        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_logout) {
            logoutViewModel.logUserOut()
        }
        return true
    }

    private fun initObserver() {
        logoutViewModel.state.observe(this, Observer { state ->
            when (state) {
                LogoutViewModel.State.Loading -> displayLoading()
                is LogoutViewModel.State.Success -> {
                    hideLoading()
                    LoginActivity.start(this)
                    finish()
                }
                is LogoutViewModel.State.Error -> {
                    hideLoading()
                    val message = state.errorMessage ?: getString(R.string.basic_general_error)
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun displayLoading() {
        binding.progressBar.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    fun displayFolderContent(folder: String?) {
        this.folder = folder
        val bundle = bundleOf("folder" to folder)
        navController.navigate(R.id.content_fragment, bundle)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && grantResults.isNotEmpty() && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            navHostSharedViewModel.pendingFile?.also {
                navHostSharedViewModel.downloadFile(it)
            }
        }
    }

    companion object {

        const val REQUEST_CODE_ASK_PERMISSIONS = 1001

        fun start(context: Context) {
            val intent = Intent(context, NavHostActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

}