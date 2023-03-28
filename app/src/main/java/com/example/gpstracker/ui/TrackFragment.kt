package com.example.gpstracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.gpstracker.databinding.FragmentTrackBinding

class TrackFragment : Fragment(){
    private lateinit var binding: FragmentTrackBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object{
        @JvmStatic
        fun newInstance() = TrackFragment()
    }
}