package com.feneix.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.feneix.app.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.translateButton.setOnClickListener {
            val textToTranslate = binding.inputText.text.toString()
            if (textToTranslate.isNotEmpty()) {
                translateText(textToTranslate)
            } else {
                Toast.makeText(this, R.string.enter_text, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun translateText(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.example.com/translate?text=$text&target=es")
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    val jsonObject = JSONObject(responseData)
                    val translatedText = jsonObject.getString("translatedText")
                    runOnUiThread {
                        binding.outputText.text = translatedText
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, R.string.translation_error, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, R.string.network_error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}