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
    val REQUEST_CAMERA_PERMISSION = 1
    val REQUEST_EXTERNAL_WRITE = 2

    fun requestCameraPermission() {
        requestPermission(Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION)
    }

    fun requestWritePermission() {
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_WRITE)
    }

    private fun requestPermission(permission: String, requestCode: Int) {
        if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, permission)) {
            ConfirmationDialog().show(fragment.childFragmentManager, FRAGMENT_DIALOG)
        } else {
            FragmentCompat.requestPermissions(fragment, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            results: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> showDialogIfNotGranted(results, R.string.request_camera_permission)
            REQUEST_EXTERNAL_WRITE -> showDialogIfNotGranted(results, R.string.request_write_permission)
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