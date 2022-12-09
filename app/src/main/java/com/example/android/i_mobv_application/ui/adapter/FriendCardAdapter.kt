package com.example.android.i_mobv_application.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.databinding.FriendsListItemBinding
import com.example.android.i_mobv_application.model.Friend


class FriendCardAdapter(
	private val friendEventListener: FriendEventListener,
) : ListAdapter<Friend, FriendCardAdapter.FriendCardViewHolder>(DiffCallback) {

	class FriendCardViewHolder(var binding: FriendsListItemBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun bind(
			friend: Friend,
			friendEventListener: FriendEventListener,
		) {
			binding.friend = friend
			binding.friendEventListener = friendEventListener
			binding.userIcon.setImageResource(R.drawable.ic_baseline_person_24)
			binding.placeIcon.setImageResource(R.drawable.ic_baseline_generic_place_24)
		}
	}

	companion object DiffCallback : DiffUtil.ItemCallback<Friend>() {
		override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
			return oldItem.userId == newItem.userId
		}

		override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
			return oldItem == newItem
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendCardViewHolder {
		return FriendCardViewHolder(
			FriendsListItemBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	}

	override fun onBindViewHolder(holder: FriendCardViewHolder, position: Int) {
		holder.bind(getItem(position), friendEventListener)
	}
}

class FriendEventListener(val clickListener: (pubId: String?) -> Unit) {
	fun onClick(pubId: String?) = clickListener(pubId)
}