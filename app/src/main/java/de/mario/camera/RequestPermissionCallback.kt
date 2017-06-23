package de.mario.camera

import android.Manifest
import android.app.Fragment
import android.content.pm.PackageManager
import android.support.v13.app.FragmentCompat

/**
 *
 */
class RequestPermissionCallback(val fragment: Fragment) : FragmentCompat.OnRequestPermissionsResultCallback{

    private val REQUEST_CAMERA_PERMISSION = 1
    private val FRAGMENT_DIALOG = "dialog"

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
            if (grantResults.size != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance(fragment.getString(R.string.request_permission))
                        .show(fragment.childFragmentManager, FRAGMENT_DIALOG)
            }
        } else {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}