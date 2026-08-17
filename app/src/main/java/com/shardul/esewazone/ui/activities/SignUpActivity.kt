package com.shardul.esewazone.ui.activities


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.crashlytics.internal.common.Utils
import com.shardul.esewazone.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            if (validateForm(username, email, password)) {
                registerUser(username, email, password)
            }
        }
    }

    private fun validateForm(username: String, email: String, password: String): Boolean {
        var isValid = true

        binding.tilUsername.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        if (username.isEmpty()) {
            binding.tilUsername.error = "Username is required"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email address"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun registerUser(username: String, email: String, password: String) {
        setLoadingState(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            setLoadingState(false)

                            if (profileTask.isSuccessful) {
                                Toast.makeText(this,"Account created successfully! Please log in",Toast.LENGTH_LONG).show()
//                                Utils.showToast(this, "Account created successfully! Please log in.")
                                finish()
                            } else {
                                Toast.makeText(this,"Failed to create user account",Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    setLoadingState(false)
                    val error = task.exception?.localizedMessage ?: "Registration failed"
                    Toast.makeText(this,error,Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.btnSignUp.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}