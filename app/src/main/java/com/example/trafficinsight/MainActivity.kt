package com.example.trafficsigns

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.trafficsigns.ml.TrafficSignModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainActivity : AppCompatActivity() {

    lateinit var selectBtn: Button
    lateinit var predictBtn: Button
    lateinit var resView: TextView
    lateinit var imageView: ImageView
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        selectBtn = findViewById(R.id.selectBtn)
        predictBtn = findViewById(R.id.predictBtn)
        resView = findViewById(R.id.resView)
        imageView = findViewById(R.id.imageView)

        var labels=application.assets.open("labels.txt").bufferedReader().readLines()


        selectBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            galleryLauncher.launch(intent)
        }

        predictBtn.setOnClickListener {
            if (::bitmap.isInitialized) {
                val tensorImage = TensorImage(DataType.FLOAT32)
                tensorImage.load(bitmap)

                // Image Processor to resize and normalize the image
                val imageProcessor = ImageProcessor.Builder()
                    .add(ResizeOp(30, 30, ResizeOp.ResizeMethod.BILINEAR))
                    .build()

                val processedImage = imageProcessor.process(tensorImage)

                val model = TrafficSignModel.newInstance(this)

                // Creates inputs for reference
                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 30, 30, 3), DataType.FLOAT32)
                inputFeature0.loadBuffer(processedImage.buffer)

                // Runs model inference and gets result
                val outputs = model.process(inputFeature0)
                val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

                var maxIdx = 0
                outputFeature0.forEachIndexed { index, fl ->
                    if (outputFeature0[maxIdx] < fl) {
                        maxIdx = index
                    }
                }
                resView.text = labels[maxIdx]

                // Releases model resources if no longer used
                model.close()
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                imageView.setImageBitmap(bitmap)
            }
        }
    }
}
