package com.example.android.i_mobv_application.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.NearbyPubListItemBinding
import com.example.android.i_mobv_application.model.NearbyPub


class NearbyPubCardAdapter(
	private val nearbyPubEventListener: NearbyPubEventListener,
) :
	ListAdapter<NearbyPub, NearbyPubCardAdapter.NearbyPubCardViewHolder>(DiffCallback) {

	class NearbyPubCardViewHolder(var binding: NearbyPubListItemBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(
			pub: NearbyPub,
			nearbyPubEventListener: NearbyPubEventListener,
		) {
			binding.pub = pub
			binding.nearbyPubEventListener = nearbyPubEventListener
			binding.distanceIcon.setImageResource(R.drawable.ic_baseline_signpost_24)
		}
	}

	companion object DiffCallback : DiffUtil.ItemCallback<NearbyPub>() {
		override fun areItemsTheSame(oldItem: NearbyPub, newItem: NearbyPub): Boolean {
			return oldItem.id == newItem.id
		}

		override fun areContentsTheSame(oldItem: NearbyPub, newItem: NearbyPub): Boolean {
			return oldItem == newItem
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyPubCardViewHolder {
		return NearbyPubCardViewHolder(
			NearbyPubListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: NearbyPubCardViewHolder, position: Int) {
		holder.bind(getItem(position), nearbyPubEventListener)
	}
}

class NearbyPubEventListener(val clickListener: (nearbyPub: NearbyPub) -> Unit) {
	fun onClick(nearbyPub: NearbyPub) = clickListener(nearbyPub)
}
