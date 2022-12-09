package com.example.android.i_mobv_application.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.i_mobv_application.R
import com.example.android.i_mobv_application.model.Friend
import com.example.android.i_mobv_application.model.Pub
import com.example.android.i_mobv_application.ui.adapter.NearbyPubCardAdapter
import com.example.android.i_mobv_application.ui.adapter.PubCardAdapter
import com.example.android.i_mobv_application.model.NearbyPub
import com.example.android.i_mobv_application.ui.adapter.FriendCardAdapter
import com.google.android.material.snackbar.Snackbar


/**
 * Bind message to Snackbar.
 *
 * @param view View used for message display.
 * @param message Message to display.
 */
@BindingAdapter("showTextToast")
fun bindTextToast(view: View, message: Evento<String>?) {
	message?.getContentIfNotHandled()?.let {
		Snackbar.make(view, it, Snackbar.LENGTH_SHORT).show()
	}
}

/**
 * Bind pubs to RecyclerView.
 *
 * @param recyclerView RecyclerView holding Pubs.
 * @param listData Pubs to bind to RecyclerView.
 */
@BindingAdapter("listData")
fun bindPubsRecyclerView(recyclerView: RecyclerView, listData: List<Pub>?) {
	val adapter = recyclerView.adapter as PubCardAdapter
	adapter.submitList(null)
	adapter.submitList(listData)
}

/**
 * Bind nearby pubs to RecyclerView.
 *
 * @param recyclerView RecyclerView holding Nearby Pubs.
 * @param listData Nearby Pubs to bind to RecyclerView.
 */
@BindingAdapter("nearbyListData")
fun bindNearbyPubsRecyclerView(recyclerView: RecyclerView, listData: List<NearbyPub>?) {
	val adapter = recyclerView.adapter as NearbyPubCardAdapter
	adapter.submitList(null)
	adapter.submitList(listData)
}

/**
 * Bind nearby pubs to RecyclerView.
 *
 * @param recyclerView RecyclerView holding Friends.
 * @param listData Friends to bind to RecyclerView.
 */
@BindingAdapter("friendsData")
fun bindFriendsRecyclerView(recyclerView: RecyclerView, listData: List<Friend>?) {
	val adapter = recyclerView.adapter as FriendCardAdapter
	adapter.submitList(null)
	adapter.submitList(listData)
}

/**
 * Set Pub icon based on Pub Type.
 *
 * @param imageView ImageView to set.
 * @param pubType Pub Type.
 */
@BindingAdapter("pubIcon")
fun setPubIcon(imageView: ImageView, pubType: String?) {
	imageView.setImageResource(
		when (pubType) {
			"cafe" -> R.drawable.ic_baseline_local_cafe_24
			"fast_food" -> R.drawable.ic_baseline_fastfood_24
			"restaurant" -> R.drawable.ic_baseline_restaurant_24
			"pub" -> R.drawable.ic_baseline_sports_bar_24
			"bar" -> R.drawable.ic_baseline_local_bar_24
			"wine_bar" -> R.drawable.ic_baseline_wine_bar_24
			else -> R.drawable.ic_baseline_generic_place_24
		}
	)
}