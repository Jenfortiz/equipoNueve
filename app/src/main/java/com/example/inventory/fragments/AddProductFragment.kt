package com.example.inventory.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.inventory.R
import com.google.android.material.textfield.TextInputEditText

class AddProductFragment : Fragment() {

    private lateinit var tietCode: TextInputEditText
    private lateinit var tietName: TextInputEditText
    private lateinit var tietPrice: TextInputEditText
    private lateinit var tietQuantity: TextInputEditText
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // find views
        val ivBack = view.findViewById<ImageView>(R.id.iv_back)
        tietCode = view.findViewById(R.id.tiet_product_code)
        tietName = view.findViewById(R.id.tiet_product_name)
        tietPrice = view.findViewById(R.id.tiet_product_price)
        tietQuantity = view.findViewById(R.id.tiet_product_quantity)
        btnSave = view.findViewById(R.id.btn_save_product)

        // back button
        ivBack.setOnClickListener { parentFragmentManager.popBackStack() }

        // filters
        val onlyDigitsFilter = InputFilter { source, start, end, _, _, _ ->
            val sb = StringBuilder()
            for (i in start until end) {
                if (source[i].isDigit()) sb.append(source[i])
            }
            if (sb.length == (end - start)) null else sb.toString()
        }

        val allowedCharsFilter = InputFilter { source, start, end, _, _, _ ->
            val sb = StringBuilder()
            for (i in start until end) {
                val c = source[i]
                if (c.isLetterOrDigit() || c.isWhitespace() || c in listOf('-', '_', '.', ',')) {
                    sb.append(c)
                }
            }
            if (sb.length == (end - start)) null else sb.toString()
        }

        tietCode.filters = arrayOf(InputFilter.LengthFilter(4), onlyDigitsFilter)
        tietName.filters = arrayOf(InputFilter.LengthFilter(40), allowedCharsFilter)
        tietPrice.filters = arrayOf(InputFilter.LengthFilter(20), onlyDigitsFilter)
        tietQuantity.filters = arrayOf(InputFilter.LengthFilter(4), onlyDigitsFilter)

        // watcher for button update
        val tw = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButtonState()
            }
        }

        tietCode.addTextChangedListener(tw)
        tietName.addTextChangedListener(tw)
        tietPrice.addTextChangedListener(tw)
        tietQuantity.addTextChangedListener(tw)

        updateSaveButtonState()

        btnSave.setOnClickListener {
            val code = tietCode.text?.toString()?.trim()
            val name = tietName.text?.toString()?.trim()
            val price = tietPrice.text?.toString()?.trim()
            val qty = tietQuantity.text?.toString()?.trim()

            if (code.isNullOrEmpty() || name.isNullOrEmpty() || price.isNullOrEmpty() || qty.isNullOrEmpty()) {
                showToast("Completa todos los campos antes de guardar")
                return@setOnClickListener
            }

            showToast("Guardado: $code - $name - $price - qty:$qty")
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateSaveButtonState() {
        val code = tietCode.text?.toString()?.trim()
        val name = tietName.text?.toString()?.trim()
        val price = tietPrice.text?.toString()?.trim()
        val qty = tietQuantity.text?.toString()?.trim()

        val allFilled = !code.isNullOrEmpty()
                && !name.isNullOrEmpty()
                && !price.isNullOrEmpty()
                && !qty.isNullOrEmpty()

        if (allFilled) {
            btnSave.isEnabled = true
            btnSave.alpha = 1f
            btnSave.isClickable = true
            btnSave.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_orange)
        } else {
            btnSave.isEnabled = false
            btnSave.alpha = 0.4f
            btnSave.isClickable = false
            btnSave.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_button_disabled)
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}





