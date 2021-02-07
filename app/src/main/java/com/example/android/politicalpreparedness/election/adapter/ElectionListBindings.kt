package com.example.android.politicalpreparedness.election.adapter

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.model.Representative

/**
 * [BindingAdapter]s for the [Election]s list.
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Election>?) {
    items?.let {
        (listView.adapter as ElectionListAdapter).submitList(items)
    }
}*/

/**
 * [BindingAdapter]s for the [Representatives]s list.
 */
@BindingAdapter("app:representatives")
fun setRepresentatives(listView: RecyclerView, items: List<Representative>?) {
    items?.let {
        (listView.adapter as RepresentativeListAdapter).submitList(items)
    }
}