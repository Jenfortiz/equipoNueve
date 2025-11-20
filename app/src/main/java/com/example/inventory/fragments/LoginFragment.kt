package com.example.inventory.fragments
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.provider.Settings
import androidx.fragment.app.Fragment
import java.util.concurrent.Executor

class LoginFragment : Fragment() {

    interface BiometricAuthListener {
        fun onBiometricAuthSuccess()
        fun onBiometricAuthError(error: String)
        fun onBiometricAuthFailed()
        fun onBiometricEnrollmentRequested(enrollIntent: Intent)
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var authListener: BiometricAuthListener? = null

    fun setBiometricAuthListener(listener: BiometricAuthListener) {
        this.authListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        executor = ContextCompat.getMainExecutor(requireContext())
        setupBiometricPrompt()
    }

    private fun setupBiometricPrompt() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        authListener?.onBiometricAuthError("Authentication error: $errString")
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    authListener?.onBiometricAuthSuccess()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    authListener?.onBiometricAuthFailed()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login")
            .setSubtitle("Log in using your finger")
            .setNegativeButtonText("Cancel")
            .build()
    }

    fun startAuthentication() {
        if (!isBiometricSupported()) {
            return
        }
        biometricPrompt.authenticate(promptInfo)
    }

    private fun isBiometricSupported(): Boolean {
        val biometricManager = BiometricManager.from(requireContext())
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK

        when (biometricManager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> return true

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, authenticators)
                }
                authListener?.onBiometricEnrollmentRequested(enrollIntent)
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                authListener?.onBiometricAuthError("No biometric features available on this device.")
                return false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                authListener?.onBiometricAuthError("Biometric features are currently unavailable.")
                return false
            }
            else -> {
                authListener?.onBiometricAuthError("An unknown biometric error occurred.")
                return false
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        authListener = null
    }

    companion object {
        const val TAG = "LoginFragment"
        fun newInstance() = LoginFragment()
    }
}