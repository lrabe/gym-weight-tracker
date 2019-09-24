package de.lrabe.gymweighttracker.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import de.lrabe.gymweighttracker.ui.main.ExerciseListFragment
import de.lrabe.gymweighttracker.R
import de.lrabe.gymweighttracker.ui.main.MainFragment


class TabPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            MainFragment()
        } else {
            ExerciseListFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            context.getString(R.string.tab_title_scan)
        } else {
            context.getString(R.string.tab_title_devices)
        }
    }
}