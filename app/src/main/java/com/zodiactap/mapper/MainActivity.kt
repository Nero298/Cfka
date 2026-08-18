package com.zodiactap.mapper

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import com.zodiactap.mapper.base.BaseMainActivity
import com.zodiactap.mapper.base.R
import com.zodiactap.mapper.base.databinding.ActivityMainBinding
import com.zodiactap.mapper.base.utils.ui.DialogProvider
import com.zodiactap.mapper.base.utils.ui.showDialogs
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseMainActivity() {

    @Inject
    lateinit var dialogProvider: DialogProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val navController = binding.container.getFragment<NavHostFragment>().navController
        val fragmentNavigator =
            navController.navigatorProvider.getNavigator(FragmentNavigator::class.java)

        val homeDest = fragmentNavigator.createDestination().apply {
            id = R.id.home_fragment
            setClassName(MainFragment::class.java.name)
        }

        navController.graph = navController.navInflater.inflate(R.navigation.nav_base_app).apply {
            addDestination(homeDest)
            setStartDestination(R.id.home_fragment)
        }

        dialogProvider.showDialogs(this, binding.coordinatorLayout)
    }
}
