package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mendapatkan data dari Intent
        val imageUri = intent.getStringExtra("imageUri")
        val prediction = intent.getStringExtra("prediction")
        val score = intent.getStringExtra("score")
        val inferenceTime = intent.getLongExtra("inferenceTime", -1)

        // Menampilkan gambar, prediksi, skor, dan waktu inferensi
        binding.resultImage.setImageURI(Uri.parse(imageUri))
        binding.resultText.text = "$prediction: $score"
        binding.inferenceTimeText.text = "Inference Time: $inferenceTime ms"
    }
}
