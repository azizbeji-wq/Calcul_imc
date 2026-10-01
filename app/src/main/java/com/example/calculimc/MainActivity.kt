package com.example.calculimc

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPoids: TextInputEditText
    private lateinit var editTextTaille: TextInputEditText
    private lateinit var buttonCalculer: Button
    private lateinit var buttonEffacer: Button
    private lateinit var textViewImc: TextView
    private lateinit var textViewCategorie: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialization of views
        editTextPoids = findViewById(R.id.editTextPoids)
        editTextTaille = findViewById(R.id.editTextTaille)
        buttonCalculer = findViewById(R.id.buttonCalculer)
        buttonEffacer = findViewById(R.id.buttonEffacer)
        textViewImc = findViewById(R.id.textViewImc)
        textViewCategorie = findViewById(R.id.textViewCategorie)

        // Calculate button click listener
        buttonCalculer.setOnClickListener {
            calculerIMC()
        }

        // Clear button click listener
        buttonEffacer.setOnClickListener {
            effacerChamps()
        }
    }

    private fun calculerIMC() {
        // Reset errors
        editTextPoids.error = null
        editTextTaille.error = null

        val poidsStr = editTextPoids.text?.toString()?.trim().orEmpty()
        val tailleStr = editTextTaille.text?.toString()?.trim().orEmpty()

        // 1. Check if fields are empty
        var hasError = false
        if (poidsStr.isEmpty()) {
            editTextPoids.error = getString(R.string.error_empty_poids)
            hasError = true
        }
        if (tailleStr.isEmpty()) {
            editTextTaille.error = getString(R.string.error_empty_taille)
            hasError = true
        }

        if (hasError) {
            Toast.makeText(this, getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Convert inputs to Double
        val poids = poidsStr.replace(',', '.').toDoubleOrNull()
        val taille = tailleStr.replace(',', '.').toDoubleOrNull()

        // 3. Verify positive non-null values
        if (poids == null || poids <= 0) {
            editTextPoids.error = getString(R.string.error_invalid_poids)
            Toast.makeText(this, getString(R.string.error_invalid_poids), Toast.LENGTH_SHORT).show()
            return
        }

        if (taille == null || taille <= 0) {
            editTextTaille.error = getString(R.string.error_invalid_taille)
            Toast.makeText(this, getString(R.string.error_invalid_taille), Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Calculate IMC: IMC = weight / (height * height)
        val imc = poids / (taille * taille)

        // 5. Format IMC value to 2 decimal places
        val imcFormatted = String.format(Locale.getDefault(), "%.2f", imc)
        textViewImc.text = getString(R.string.label_imc_result, imcFormatted)

        // 6. Determine category and color using if, else if, else
        val (categoryResId, colorResId) = if (imc < 18.5) {
            R.string.cat_underweight to R.color.color_underweight
        } else if (imc < 25.0) {
            R.string.cat_normal to R.color.color_normal
        } else if (imc < 30.0) {
            R.string.cat_overweight to R.color.color_overweight
        } else if (imc < 35.0) {
            R.string.cat_obese_moderate to R.color.color_obese_moderate
        } else if (imc < 40.0) {
            R.string.cat_obese_severe to R.color.color_obese_severe
        } else {
            R.string.cat_obese_morbid to R.color.color_obese_morbid
        }

        val categoryText = getString(categoryResId)
        textViewCategorie.text = getString(R.string.label_category_result, categoryText)
        textViewCategorie.setTextColor(ContextCompat.getColor(this, colorResId))
    }

    private fun effacerChamps() {
        editTextPoids.text?.clear()
        editTextTaille.text?.clear()
        editTextPoids.error = null
        editTextTaille.error = null
        textViewImc.text = ""
        textViewCategorie.text = ""
        editTextPoids.requestFocus()
    }
}
