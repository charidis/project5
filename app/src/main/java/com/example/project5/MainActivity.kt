package com.example.project5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableEdgeToEdge()

        val button = findViewById<Button>(R.id.button)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val dateTextView = findViewById<TextView>(R.id.year)
        val techniqueTextView = findViewById<TextView>(R.id.title)

        button.setOnClickListener {
            getImageData { imageUrl, date, technique ->
                dateTextView.text = date
                techniqueTextView.text = technique

                if (imageUrl != null) {
                    Glide.with(this@MainActivity)
                        .load(imageUrl)
                        .fitCenter()
                        .into(imageView)
                }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getImageData(onSuccess: (String?, String?, String?) -> Unit) {
        val client = AsyncHttpClient()
        val apiKey = "8c13abe7-a502-4d0a-b52b-5047fc3f0462"
        val url = "https://api.harvardartmuseums.org/image?size=300&apikey=$apiKey"

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                val jsonObject = json.jsonObject
                val recordsArray = jsonObject.getJSONArray("records")

                val randomIndex = Random.nextInt(recordsArray.length())
                val record = recordsArray.getJSONObject(randomIndex)

                val imageUrl = record.optString("baseimageurl", null)
                val date = record.optString("date", "Unknown Date")
                val technique = record.optString("technique", "Unknown Technique")

                onSuccess(imageUrl, date, technique)
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.d("Image Error", errorResponse)
            }
        })
    }
}
