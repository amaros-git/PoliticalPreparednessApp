package com.example.android.politicalpreparedness.representative

import android.provider.Settings.Global.getString
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R
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