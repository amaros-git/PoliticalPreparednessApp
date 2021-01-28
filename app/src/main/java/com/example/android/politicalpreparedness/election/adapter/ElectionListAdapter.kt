package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ElectionViewHolderBinding
import com.example.android.politicalpreparedness.data.network.models.Election
import com.example.android.politicalpreparedness.election.ElectionsViewModel


class ElectionListAdapter(private val viewModel: ElectionsViewModel): ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }
}

class ElectionViewHolder(val binding: ElectionViewHolderBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(viewMode: ElectionsViewModel, item: Election) {
        binding.viewModel = viewMode
        binding.election = item

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val binding = ElectionViewHolderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
            ).apply {
                this.lifecycleOwner = lifecycleOwner
            }
            return ElectionViewHolder(binding)
        }
    }
}

class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}
