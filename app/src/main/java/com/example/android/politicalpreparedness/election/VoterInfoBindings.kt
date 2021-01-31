package com.example.android.politicalpreparedness.election

import android.opengl.Visibility
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.data.network.models.Address
import java.text.SimpleDateFormat
import java.util.*

/**
 * [BindingAdapter] to set address group visibility
 */
@BindingAdapter("app:addressVisibility")
fun addressVisibility(group: Group, address: Address?) { //TODO rework. What is empty ?
    if (null == address) {
        group.visibility = View.GONE
    } else {
        group.visibility = View.VISIBLE
    }
}

@BindingAdapter("app:showDate")
fun showDate(textView: TextView, date: Date?) {
    date?.let {
        val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(it)
        textView.text = dateString
    }
}

//TODO use resources
@BindingAdapter("app:setText")
fun setText(button: Button, isFollowed: Boolean) {
    if (isFollowed) {
        button.text = "UNFOLLOW ELECTION"
    } else {
        button.text = "FOLLOW ELECTION"
    }
}

