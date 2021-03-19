package com.example.labs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.labs.pages.PagerAdapter
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity(), TabLayout.OnTabSelectedListener {

    lateinit var mViewPager:ViewPager
    lateinit var tabLayout:TabLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // The ViewPager is responsible for sliding pages (fragments) in and out upon user input
        mViewPager = findViewById(R.id.viewpager)
        tabLayout = findViewById(R.id.tabLayout)
        mViewPager?.adapter = PagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(mViewPager)
    }



    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) mViewPager.currentItem = tab.position
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        TODO("Not yet implemented")
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        TODO("Not yet implemented")
    }
}


