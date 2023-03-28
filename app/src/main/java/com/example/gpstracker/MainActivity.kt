package com.example.gpstracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gpstracker.databinding.ActivityMainBinding
import com.example.gpstracker.ui.HomeFragment
import com.example.gpstracker.ui.SettingsFragment
import com.example.gpstracker.ui.TracksFragment
import com.example.gpstracker.utils.openFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        openFragment(HomeFragment.newInstance())
        onBottomMenuSelected()
    }

    private fun onBottomMenuSelected() {
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.itemHome -> {
                    openFragment(HomeFragment.newInstance())
                }
                R.id.itemSettings -> openFragment(SettingsFragment())
                R.id.itemTracks -> openFragment(TracksFragment.newInstance())
            }
            true
        }
    }
}