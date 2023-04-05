package com.wny2023.tpquickplacesearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wny2023.tpquickplacesearchapp.databinding.FragmentPlaceListBinding
import com.wny2023.tpquickplacesearchapp.databinding.FragmentPlaceMapBinding

class PlaceMapFragment : Fragment() {

    lateinit var binding: FragmentPlaceMapBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPlaceMapBinding.inflate(inflater,container,false)
        return binding.root
    }
}