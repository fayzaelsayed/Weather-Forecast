package com.example.weatherforecast.ui.favorites

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.databinding.FavoritesItemBinding
import com.example.weatherforecast.datasource.local.FavoritesEntity
import com.example.weatherforecast.utils.GlobalHelper
import javax.inject.Inject

class FavoritesAdapter @Inject constructor(private val globalHelper: GlobalHelper) :
    ListAdapter<FavoritesEntity, FavoritesAdapter.FavouritesViewHolder>(DiffCallback()) {
    private lateinit var buttonClickListener: OnButtonClickListener
    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        buttonClickListener = listener
    }

    interface OnButtonClickListener {
        fun onButtonClick(action: String, favoritesEntity: FavoritesEntity, position: Int)
    }

    inner class FavouritesViewHolder(private val binding: FavoritesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(favoritesEntity: FavoritesEntity, position: Int) {
            binding.apply {
                tvFavoriteCityName.text = favoritesEntity.cityNameF
                ivDeleteFavorite.setOnClickListener {
                    buttonClickListener.onButtonClick("delete", favoritesEntity, position)
                }
                cvFavoriteItem.setOnClickListener {
                    buttonClickListener.onButtonClick("navigate", favoritesEntity, position)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FavoritesEntity>() {
        override fun areItemsTheSame(oldItem: FavoritesEntity, newItem: FavoritesEntity): Boolean {
            return oldItem.idF == newItem.idF
        }

        override fun areContentsTheSame(
            oldItem: FavoritesEntity,
            newItem: FavoritesEntity
        ): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        val binding =
            FavoritesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavouritesViewHolder(binding)
    }


    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {
        val currentItem = getItem(position)
        currentItem?.let {
            holder.bind(currentItem, position)
        }
    }


}