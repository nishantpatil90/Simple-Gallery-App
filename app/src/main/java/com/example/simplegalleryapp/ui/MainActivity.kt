package com.example.simplegalleryapp.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.simplegalleryapp.R
import com.example.simplegalleryapp.presentation.album_list.AlbumListFragment
import com.example.simplegalleryapp.databinding.MainActivityBinding
import com.example.simplegalleryapp.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: MainActivityBinding

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
            viewModel.checkUiState()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)

        initObserver()
        binding.retryButton.setOnClickListener { viewModel.checkUiState() }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(state = Lifecycle.State.STARTED) {
                viewModel.state.collectLatest {
                    val state = it.getContentIfNotHandled()
                    state ?: return@collectLatest
                    when (state) {
                        ViewState.LaunchAlbumListScreen -> launchAlbumListFragment()
                        ViewState.RequestPermission -> requestPermissionsIfNecessary()
                    }
                }
            }
        }
    }

    private fun showErrorScreen(value: Boolean) {
        binding.errorGroup.isVisible = value
        binding.fragmentContainer.isVisible = !value
    }

    private fun launchAlbumListFragment() {
        showErrorScreen(false)
        replaceFragment(AlbumListFragment.newInstance(), false)
    }

    private fun requestPermissionsIfNecessary() {
        val permissionsToRequest = ArrayList<String>()

        for (permission in Constants.PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            if (shouldShowRequestPermissionRationale(permissionsToRequest.toTypedArray())) {
                showPermissionRationaleDialog()
                showErrorScreen(true)
            } else {
                requestMultiplePermissions.launch(permissionsToRequest.toTypedArray())
            }
        }
    }

    private fun shouldShowRequestPermissionRationale(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                return false
            }
        }
        return true
    }

    private fun showPermissionRationaleDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(getString(R.string.app_permission_rationale))
        alertDialogBuilder.setPositiveButton("SETTINGS") { _, _ ->
            openAppSettings()
        }
        alertDialogBuilder.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    fun replaceFragment(fragment: Fragment, shouldAddToBackSTack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
        if (shouldAddToBackSTack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun popFragment() {
        supportFragmentManager.popBackStack()
    }
}
