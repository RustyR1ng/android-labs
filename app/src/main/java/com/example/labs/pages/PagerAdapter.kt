package com.example.labs.pages

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/* PagerAdapter for supplying the ViewPager with the pages (fragments) to display. */
class PagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(
    fragmentManager,
    FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    // Titles of the individual pages (displayed in tabs)
    private val PAGE_TITLES = arrayOf(
        "Lab 1",
        "Lab 2",
        "Lab 3"
    )


    // The fragments that are used as the individual pages
    private val PAGES: Array<Fragment> = arrayOf<Fragment>(
        Lab1(),
        Lab2(),
        Lab3()
    )

    override fun getItem(position: Int): Fragment {
        return PAGES[position]
    }

    override fun getCount(): Int {
        return PAGES.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return PAGE_TITLES[position]
    }


}