package mx.osg.camera

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.app.Activity
import android.content.Intent
import android.graphics.*
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
import kotlin.random.Random


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

        // All filters available on the app
        val textItems = ArrayList<CarouselPicker.PickerItem>()

        textItems.add(CarouselPicker.TextItem("Original", 9))   //0
        // Basics
        textItems.add(CarouselPicker.TextItem("Negativo", 9))   //1
        textItems.add(CarouselPicker.TextItem("Escala de Grises", 9))   //2
        textItems.add(CarouselPicker.TextItem("Brillo", 9))   //3
        textItems.add(CarouselPicker.TextItem("Contraste", 9))   //4
        textItems.add(CarouselPicker.TextItem("Gamma", 9))   //5
        textItems.add(CarouselPicker.TextItem("Separación RGB", 9))   //6
        // Convolutions
        textItems.add(CarouselPicker.TextItem("Smooth", 9))   //7
        textItems.add(CarouselPicker.TextItem("Gaussian Blur", 9))   //8
        textItems.add(CarouselPicker.TextItem("Sharpen", 9))   //9
        textItems.add(CarouselPicker.TextItem("Mean Removal", 9))   //10
        textItems.add(CarouselPicker.TextItem("Embossing", 9))   //11
        textItems.add(CarouselPicker.TextItem("Edge Detection", 9))   //12
        // Zoom
        textItems.add(CarouselPicker.TextItem("Zoom", 9))   //13
        // Extras

        val textAdapter = CarouselPicker.CarouselViewAdapter(this, textItems, 0)
        carouselPicker.adapter = textAdapter

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

            btnAplicar.setOnClickListener {

                when (carouselPicker.currentItem) {
                    0 -> {
                        try {
                            imageView.setImageBitmap(imageTaken)
                        } catch (e: Exception) {
                        }
                    }
                    1 -> {
                        try {
                            imageView.setImageBitmap(negative(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    2 -> {
                        try {
                            imageView.setImageBitmap(grayScale(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    3 -> {
                        try {
                            imageView.setImageBitmap(bright(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    4 -> {
                        try {
                            imageView.setImageBitmap(contrast(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    5 -> {
                        try {
                            imageView.setImageBitmap(gamma(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    6 -> {
                        try {
                            imageView.setImageBitmap(rgbFilter(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    7 -> { /* TODO */
                    }
                    8 -> { /* TODO */
                    }
                    9 -> { /* TODO */
                    }
                    10 -> { /* TODO */
                    }
                    11 -> { /* TODO */
                    }
                    12 -> { /* TODO */
                    }
                    13 -> { /* TODO */
                    }
                }
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

    // Function needed to place the bitmap in the same way as the photo was taken
    private fun RotateBitmap(source: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(90F)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

// Basic filters functions

    // Apply negative effect on the bitmap
    private fun negative(original: Bitmap): Bitmap? {
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

    // Apply grayscale effect on the bitmap
    private fun grayScale(original: Bitmap): Bitmap? {
        // Create mutable Bitmap to invert, argument true makes it mutable
        val gray = original.copy(Bitmap.Config.ARGB_8888, true)

        val canvas = Canvas(gray)
        val paint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val colorMatrixFilter = ColorMatrixColorFilter(colorMatrix)
        paint.colorFilter = colorMatrixFilter

        // Apply gray filter to new bitmap
        canvas.drawBitmap(gray, 0F, 0F, paint)

        // Return inverted Bitmap
        return gray
    }

    // Apply bright effect on the bitmap
    private fun bright(original: Bitmap): Bitmap? {
        val brightness = Random.nextInt(3, 6)

        // Create mutable Bitmap to invert, argument true makes it mutable
        val bri = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = bri.width
        val height = bri.height
        val pixels = IntArray(width * height)

        // Get original pixels
        bri.getPixels(pixels, 0, width, 0, 0, width, height)

        val powers = IntArray(256)
        for (i in powers.indices) powers[i] =
            (Math.pow(i / 255.0f.toDouble(), 1.0f / brightness.toDouble()) * 255).toInt()
        for (p in pixels.indices) {
            val r = Color.red(pixels[p])
            val g = Color.green(pixels[p])
            val b = Color.blue(pixels[p])
            val newR = powers[r]
            val newG = powers[g]
            val newB = powers[b]
            pixels[p] = Color.rgb(newR, newG, newB)
        }

        bri.setPixels(pixels, 0, width, 0, 0, width, height)

        // Return inverted Bitmap
        return bri
    }

    // Apply contrast effect on the bitmap
    private fun contrast(original: Bitmap): Bitmap? {
        // Create mutable Bitmap to invert, argument true makes it mutable
        val cont = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = cont.width
        val height = cont.height

        // color information
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int

        // Get through all pixels
        for (x in 0 until width) {
            for (y in 0 until height) {

                // get pixel color
                pixel = cont.getPixel(x, y)
                A = Color.alpha(pixel)

                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel)
                R = (((R / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (R < 0) R = 0
                else if (R > 255) R = 255

                G = Color.green(pixel)
                G = (((G / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (G < 0) G = 0
                else if (G > 255) G = 255

                B = Color.blue(pixel)
                B = (((B / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (B < 0) B = 0
                else if (B > 255) B = 255

                // set new pixel color to output bitmap
                cont.setPixel(x, y, Color.argb(A, R, G, B))
            }
        }

        // Return inverted Bitmap
        return cont
    }

    // Apply gamma effect on the bitmap
    private fun gamma(original: Bitmap): Bitmap? {
        val gamma = Random.nextInt(1, 4)

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gam = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = gam.width
        val height = gam.height
        val pixels = IntArray(width * height)

        gam.getPixels(pixels, 0, width, 0, 0, width, height)
        val powers = IntArray(256)
        for (i in powers.indices) powers[i] =
            (Math.pow(i / 255.0f.toDouble(), 1.0f / gamma.toDouble()) * 255).toInt()
        for (p in pixels.indices) {
            val r = Color.red(pixels[p])
            val g = Color.green(pixels[p])
            val b = Color.blue(pixels[p])
            val newR = powers[r]
            val newG = powers[g]
            val newB = powers[b]
            pixels[p] = Color.rgb(newR, newG, newB)
        }

        gam.setPixels(pixels, 0, width, 0, 0, width, height)

        // Return inverted Bitmap
        return gam
    }

    // Apply contrast effect on the bitmap
    private fun rgbFilter(original: Bitmap): Bitmap? {

        //Randomly select which colors to filter
        var color = 0xFFFF0000
        when (Random.nextInt(0, 3)) {
            0 -> color = 0x00FF0000
            1 -> color = 0x0000FF00
            2 -> color = 0x000000FF
        }

        // Create mutable Bitmap to invert, argument true makes it mutable
        val rgb = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = rgb.width
        val height = rgb.height
        val pixels = width * height

        // Get original pixels
        val pixel = IntArray(pixels)
        rgb.getPixels(pixel, 0, width, 0, 0, width, height)

        // Modify pixels
        for (i in 0 until pixels) pixel[i] = pixel[i] or color.toInt()
        rgb.setPixels(pixel, 0, width, 0, 0, width, height)

        // Return inverted Bitmap
        return rgb
    }

// Convolution filters functions

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
