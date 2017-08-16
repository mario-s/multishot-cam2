package de.mario.camera

import android.Manifest
import android.app.Fragment
import android.content.pm.PackageManager
import android.support.v13.app.FragmentCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import de.mario.camera.widget.ConfirmationDialog
import de.mario.camera.widget.ErrorDialog

/**
 *
 */
class PermissionRequester(private val fragment: Fragment) : FragmentCompat.OnRequestPermissionsResultCallback {

    private val FRAGMENT_DIALOG = "dialog"
    private val cameraPermission = R.string.request_camera_permission
    private val writePermission = R.string.request_write_permission

    /**
     * This method requests the required permissions for this app.
     * First it checks if it already has what the app needs, if not it show a dialog to the user.
     * @returns true if all needed permissions are given.
     */
    fun hasPermissions(): Boolean {
        var has = true
        if (hasNoPermissions(Manifest.permission.CAMERA)) {
            requestPermission(cameraPermission, Manifest.permission.CAMERA)
            has = false
        } else if(hasNoPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(writePermission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            has = false
        }
        return has
    }

    private fun hasNoPermissions(permission: String): Boolean {
        return checkSelfPermission(fragment.activity, permission) != PackageManager.PERMISSION_GRANTED
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