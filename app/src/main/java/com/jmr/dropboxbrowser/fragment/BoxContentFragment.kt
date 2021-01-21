package com.jmr.dropboxbrowser.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dropbox.core.DbxRequestConfig
import com.jmr.data.model.FileResponse
import com.jmr.domain.model.DropboxTypes
import com.jmr.domain.usecases.GetTokenUseCase
import com.jmr.dropboxbrowser.R
import com.jmr.dropboxbrowser.activity.HomeActivity
import com.jmr.dropboxbrowser.adapter.DropboxContentAdapter
import com.jmr.dropboxbrowser.databinding.FragmentBoxContentBinding
import com.jmr.dropboxbrowser.util.extension.checkAndRequestPermission
import com.jmr.dropboxbrowser.viewmodel.BoxContentViewModel
import com.jmr.dropboxbrowser.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BoxContentFragment : Fragment() {

    @Inject
    lateinit var dbxRequestConfig: DbxRequestConfig

    @Inject
    lateinit var getTokenUseCase: GetTokenUseCase

    private val boxContentViewModel: BoxContentViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val args: BoxContentFragmentArgs by navArgs()

    private var _binding: FragmentBoxContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoxContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        boxContentViewModel.getBoxContent(args.folder)
    }

    private fun initObservers() {
        boxContentViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                BoxContentViewModel.State.Loading -> (requireActivity() as HomeActivity).displayLoading()
                is BoxContentViewModel.State.Success -> {
                    (requireActivity() as HomeActivity).hideLoading()
                    populateView(state.files.entries)
                }
                is BoxContentViewModel.State.FileIntent -> onFileIntent(state.intent)
                is BoxContentViewModel.State.Error -> {
                    (requireActivity() as HomeActivity).hideLoading()
                    displayEmptyFolderMessage()
                    displayErrorToast(state.errorMessage)
                }
            }
        })

        homeViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state.peekContent()) {
                is HomeViewModel.State.FileDownloaded -> {
                    homeViewModel.requestLoading(false)
                    openFile((state.getContentIfNotHandled() as? HomeViewModel.State.FileDownloaded)?.file)
                }
                is HomeViewModel.State.Error -> {
                    homeViewModel.requestLoading(false)
                    displayErrorToast((state.getContentIfNotHandled() as? HomeViewModel.State.Error)?.errorMessage)
                }
            }
        })
    }

    private fun displayEmptyFolderMessage() {
        binding.rvContent.visibility = View.GONE
        binding.contentMessage.visibility = View.VISIBLE
    }

    private fun displayErrorToast(errorMessage: String?) {
        val message = errorMessage ?: getString(R.string.basic_general_error)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun populateView(dropboxContent: List<FileResponse>) {
        if (dropboxContent.isEmpty()) {
            displayEmptyFolderMessage()
        } else {
            binding.rvContent.apply {
                adapter = DropboxContentAdapter(
                    dropboxContent,
                    homeViewModel.picassoInstance!!
                ) {
                    when (it.tag) {
                        DropboxTypes.FILE.value -> {
                            checkAndRequestPermission(
                                getString(R.string.permission_request_title),
                                getString(R.string.permission_request_message),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                REQUEST_CODE_ASK_PERMISSIONS,
                                {
                                    homeViewModel.downloadFile(it)
                                },
                                {
                                    homeViewModel.pendingFile = it
                                },
                                {
                                    Toast.makeText(
                                        requireContext(),
                                        R.string.permission_request_message,
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            )
                        }
                        DropboxTypes.FOLDER.value -> {
                            homeViewModel.displayFolderContent(it.path_display)
                        }
                    }
                }
                setHasFixedSize(true)
                layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        }
    }

    private fun openFile(file: File?) {
        file?.let { result ->
            val fileUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireActivity().applicationContext.packageName}.provider",
                result
            )
            boxContentViewModel.getFileIntent(result.name, fileUri)
        } ?: run {
            displayErrorToast(null)
        }
    }

    private fun onFileIntent(fileIntent: Intent) {
        val resolveInfo =
            requireActivity().packageManager.queryIntentActivities(fileIntent, 0)
        if (resolveInfo.size > 0) {
            startActivity(fileIntent)
        } else {
            displayErrorToast(null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS && grantResults.isNotEmpty() && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            homeViewModel.pendingFile?.also {
                homeViewModel.downloadFile(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_CODE_ASK_PERMISSIONS = 1001
    }
}