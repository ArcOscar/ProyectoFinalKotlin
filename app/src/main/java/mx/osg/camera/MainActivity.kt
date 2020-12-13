package mx.osg.camera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


private const val REQUEST_CODE = 42
private lateinit var photoFile: File

class MainActivity : AppCompatActivity() {

    // Check if need to ask permissions
    // Lollipop and before versions will not require permissions
    private fun shouldAskPermissions(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    // Ask for the permissions
    @RequiresApi(Build.VERSION_CODES.M)
    private fun askPermissions() {
        val permissions = arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        val requestCode = 200
        requestPermissions(permissions, requestCode)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Clicking the button for the camera
        btnTakePicture.setOnClickListener {
            takingPhoto()
        }
    }

    // Opening camera and taking the photo
    private fun takingPhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = getPhotoFile(System.currentTimeMillis().toString())

        val fileProvider = FileProvider.getUriForFile(
            this,
            "mx.osg.camera.fileprovider",
            photoFile
        )

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CODE)
        } else {
            Toast.makeText(this, "No es posible abrir la cámara", Toast.LENGTH_LONG).show()
        }
    }

    private fun getPhotoFile(fName: String): File {
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val imageTaken = RotateBitmap(BitmapFactory.decodeFile(photoFile.absolutePath))
            imageView.setImageBitmap(imageTaken)

            btnSavePicture.visibility = View.VISIBLE
            btnNegative.visibility = View.VISIBLE

            btnNegative.setOnClickListener {
                try { imageView.setImageBitmap(negative(imageTaken)) }
                catch (e: Exception) {}
            }

            btnSavePicture.setOnClickListener {
                // Check if the app has the required permissions. If not granted, will ask for them
                if (shouldAskPermissions())
                    askPermissions()
                saveToGallery()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent)
        }

    }

    fun RotateBitmap(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90F)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    // Apply negative effect on the bitmap
    fun negative(original: Bitmap): Bitmap? {
        // Create mutable Bitmap to invert, argument true makes it mutable
        val neg = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = neg.width
        val height = neg.height
        val pixels = width * height

        // Get original pixels
        val pixel = IntArray(pixels)
        neg.getPixels(pixel, 0, width, 0, 0, width, height)

        // Modify pixels
        for (i in 0 until pixels) pixel[i] = pixel[i] xor 0x00FFFFFF
        neg.setPixels(pixel, 0, width, 0, 0, width, height)

        // Return inverted Bitmap
        return neg
    }

    // Saving the photo into the gallery
    private fun saveToGallery() {
        imageView.destroyDrawingCache()
        imageView.buildDrawingCache()
        val bm = imageView.drawingCache
        MediaStore.Images.Media.insertImage(
            contentResolver, bm,
            System.currentTimeMillis().toString() + ".jpg", "Pruebas de Proyecto Final"
        )
        Toast.makeText(applicationContext, "Imagen guardada en galería", Toast.LENGTH_LONG).show()
    }
}
