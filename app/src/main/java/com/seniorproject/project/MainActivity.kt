package com.seniorproject.project

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction

import com.etebarian.meowbottomnavigation.MeowBottomNavigation

import com.seniorproject.project.ui.Favourites.FavouriteFragment
import com.seniorproject.project.ui.Home.HomeFragment
import com.seniorproject.project.ui.ProfileFragment

//This class is parent of all classes
//On this we created bottom navigation which allow user to navigates easily
//This class acts as container for fragments to provide easy navigation
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        //custom bottom navigation
        val  bottomNavigation: MeowBottomNavigation = findViewById(R.id.nav_view)
        //Initializing all three bottom tabs
        bottomNavigation.add(MeowBottomNavigation.Model(1, R.drawable.ic_home_black_24dp))

        bottomNavigation.add(MeowBottomNavigation.Model(2, R.drawable.ic_favorite_24))

        bottomNavigation.add(MeowBottomNavigation.Model(3, R.drawable.ic_hamburger))

        bottomNavigation.show(1, true)
        replace(HomeFragment())
        //switching between all three bottom tabs
        bottomNavigation.setOnClickMenuListener { model: MeowBottomNavigation.Model ->
            when (model.id) {
                1 -> replace(HomeFragment())

                2 -> replace(FavouriteFragment())

                3 -> replace(ProfileFragment())

            }

        }
    }
    //replacing fragment from one to other
    private fun replace(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }
}
