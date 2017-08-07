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
class RequestPermissionCallback(val fragment: Fragment) : FragmentCompat.OnRequestPermissionsResultCallback{

    private val FRAGMENT_DIALOG = "dialog"
    val REQUEST_CAMERA_PERMISSION = 1

    fun requestCameraPermission() {
        if (FragmentCompat.shouldShowRequestPermissionRationale(fragment, Manifest.permission.CAMERA)) {
            ConfirmationDialog().show(fragment.childFragmentManager, FRAGMENT_DIALOG)
        } else {
            FragmentCompat.requestPermissions(fragment, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if(!grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                ErrorDialog.newInstance(fragment.getString(R.string.request_permission))
                        .show(fragment.childFragmentManager, FRAGMENT_DIALOG)
            }
        } else {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}