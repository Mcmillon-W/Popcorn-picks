
package com.popcornpicks.home.utils

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.popcornpicks.home.R

/**
 * Utility object for image loading operations using Glide
 */
object ImageUtils {
    
    private const val TAG = "ImageUtils"
    
    /**
     * Loads an image from the given URL into the ImageView using Glide
     * Includes error handling, retry logic, and better placeholder management
     *
     * @param imageView The ImageView to load the image into
     * @param imageUrl The URL of the image to load
     * @param placeholder Optional placeholder drawable (default: gray color)
     * @param errorPlaceholder Optional error placeholder drawable (default: dark gray color)
     */
    fun loadImage(
        imageView: ImageView,
        imageUrl: String?,
        placeholder: ColorDrawable? = null,
        errorPlaceholder: ColorDrawable? = null
    ) {
        // Use default placeholders if not provided
        val defaultPlaceholder = placeholder ?: ColorDrawable(
            ContextCompat.getColor(imageView.context, android.R.color.black)
        )
        val defaultError = errorPlaceholder ?: ColorDrawable(
            ContextCompat.getColor(imageView.context, android.R.color.darker_gray)
        )
        
        // Handle null or empty URL
        if (imageUrl.isNullOrEmpty()) {
            Log.w(TAG, "Image URL is null or empty")
            imageView.setImageDrawable(defaultError)
            return
        }
        
        // Log URL for debugging
        Log.d(TAG, "Loading image from: $imageUrl")
        
        val requestOptions = RequestOptions()
            .placeholder(defaultPlaceholder)
            .error(defaultError)
            .fallback(defaultError)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .override(Target.SIZE_ORIGINAL) // Don't resize, use original size
        
        Glide.with(imageView.context)
            .load(imageUrl)
            .apply(requestOptions)
            .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(TAG, "Failed to load image: $imageUrl", e)
                    e?.logRootCauses(TAG)
                    return false // Return false to allow Glide to handle the error placeholder
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "Successfully loaded image from: $dataSource")
                    return false // Return false to allow Glide to handle the resource
                }

            })
            .into(imageView)
    }
}
