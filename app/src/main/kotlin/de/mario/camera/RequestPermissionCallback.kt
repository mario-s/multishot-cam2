package de.mario.camera

import android.Manifest
import android.app.Fragment
import android.content.pm.PackageManager
import android.support.v13.app.FragmentCompat
import de.mario.camera.widget.ConfirmationDialog
import de.mario.camera.widget.ErrorDialog

/**
 *
 */
class RequestPermissionCallback(val fragment: Fragment) : FragmentCompat.OnRequestPermissionsResultCallback {

    private val FRAGMENT_DIALOG = "dialog"
    private val cameraPermission = R.string.request_camera_permission
    private val writePermission = R.string.request_write_permission

    fun requestCameraPermission() {
        requestPermission(cameraPermission, Manifest.permission.CAMERA)
    }

    fun requestWritePermission() {
        requestPermission(writePermission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun requestPermission(requestCode: Int, permission: String) {
        if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission)) {
            ConfirmationDialog(requestCode, permission).show(fragment.childFragmentManager, FRAGMENT_DIALOG)
        } else {
            FragmentCompat.requestPermissions(fragment, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            results: IntArray) {
        when (requestCode) {
            cameraPermission, writePermission -> showDialogIfNotGranted(results, requestCode)
            else -> fragment.onRequestPermissionsResult(requestCode, permissions, results)
        }
    }

    private fun showDialogIfNotGranted(results: IntArray, id: Int) {
        if (notGranted(results)) showErrorDialog(id)
    }

    private fun notGranted(results: IntArray): Boolean {
        return !results.contains(PackageManager.PERMISSION_GRANTED)
    }

    private fun showErrorDialog(id: Int) {
        ErrorDialog.newInstance(fragment.getString(id))
                .show(fragment.childFragmentManager, FRAGMENT_DIALOG)
    }
}