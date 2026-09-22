package com.shardul.esewazone.ui.activities


import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.SignInButton
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.shardul.esewazone.R
import com.shardul.esewazone.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var isCheckingAuth = true
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { isCheckingAuth }
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){
            startActivity(Intent(this,MainActivity::class.java))
            isCheckingAuth=false
            finish()
            return
        }

        isCheckingAuth=false
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        binding.btnGoogleNative.setSize(SignInButton.SIZE_WIDE)
        binding.btnGoogleNative.setColorScheme(SignInButton.COLOR_LIGHT)

        binding.btnLogin.setOnClickListener { handleLogin() }
        binding.btnGoogleNative.setOnClickListener{signInWithGoogle()}
        binding.tvForgotPassword.setOnClickListener { handleForgotPassword() }
        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!validateForm(email, password)) return
        setLoadingState(true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                setLoadingState(false)
                if (task.isSuccessful) {
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_LONG).show()
                    navigateToMain()
                    finish()
                } else {
                    val readableMessage = getErrorMessage(task.exception)
                    Toast.makeText(this, readableMessage, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val serverClientId = getString(R.string.default_web_client_id)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(serverClientId)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val credentialManager = CredentialManager.create(this)

        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleGoogleCredential(result.credential)
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                val message = e.localizedMessage ?: "Google sign-in failed"
                if (!message.contains("canceled", ignoreCase = true)) {
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleGoogleCredential(credential: androidx.credentials.Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)

                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        binding.progressBar.visibility = View.GONE
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Google Sign-In Successful!", Toast.LENGTH_SHORT).show()
                            navigateToMain()
                        } else {
                            val error = task.exception?.localizedMessage ?: "Authentication failed"
                            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        }
                    }
            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Failed to parse Google credential.", Toast.LENGTH_SHORT).show()
            }
        } else {
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "Received unsupported credential type.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleForgotPassword() {
        val email = binding.etEmail.text.toString().trim()

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Enter your email to reset password"
            return
        }

        binding.tilEmail.error = null
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validateForm(email: String, password: String): Boolean {
        var isValid = true
        binding.tilEmail.error = null
        binding.tilPassword.error = null

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
        } else if (password.length < 8) {
            binding.tilPassword.error = "Password must be at least 8 characters"
            isValid = false
        }

        return isValid
    }



    private fun setLoadingState(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.text = ""
            binding.btnLogin.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.text = "Log In"
            binding.btnLogin.isEnabled = true
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun getErrorMessage(exception:Exception?): String{
        return when(exception) {
            is FirebaseAuthException -> {
                when(exception.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Invalid email format"
                    "ERROR_WRONG_PASSWORD" -> "Incorrect password"
                    "ERROR_USER_NOT_FOUND" -> "User not found"
                    else -> "Authentication failed. Please check your credentials"
                }
            }
            is FirebaseNetworkException -> "No Internet Connection"
            else -> exception?.localizedMessage?: "Authentication failed"
        }
    }
}