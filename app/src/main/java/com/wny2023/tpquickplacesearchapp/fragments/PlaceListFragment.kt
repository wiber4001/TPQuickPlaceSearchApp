package com.wny2023.tpquickplacesearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wny2023.tpquickplacesearchapp.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {
//    val binding:FragmentPlaceListBinding by lazy {FragmentPlaceListBinding.inflate(...)}...길...어
    lateinit var binding: FragmentPlaceListBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentPlaceListBinding.inflate(inflater,container,false)
        return binding.root
    }
}