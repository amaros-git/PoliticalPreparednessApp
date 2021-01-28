package com.example.android.politicalpreparedness.election.adapter

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.data.network.models.Election

/**
 * [BindingAdapter]s for the [Task]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Election>?) {
    Log.d("TEST", "setItems called")
    items?.let {
        Log.d("TEST", "items not null")
        (listView.adapter as ElectionListAdapter).submitList(items)
    }
}