package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.RepresantiveItemBinding
import com.example.android.politicalpreparedness.data.network.models.Channel
import com.example.android.politicalpreparedness.databinding.HeaderBinding
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RepresentativeListAdapter(private val viewModel: RepresentativeViewModel) :
        ListAdapter<RepresentativeDataItem, RecyclerView.ViewHolder>(RepresentativeDiffCallback()) {

    companion object {
        private const val ITEM_VIEW_TYPE_HEADER = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1
    }

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> Header.from(parent)
            ITEM_VIEW_TYPE_ITEM -> RepresentativeViewHolder.from(parent)
             else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RepresentativeViewHolder -> {
                val representativeItem = getItem(position) as RepresentativeDataItem.RepresentativeItem
                holder.bind(viewModel, representativeItem.representative)
            }
            is Header -> {
                val headerItem = getItem(position) as RepresentativeDataItem.Header
                holder.bind(headerItem.headerText)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is RepresentativeDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is RepresentativeDataItem.RepresentativeItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun submitMyList(list: List<Representative>?, headerText: String? = null) {
        list?.let {
            adapterScope.launch {
                val items = if (null != headerText) {
                    listOf(RepresentativeDataItem.Header(headerText)) + list.map {
                        RepresentativeDataItem.RepresentativeItem(it)
                    }
                } else {
                    list.map { RepresentativeDataItem.RepresentativeItem(it) }
                }

                withContext(Dispatchers.Main) {
                    submitList(items)
                }
            }
        }
    }
}

class RepresentativeViewHolder(val binding: RepresantiveItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(viewModel: RepresentativeViewModel, item: Representative) {
        binding.viewModel = viewModel
        binding.representative = item

        item.official.channels?.let { showSocialLinks(it) }
        item.official.urls?.let { showWWWLinks(it) }

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            val binding = RepresantiveItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            )
            return RepresentativeViewHolder(binding)
        }
    }

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) {
            Log.d("TEST", "facebookUrl= $facebookUrl")
            enableLink(binding.facebookIcon, facebookUrl)
        }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) {
            Log.d("TEST", "twitterUrl= $twitterUrl")
            enableLink(binding.twitterIcon, twitterUrl)
        }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.wwwIcon, urls.first())
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
                .map { channel -> "https://www.facebook.com/${channel.id}" }
                .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
                .map { channel -> "https://www.twitter.com/${channel.id}" }
                .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
}

class Header private constructor(
        private val binding: HeaderBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(text: String) {
        binding.headerText = text
        binding.executePendingBindings()

    }

    companion object {
        fun from(parent: ViewGroup): Header {
            val binding = HeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            )
            return Header(binding)
        }
    }
}

class RepresentativeDiffCallback : DiffUtil.ItemCallback<RepresentativeDataItem>() {
    override fun areItemsTheSame(oldItem: RepresentativeDataItem, newItem: RepresentativeDataItem): Boolean {
        return ((oldItem._representative?.office == newItem._representative?.office) &&
                (oldItem._representative?.official == newItem._representative?.official))
    }

    override fun areContentsTheSame(oldItem: RepresentativeDataItem, newItem: RepresentativeDataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class RepresentativeDataItem {
    data class RepresentativeItem(val representative : Representative) : RepresentativeDataItem() {
        override val _representative: Representative = representative
    }

    data class Header(val headerText: String) : RepresentativeDataItem() {
        override val _representative: Representative? = null
    }

    abstract val _representative : Representative?
}