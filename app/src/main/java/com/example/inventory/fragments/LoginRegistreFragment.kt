package com.example.inventory.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.inventory.R
import com.example.inventory.databinding.FragmentLoginAndRegistreBinding
import com.example.inventory.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginRegistreFragment : Fragment() {

    private var _binding: FragmentLoginAndRegistreBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private val MIN_PASSWORD_LENGTH = 6
    private val MAX_PASSWORD_LENGTH = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginAndRegistreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Inicializar colores desde resources (seguro en onViewCreated) ---
        val colorError = try {
            ContextCompat.getColor(requireContext(), R.color.red)
        } catch (e: Exception) {
            // fallback sencillo si no tienes el recurso
            android.graphics.Color.RED
        }
        val colorValid = try {
            ContextCompat.getColor(requireContext(), R.color.white)
        } catch (e: Exception) {
            android.graphics.Color.WHITE
        }

        // Inicial: botones deshabilitados (si quieres que estén inicialmente activos cambia esto)
        setButtonsEnabled(false)
        // Asegurar estilo inicial del borde
        setPasswordBorderColor(colorValid)

        // --- Listeners de botones (tu lógica de Firebase) ---
        binding.btnLogin.setOnClickListener {
            val email = binding.tietEmail.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()

            // comprobación final antes de enviar
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Introduce un email válido"
                return@setOnClickListener
            }
            if (password.length < MIN_PASSWORD_LENGTH) {
                // mostrar error final si intenta enviar con menos de 6
                binding.tilPassword.error = "Mínimo $MIN_PASSWORD_LENGTH dígitos"
                binding.tilPassword.isErrorEnabled = true
                setPasswordBorderColor(colorError)
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d("Login", "signInWithEmail:success")
                        (activity as? MainActivity)?.showHome()
                    } else {
                        Log.w("Login", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            context, "Authentication failed: ${task.exception?.message ?: ""}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        binding.tvRegistrarse.setOnClickListener {
            val email = binding.tietEmail.text.toString().trim()
            val password = binding.tietPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please enter email and password.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Introduce un email válido"
                return@setOnClickListener
            }
            if (password.length < MIN_PASSWORD_LENGTH) {
                binding.tilPassword.error = "Mínimo $MIN_PASSWORD_LENGTH dígitos"
                binding.tilPassword.isErrorEnabled = true
                setPasswordBorderColor(colorError)
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d("Register", "createUserWithEmail:success")
                        (activity as? MainActivity)?.showHome()
                    } else {
                        Log.w("Register", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            context, "Registration failed: ${task.exception?.message ?: ""}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        // --- TextWatcher para validación EN TIEMPO REAL ---
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = binding.tietEmail.text.toString().trim()
                val password = binding.tietPassword.text.toString().trim()

                // Validación en tiempo real del password:
                // Si tiene entre 1 y 5 caracteres -> mostrar error y borde rojo.
                // Si está vacío -> no mostrar error.
                // Si tiene >= 6 -> quitar error y borde blanco.
                if (password.isEmpty()) {
                    binding.tilPassword.error = null
                    binding.tilPassword.isErrorEnabled = false
                    setPasswordBorderColor(colorValid)
                } else if (password.length in 1 until MIN_PASSWORD_LENGTH) {
                    binding.tilPassword.error = "Mínimo $MIN_PASSWORD_LENGTH dígitos"
                    binding.tilPassword.isErrorEnabled = true
                    setPasswordBorderColor(colorError)
                } else {
                    binding.tilPassword.error = null
                    binding.tilPassword.isErrorEnabled = false
                    setPasswordBorderColor(colorValid)
                }

                // Habilitar botones sólo si email no vacío y password >= MIN_PASSWORD_LENGTH
                val enabled = email.isNotEmpty() && password.length >= MIN_PASSWORD_LENGTH
                setButtonsEnabled(enabled)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        // Añadir watcher a ambos campos (email por si quieres habilitar botones immediatamente)
        binding.tietEmail.addTextChangedListener(textWatcher)
        binding.tietPassword.addTextChangedListener(textWatcher)
    }

    // ---------- helpers ----------
    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnLogin.isEnabled = enabled
        binding.tvRegistrarse.isEnabled = enabled
        binding.btnLogin.alpha = if (enabled) 1.0f else 0.4f
        binding.tvRegistrarse.alpha = if (enabled) 1.0f else 0.4f
    }

    private fun setPasswordBorderColor(color: Int) {
        try {
            binding.tilPassword.boxStrokeColor = color
        } catch (e: Exception) {
            try {
                binding.tilPassword.setBoxStrokeColor(color)
            } catch (_: Exception) {
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}