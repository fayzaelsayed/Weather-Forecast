package com.example.weatherforecast.utils

import androidx.fragment.app.Fragment
import com.example.weatherforecast.ui.MainActivity

abstract class BaseFragment(private val isVisible: Boolean): Fragment() {
    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).isBottomAppBarAndActionBarVisible(isVisible)
    }

}