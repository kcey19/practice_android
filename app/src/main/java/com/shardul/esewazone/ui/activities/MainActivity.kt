package com.shardul.esewazone.ui.activities

import android.content.Intent
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
import com.google.firebase.auth.FirebaseAuth
import com.shardul.esewazone.R
import com.shardul.esewazone.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        if (!isUserLoggedIn()) {
            navigateToLogin()
            return
        }
        splashScreen.setOnExitAnimationListener { splash ->
            splash.view
                .animate()
                .scaleX(2.1f)
                .scaleY(2.1f)
                .alpha(0.8f)
                .setDuration(800)
                .setInterpolator(DecelerateInterpolator())
                .withEndAction {
                    splash.remove()
                }
                .start()
        }
        setupMainUI()
    }

    private fun isUserLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        Log.d("AUTH_SESSION", "Current user: ${currentUser?.email ?: "No user logged in"}")
        return currentUser != null
    }

    private fun navigateToLogin() {

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
        finish()
    }

    private fun setupMainUI() {
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                binding.navHostFragment.id
            ) as NavHostFragment

        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(
            navController
        )

        navController.addOnDestinationChangedListener {
                _, destination, _ ->

            when (destination.id) {
                R.id.homeFragment,
                R.id.cartFragment,
                R.id.favouritesFragment,
                R.id.moreFragment -> {
                    binding.bottomNavigation.visibility =
                        View.VISIBLE
                }

                else -> {
                    binding.bottomNavigation.visibility =
                        View.GONE
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(
            binding.main
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }
}

