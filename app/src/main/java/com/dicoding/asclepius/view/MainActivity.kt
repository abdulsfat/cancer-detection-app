package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

class MainActivity : AppCompatActivity(), ImageClassifierHelper.ClassifierListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = this
        )

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.currentImageUri = uri
            binding.previewImageView.setImageURI(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
            showToast("No image selected")
        }
    }

    private fun analyzeImage() {
        viewModel.currentImageUri?.let { uri ->
            imageClassifierHelper.classifyStaticImage(uri)
        } ?: run {
            showToast("Please select an image first.")
        }
    }

    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
        results?.let {
            if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                val sortedCategories = it[0].categories.sortedByDescending { it?.score }
                val prediction = sortedCategories[0].label
                val score = NumberFormat.getPercentInstance().format(sortedCategories[0].score)

                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("imageUri", viewModel.currentImageUri.toString())
                    putExtra("prediction", prediction)
                    putExtra("score", score)
                    putExtra("inferenceTime", inferenceTime)
                }
                startActivity(intent)
            } else {
                showToast("Tidak ada kategori hasil klasifikasi")
            }
        }
    }

    override fun onError(error: String) {
        showToast("Error: $error")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
