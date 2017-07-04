package de.mario.camera

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.view.TextureView
import android.view.View

/**
 */
open class AutoFitTextureView : TextureView {

    private var mRatioWidth = 0
    private var mRatioHeight = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)


    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.

     * @param size  Relative size
     */
    fun setAspectRatio(size: Size) {
        setAspectRatio(size.width, size.height)
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.

     * @param width  Relative horizontal size
     * *
     * @param height Relative vertical size
     */
    fun setAspectRatio(width: Int, height: Int) {
        if (width < 0 || height < 0) {
            throw IllegalArgumentException("Size cannot be negative.")
        }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = View.MeasureSpec.getSize(widthMeasureSpec)
        val height = View.MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                setDimension(width, width * mRatioHeight / mRatioWidth)
            } else {
                setDimension(height * mRatioWidth / mRatioHeight, height)
            }
        }
    }

    open fun setDimension(width: Int, height: Int) {
        setMeasuredDimension(width, height)
    }
}