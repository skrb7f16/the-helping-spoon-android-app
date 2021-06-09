package com.skrb7f16.thehelpingspoon


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.skrb7f16.thehelpingspoon.ui.feeds.FeedsFragment
import com.skrb7f16.thehelpingspoon.ui.home.HomeFragment
import com.skrb7f16.thehelpingspoon.ui.user.UserFragment


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private  var baseUrl="https://the-helping-spoon-django.herokuapp.com/";
    private lateinit var sharedPreferences: SharedPreferences;
    private lateinit var editor: SharedPreferences.Editor;
    private lateinit var drawerLayout:DrawerLayout;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_feeds, R.id.nav_user), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)
        sharedPreferences=getPreferences(MODE_PRIVATE)
        editor=sharedPreferences.edit();
        editor.putString("baseUrl", baseUrl)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigateUp(): Boolean {
        return true;
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val homeFragment = HomeFragment()
                show(homeFragment)
            }
            R.id.nav_user -> {
                val galleryFragment = UserFragment()
                show(galleryFragment)
            }
            R.id.nav_feeds -> {
                val slideShowFragment = FeedsFragment()
                show(slideShowFragment)
            }

        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun show(fragment: Fragment) {


        val fragmentManager = supportFragmentManager

        fragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit()

        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


}