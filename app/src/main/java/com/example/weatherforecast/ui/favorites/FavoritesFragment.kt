package com.example.weatherforecast.ui.favorites

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentFavoritesBinding
import com.example.weatherforecast.datasource.local.FavoritesEntity
import com.example.weatherforecast.utils.BaseFragment
import com.example.weatherforecast.utils.GlobalHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(true) {
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoriteList: MutableList<FavoritesEntity>


    @Inject
    lateinit var globalHelper: GlobalHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
        favoriteList = ArrayList()
        setUpAdapter()


        viewModel.getAllFavoritesPlacesFromDatabase().observe(viewLifecycleOwner) {
            it?.let {
                favoriteList.clear()
                favoriteList.addAll(it)
                val newList = ArrayList<FavoritesEntity>()
                newList.addAll(favoriteList)
                favoritesAdapter.submitList(newList)

                if (favoriteList.isEmpty()) {
                    binding.ivEmptyFavorites.visibility = View.VISIBLE
                    binding.tvEmptyFavorites.visibility = View.VISIBLE
                } else {
                    binding.ivEmptyFavorites.visibility = View.GONE
                    binding.tvEmptyFavorites.visibility = View.GONE
                }
            }
        }
        return binding.root
    }

    private fun setUpAdapter() {
        favoritesAdapter = FavoritesAdapter(globalHelper)
        binding.rvFavorite.layoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.rvFavorite.adapter = favoritesAdapter

        favoritesAdapter.setOnButtonClickListener(object : FavoritesAdapter.OnButtonClickListener {
            override fun onButtonClick(
                action: String,
                favoritesEntity: FavoritesEntity,
                position: Int
            ) {
                when (action) {
                    "delete" -> {
                        showDialog(favoritesEntity, position)
                    }
                    "navigate" -> {
                        navigateToHome(favoritesEntity)
                    }
                }

            }

        })
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun showDialog(favoritesEntity: FavoritesEntity, position: Int) {
        val dialogLayout = layoutInflater.inflate(R.layout.dialog, null)
        val textView = dialogLayout.findViewById<TextView>(R.id.tv_city_name)
        val saveButton = dialogLayout.findViewById<Button>(R.id.btn_save)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.btn_cancel)

        textView.text = "Are you sure you want to delete this place from favorites?"
        saveButton.text = "yes"

        val builder = AlertDialog.Builder(requireContext()).setView(dialogLayout).create()

        saveButton.setOnClickListener {
            viewModel.deleteFavoritePlaceByIdFromDatabase(favoritesEntity)
            builder.dismiss()
        }

        cancelButton.setOnClickListener {
            builder.dismiss()
        }

        builder.show()
    }

    private fun navigateToHome(favoritesEntity: FavoritesEntity) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToHomeFavoriteFragment(favoritesEntity)
       // action.currentPlace = favoritesEntity
        findNavController().navigate(action)
    }
}