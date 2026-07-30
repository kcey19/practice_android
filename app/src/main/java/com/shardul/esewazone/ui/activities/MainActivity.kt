package com.shardul.esewazone.ui.activities

import com.shardul.esewazone.R
import android.view.View
import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.shardul.esewazone.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen= installSplashScreen()
        splashScreen.setOnExitAnimationListener { splashScreen ->
            splashScreen.view.animate()
                .scaleX(2.1f)
                .scaleY(2.1f)
                .alpha(0.8f)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    splashScreen.remove()
                }
                .start()

        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {

                R.id.homeFragment,
                R.id.cartFragment,
                R.id.favouritesFragment,
                R.id.profileFragment -> {
                    binding.bottomNavigation.visibility =
                        View.VISIBLE
                }
                else -> {
                    binding.bottomNavigation.visibility =
                        View.GONE
                }
            }
        }




        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}
/*
val splashScreen = installSplashScreen()
splashScreen.setOnExitAnimationListener{ splash ->
splash.view.animate()
    .scaleX()
    .scaleY()
    .alpha()
    .setInterpolator(AccelerateInterpolator())
    .withEndAction{
        splash.remove()
    }
    .start()


}
val keepSplash = true

splash.keepOnScreenCondition{
kepSplash
}
 */
