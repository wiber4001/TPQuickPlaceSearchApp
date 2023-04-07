package com.wny2023.tpquickplacesearchapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wny2023.tpquickplacesearchapp.activities.MainActivity
import com.wny2023.tpquickplacesearchapp.adapters.PlaceListRecyclerAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //MainActivity에 있는 대량의 데이터(documents)를 소환
        val ma:MainActivity = requireActivity() as MainActivity // as 형변환
//        if(ma.searchPlaceResponse==null) return // 서버에 접속중이라 자료가 안불러와졌을때는 실행멈춤
//        ma.searchPlaceResponse ?:return
//        binding.recycler.adapter = PlaceListRecyclerAdapter(requireActivity(),ma.searchPlaceResponse!!.documents)
        //scope함수로 간결화
        ma.searchPlaceResponse?.apply {
            binding.recycler.adapter=PlaceListRecyclerAdapter(requireActivity(),documents) //영역안에서는 this. 생략가능
        }

    }
}