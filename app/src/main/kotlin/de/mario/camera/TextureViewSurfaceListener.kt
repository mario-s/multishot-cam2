package de.mario.camera

import android.graphics.SurfaceTexture
import android.view.TextureView
import de.mario.camera.glue.CameraControlable


class TextureViewSurfaceListener(private val control: CameraControlable) : TextureView.SurfaceTextureListener {
    override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
        control.openCamera(width, height)
    }

    override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
        control.updateTransform(width, height)
    }

    override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
        return true
    }

    override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
}