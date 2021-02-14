package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionItemBinding
import com.example.android.politicalpreparedness.data.models.Election
import com.example.android.politicalpreparedness.databinding.HeaderBinding
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


private const val ITEM_VIEW_TYPE_HEADER = 0
private const val ITEM_VIEW_TYPE_ITEM = 1

class ElectionListAdapter(
        private val viewModel: ElectionsViewModel
) : ListAdapter<ElectionDataItem, RecyclerView.ViewHolder>(ElectionDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> Header.from(parent)
            ITEM_VIEW_TYPE_ITEM -> ElectionViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ElectionViewHolder -> {
                val electionItem = getItem(position) as ElectionDataItem.ElectionItem
                holder.bind(viewModel, electionItem.election)
            }
            is Header -> {
                val headerItem = getItem(position) as ElectionDataItem.Header
                holder.bind(headerItem.headerText)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ElectionDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is ElectionDataItem.ElectionItem -> ITEM_VIEW_TYPE_ITEM
        }
    }

    fun submitMyList(list: List<Election>?, headerText: String? = null) {
        list?.let {
            adapterScope.launch {
                val items = if (null != headerText) {
                    listOf(ElectionDataItem.Header(headerText)) + list.map {
                        ElectionDataItem.ElectionItem(it)
                    }
                } else {
                    list.map { ElectionDataItem.ElectionItem(it) }
                }

                withContext(Dispatchers.Main) {
                    submitList(items)
                }
            }
        }
    }

}

class ElectionViewHolder(
        private val binding: ElectionItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(viewMode: ElectionsViewModel, item: Election) {
        binding.viewModel = viewMode
        binding.election = item

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val binding = ElectionItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            )
            return ElectionViewHolder(binding)
        }
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

class ElectionDiffCallback : DiffUtil.ItemCallback<ElectionDataItem>() {
    override fun areItemsTheSame(
            oldItem: ElectionDataItem,
            newItem: ElectionDataItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
            oldItem: ElectionDataItem,
            newItem: ElectionDataItem
    ): Boolean {
        return oldItem == newItem
    }
}

sealed class ElectionDataItem {
    data class ElectionItem(val election: Election) : ElectionDataItem() {
        override val id = election.id.toLong()
    }

    data class Header(val headerText: String) : ElectionDataItem() {
        override val id = Long.MAX_VALUE
    }

    abstract val id: Long
}
