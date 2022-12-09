package com.example.android.i_mobv_application.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.PubListItemBinding
import com.example.android.i_mobv_application.model.Pub


class PubCardAdapter(
	private val pubEventListener: PubEventListener,
) : ListAdapter<Pub, PubCardAdapter.PubCardViewHolder>(DiffCallback) {

	class PubCardViewHolder(var binding: PubListItemBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(
			pubEventListener: PubEventListener,
			pub: Pub,
		) {
			binding.pub = pub
			binding.pubEventListener = pubEventListener
			binding.numberOfUsersIcon.setImageResource(R.drawable.ic_baseline_person_pin_circle_24)
			binding.distanceIcon.setImageResource(R.drawable.ic_baseline_signpost_24)
		}
	}

	companion object DiffCallback : DiffUtil.ItemCallback<Pub>() {
		override fun areItemsTheSame(oldItem: Pub, newItem: Pub): Boolean {
			return oldItem.id == newItem.id
		}

		override fun areContentsTheSame(oldItem: Pub, newItem: Pub): Boolean {
			return oldItem == newItem
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PubCardViewHolder {
		return PubCardViewHolder(
			PubListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: PubCardViewHolder, position: Int) {
		val pub: Pub = getItem(position)
		holder.bind(pubEventListener, pub)
	}
}

class PubEventListener(val clickListener: (firmId: String, users: Int) -> Unit) {
	fun onClick(firmId: String, users: Int) = clickListener(firmId, users)
}