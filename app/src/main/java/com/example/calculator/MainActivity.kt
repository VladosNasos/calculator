package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.view.View
import android.util.Log
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private var lastNumeric: Boolean = false
    private var stateError: Boolean = false
    private var lastDot: Boolean = false
    private val TAG = "Calculator"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.text_view_result)
        setButtonListeners()
    }

    private fun setButtonListeners() {
        // Цифровые кнопки
        val buttonIds = listOf(
            R.id.button_zero, R.id.button_one, R.id.button_two,
            R.id.button_three, R.id.button_four, R.id.button_five,
            R.id.button_six, R.id.button_seven, R.id.button_eight,
            R.id.button_nine
        )

        for (id in buttonIds) {
            findViewById<Button>(id).setOnClickListener { onDigit(it) }
        }

        // Точка
        findViewById<Button>(R.id.button_decimal).setOnClickListener { onDecimalPoint(it) }

        // Операторы
        val operatorIds = listOf(
            R.id.button_plus, R.id.button_minus,
            R.id.button_multiply, R.id.button_divide
        )

        for (id in operatorIds) {
            findViewById<Button>(id).setOnClickListener { onOperator(it) }
        }

        // Кнопка равно
        findViewById<Button>(R.id.button_equals).setOnClickListener { onEqual() }

        // Кнопка очистки
        findViewById<Button>(R.id.button_clear).setOnClickListener { onClear() }

        // Кнопка удаления
        findViewById<Button>(R.id.button_delete).setOnClickListener { onDelete() }
    }

    private fun onDigit(view: View) {
        if (stateError) {
            resultTextView.text = (view as Button).text
            stateError = false
        } else {
            resultTextView.append((view as Button).text)
        }
        lastNumeric = true
    }

    private fun onDecimalPoint(view: View) {
        if (lastNumeric && !lastDot && !stateError) {
            resultTextView.append(".")
            lastNumeric = false
            lastDot = true
        }
    }

    private fun onOperator(view: View) {
        if (lastNumeric && !stateError) {
            resultTextView.append((view as Button).text)
            lastNumeric = false
            lastDot = false
        }
    }

    private fun onEqual() {
        if (lastNumeric && !stateError) {
            val txt = resultTextView.text.toString()
            try {
                val expression = ExpressionBuilder(txt.replace('×', '*').replace('÷', '/')).build()
                val result = expression.evaluate()
                resultTextView.text = result.toString()
                lastDot = true
            } catch (ex: ArithmeticException) {
                resultTextView.text = getString(R.string.error_invalid_expression)
                stateError = true
                lastNumeric = false
            } catch (ex: Exception) {
                resultTextView.text = getString(R.string.error_invalid_expression)
                stateError = true
                lastNumeric = false
                Log.e(TAG, "Error evaluating expression", ex)
            }
        }
    }

    private fun onClear() {
        resultTextView.text = ""
        lastNumeric = false
        stateError = false
        lastDot = false
    }

    private fun onDelete() {
        if (!stateError && resultTextView.text.isNotEmpty()) {
            val currentText = resultTextView.text.toString()
            resultTextView.text = currentText.substring(0, currentText.length - 1)
            lastNumeric = currentText.last().isDigit()
        }
    }
}