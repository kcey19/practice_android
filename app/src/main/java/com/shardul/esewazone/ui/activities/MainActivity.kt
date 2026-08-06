package com.shardul.esewazone.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.FirebaseApp
import com.shardul.esewazone.R
import com.shardul.esewazone.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
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
        val firebase = FirebaseApp.initializeApp(this)
        if (firebase
            != null
        ) {
            Log.v("Firevase initialize", "Firevase initialized")
        } else {
            Log.v("Firevase notinitialize", "Firevase not initialized")

        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
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

