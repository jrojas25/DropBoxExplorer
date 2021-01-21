package com.jmr.dropboxbrowser.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.google.android.material.navigation.NavigationView
import com.jmr.dropboxbrowser.R
import com.jmr.dropboxbrowser.databinding.ActivityHomeBinding
import com.jmr.dropboxbrowser.util.FileThumbnailRequestHandler
import com.jmr.dropboxbrowser.viewmodel.LogoutViewModel
import com.jmr.dropboxbrowser.viewmodel.HomeViewModel
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private val logoutViewModel: LogoutViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }
    private var folder: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        initObserver()

        homeViewModel.picassoInstance = Picasso.Builder(applicationContext)
            .downloader(OkHttp3Downloader(applicationContext))
            .addRequestHandler(FileThumbnailRequestHandler(homeViewModel.dbxClientV2))
            .build()
    }

    private fun initView() {
        val toggle =
            ActionBarDrawerToggle(this, binding.drawerLayout, R.string.app_name, R.string.app_name)

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.navigationView.setNavigationItemSelectedListener(this)
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

        homeViewModel.state.observe(this, Observer { state ->
            when (state.peekContent()) {
                is HomeViewModel.State.Loading -> {
                    if (state.getContentIfNotHandled() as? Boolean? == true) {
                        displayLoading()
                    } else {
                        hideLoading()
                    }
                }
                is HomeViewModel.State.FolderContent -> displayFolderContent((state.getContentIfNotHandled() as? HomeViewModel.State.FolderContent?)?.folder)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nav_logout) {
            logoutViewModel.logUserOut()
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    companion object {

        fun start(context: Context) {
            val intent = Intent(context, HomeActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

}