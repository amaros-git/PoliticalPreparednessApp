package com.example.android.politicalpreparedness.representative

import android.provider.Settings.Global.getString
import android.view.View
import android.widget.ImageView
import android.widget.Spinner
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.utils.fadeIn
import com.example.android.politicalpreparedness.utils.fadeOut
import com.squareup.picasso.Picasso

@BindingAdapter("app:imageUrl")
fun bindImage(imageView: ImageView, imgUrl: String?) {
    if (null == imgUrl) {
        imageView.setImageResource(R.drawable.ic_profile)
    } else {
        val imgURI = imgUrl.toUri().buildUpon().scheme("https").build()
        Picasso.with(imageView.context)
                .load(imgURI)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .into(imageView)
    }
}

/**
 * Use this binding adapter to show and hide the views using boolean variables
 */
@BindingAdapter("android:fadeVisible")
fun setFadeVisible(view: View, visible: Boolean? = true) {
    if (view.tag == null) {
        view.tag = true
        view.visibility = if (visible == true) View.VISIBLE else View.GONE
    } else {
        view.animate().cancel()
        if (visible == true) {
            if (view.visibility == View.GONE)
                view.fadeIn()
        } else {
            if (view.visibility == View.VISIBLE)
                view.fadeOut()
        }
    }
}
