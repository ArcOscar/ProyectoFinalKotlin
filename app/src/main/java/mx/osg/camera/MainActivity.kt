package mx.osg.camera

import `in`.goodiebag.carouselpicker.CarouselPicker
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.Color.*
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
        textItems.add(CarouselPicker.TextItem("Zoom", 9))   //13    //FALTA JEJE
        // Extras
        textItems.add(CarouselPicker.TextItem("Lossy", 9))   //14
        textItems.add(CarouselPicker.TextItem("Stand", 9))   //15
        textItems.add(CarouselPicker.TextItem("Sepia", 9))   //16
        textItems.add(CarouselPicker.TextItem("Rotate", 9))   //17
        textItems.add(CarouselPicker.TextItem("Pixels", 9))   //18

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

    @RequiresApi(Build.VERSION_CODES.O)
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
                    7 -> {
                        try {
                            imageView.setImageBitmap(smooth(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    8 -> {
                        try {
                            imageView.setImageBitmap(gaussianBlur(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    9 -> {
                        try {
                            imageView.setImageBitmap(sharpen(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    10 -> {
                        try {
                            imageView.setImageBitmap(mean(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    11 -> {
                        try {
                            imageView.setImageBitmap(embossing(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    12 -> {
                        try {
                            imageView.setImageBitmap(edge(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    13 -> { /* TODO */
                    }
                    14 -> {
                        try {
                            imageView.setImageBitmap(lossy(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    15 -> {
                        try {
                            imageView.setImageBitmap(stand(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    16 -> {
                        try {
                            imageView.setImageBitmap(sepia(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    17 -> {
                        try {
                            imageView.setImageBitmap(rotate(imageTaken))
                        } catch (e: Exception) {
                        }
                    }
                    18 -> {
                        try {
                            imageView.setImageBitmap(pixels(imageTaken))
                        } catch (e: Exception) {
                        }
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

        // Return Bitmap
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

        // Return Bitmap
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
                A = alpha(pixel)

                // apply filter contrast for every channel R, G, B
                R = red(pixel)
                R = (((R / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (R < 0) R = 0
                else if (R > 255) R = 255

                G = green(pixel)
                G = (((G / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (G < 0) G = 0
                else if (G > 255) G = 255

                B = blue(pixel)
                B = (((B / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (B < 0) B = 0
                else if (B > 255) B = 255

                // set new pixel color to output bitmap
                cont.setPixel(x, y, argb(A, R, G, B))
            }
        }

        // Return Bitmap
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
            val r = red(pixels[p])
            val g = green(pixels[p])
            val b = blue(pixels[p])
            val newR = powers[r]
            val newG = powers[g]
            val newB = powers[b]
            pixels[p] = rgb(newR, newG, newB)
        }

        gam.setPixels(pixels, 0, width, 0, 0, width, height)

        // Return Bitmap
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

        // Return Bitmap
        return rgb
    }

// Convolution filters functions

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convolution(
        ori: Bitmap,
        mod: Bitmap,
        matrix: Array<Array<Int>>,
        sumMatrix: Int,
        extra: Int
    ): Bitmap {
        val w = ori.width
        val h = ori.height

        //apply the filter
                var R1 = 0
                var R2 = 0
                var R3 = 0
                var R4 = 0
                var R5 = 0
                var R6 = 0
                var R7 = 0
                var R8 = 0
                var R9 = 0
                var B1 = 0
                var B2 = 0
                var B3 = 0
                var B4 = 0
                var B5 = 0
                var B6 = 0
                var B7 = 0
                var B8 = 0
                var B9 = 0
                var G1 = 0
                var G2 = 0
                var G3 = 0
                var G4 = 0
                var G5 = 0
                var G6 = 0
                var G7 = 0
                var G8 = 0
                var G9 = 0
                var RP = 0
                var GP = 0
                var BP = 0

                for (i in 2 until w - 2) {
                    for (j in 2 until h - 2) {

                        var pixelTL = ori.getPixel(i - 1, j - 1)
                        R2 = Color.red(pixelTL) * matrix[0][0]
                        G2 = Color.green(pixelTL) * matrix[0][0]
                        B2 = Color.blue(pixelTL) * matrix[0][0]
                        var pixelT = ori.getPixel(i, j - 1)
                        R3 = Color.red(pixelT) * matrix[1][0]
                        G3 = Color.green(pixelT) * matrix[1][0]
                        B3 = Color.blue(pixelT) * matrix[1][0]
                        var pixelTR = ori.getPixel(i + 1, j - 1)
                        R4 = Color.red(pixelTR) * matrix[2][0]
                        G4 = Color.green(pixelTR) * matrix[2][0]
                        B4 = Color.blue(pixelTR) * matrix[2][0]

                        var pixelML = ori.getPixel(i - 1, j)
                        R1 = Color.red(pixelML) * matrix[0][1]
                        G1 = Color.green(pixelML) * matrix[0][1]
                        B1 = Color.blue(pixelML) * matrix[0][1]
                        var pixeMT = ori.getPixel(i, j)
                        R5 = Color.red(pixeMT) * matrix[1][1]
                        G5 = Color.green(pixeMT) * matrix[1][1]
                        B5 = Color.blue(pixeMT) * matrix[1][1]
                        var pixelMR = ori.getPixel(i + 1, j)
                        R9 = Color.red(pixelMR) * matrix[2][1]
                        G9 = Color.green(pixelMR) * matrix[2][1]
                        B9 = Color.blue(pixelMR) * matrix[2][1]

                        var pixelBR = ori.getPixel(i + 1, j + 1)
                        R6 = Color.red(pixelBR) * matrix[2][2]
                        G6 = Color.green(pixelBR) * matrix[2][2]
                        B6 = Color.blue(pixelBR) * matrix[2][2]
                        var pixelB = ori.getPixel(i, j + 1)
                        R7 = Color.red(pixelB) * matrix[1][2]
                        G7 = Color.green(pixelB) * matrix[1][2]
                        B7 = Color.blue(pixelB) * matrix[1][2]
                        var pixelBL = ori.getPixel(i - 1, j + 1)
                        R8 = Color.red(pixelBL) * matrix[0][2]
                        G8 = Color.green(pixelBL) * matrix[0][2]
                        B8 = Color.blue(pixelBL) * matrix[0][2]

                        RP = (R1 + R2 + R3 + R4 + R5 + R6 + R7 + R8 + R9) / sumMatrix + extra
                        GP = (G1 + G2 + G3 + G4 + G5 + G6 + G7 + G8 + G9) / sumMatrix + extra
                        BP = (B1 + B2 + B3 + B4 + B5 + B6 + B7 + B8 + B9) / sumMatrix + extra

                        mod.setPixel(i, j, argb(255, RP, GP, BP))
                    }
                }

        // return new bitmap
        return mod
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun smooth(original: Bitmap): Bitmap? {
        val kernel= arrayOf(
            arrayOf(1, 1, 1),
            arrayOf(1, 1, 1),
            arrayOf(1, 1, 1)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gSmo = original.copy(Bitmap.Config.ARGB_8888, true)
        val gSmo2 = original.copy(Bitmap.Config.ARGB_8888, true)

        var sum = 1
        var extra = 9

        // Return Bitmap
        return convolution(gSmo, gSmo2, kernel, sum, extra)
    }

    // Apply gaussianBlur effect on the bitmap
    @RequiresApi(Build.VERSION_CODES.O)
    private fun gaussianBlur(original: Bitmap): Bitmap? {
        val gauss = arrayOf(
            arrayOf(1, 2, 1),
            arrayOf(2, 4, 2),
            arrayOf(1, 2, 1)
        )

        var sum = 0
        var extra = 0

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gGauss = original.copy(Bitmap.Config.ARGB_8888, true)
        val gGauss2 = original.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until 3)
            for (x in 0 until 3)
                sum += gauss[x][y]

        // Return Bitmap
        return convolution(gGauss, gGauss2, gauss, sum, extra)
    }

    // Apply sharpen effect on the bitmap
    @RequiresApi(Build.VERSION_CODES.O)
    fun sharpen(original: Bitmap): Bitmap? {
        val kernel= arrayOf(
            arrayOf(0, -2, 0),
            arrayOf(-2, 11, -2),
            arrayOf(0, -2, 0)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gSharp = original.copy(Bitmap.Config.ARGB_8888, true)
        val gSharp2 = original.copy(Bitmap.Config.ARGB_8888, true)

        var sum = 0
        var extra = 0

        for (y in 0 until 3)
            for (x in 0 until 3)
                sum += kernel[x][y]

        // Return Bitmap
        return convolution(gSharp, gSharp2, kernel, sum, extra)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun mean(original: Bitmap): Bitmap? {
        val kernel= arrayOf(
            arrayOf(-1, -1, -1),
            arrayOf(-1, 9, -1),
            arrayOf(-1, -1, -1)
        )


        // Create mutable Bitmap to invert, argument true makes it mutable
        val gMean = original.copy(Bitmap.Config.ARGB_8888, true)
        val gMean2 = original.copy(Bitmap.Config.ARGB_8888, true)

        var sum = 0
        var extra = 0

        for (y in 0 until 3)
            for (x in 0 until 3)
                sum += kernel[x][y]

        // Return Bitmap
        return convolution(gMean, gMean2, kernel, sum, extra)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun embossing(original: Bitmap): Bitmap? {
        val kernel= arrayOf(
            arrayOf(-1, 0, -1),
            arrayOf(0, 4, 0),
            arrayOf(-1, 0, -1)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gEmb = original.copy(Bitmap.Config.ARGB_8888, true)
        val gEmb2 = original.copy(Bitmap.Config.ARGB_8888, true)

        var sum = 1
        var extra = 127

        // Return Bitmap
        return convolution(gEmb, gEmb2, kernel, sum, extra)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun edge(original: Bitmap): Bitmap? {
        val kernel= arrayOf(
            arrayOf(1, 1, 1),
            arrayOf(0, 0, 0),
            arrayOf(-1, -1, -1)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gEdg = original.copy(Bitmap.Config.ARGB_8888, true)
        val gEdg2 = original.copy(Bitmap.Config.ARGB_8888, true)

        var sum = 1
        var extra = 127

        // Return Bitmap
        return convolution(gEdg, gEdg2, kernel, sum, extra)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun lossy(original: Bitmap): Bitmap? {
        val kernel= arrayOf(
            arrayOf(1, -2, 1),
            arrayOf(-2, 4, -2),
            arrayOf(-2, 1, -2)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gEmb = original.copy(Bitmap.Config.ARGB_8888, true)
        val gEmb2 = original.copy(Bitmap.Config.ARGB_8888, true)

        var sum = 1
        var extra = 70

        // Return Bitmap
        return convolution(gEmb, gEmb2, kernel, sum, extra)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stand(original: Bitmap): Bitmap? {

        val width = original.getWidth()
        val height = original.getHeight()
        val bmOut = Bitmap.createBitmap(width, height, original.getConfig())
        var A:Int
        var R:Int
        var G:Int
        var B:Int
        var pixel:Int

        for (x in 0 until width)
        {
            for (y in 0 until height)
            {
                // get pixel color
                pixel = original.getPixel(x, y)
                // apply filtering on each channel R, G, B
                A = Color.alpha(pixel)
                R = (Color.red(pixel) * 5)
                G = (Color.green(pixel) * 2)
                B = (Color.blue(pixel) * 2)
                // set new color pixel to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B))
            }
        }

        return bmOut
    }

    fun sepia(src: Bitmap): Bitmap? {
        // image size
        var src = src
        val width = src!!.width
        val height = src.height
        // create output bitmap
        val bmOut = Bitmap.createBitmap(width, height, src.config)
        // constant grayscale
        val GS_RED = 0.3
        val GS_GREEN = 0.59
        val GS_BLUE = 0.11
        // color information
        var A: Int
        var R: Int
        var G: Int
        var B: Int
        var pixel: Int

        // scan through all pixels
        for (x in 0 until width) {
            for (y in 0 until height) {
                // get pixel color
                pixel = src.getPixel(x, y)
                // get color on each channel
                A = alpha(pixel)
                R = red(pixel)
                G = green(pixel)
                B = blue(pixel)
                // apply grayscale sample
                R = (GS_RED * R + GS_GREEN * G + GS_BLUE * B).toInt()
                G = R
                B = G

                // apply intensity level for sepid-toning on each channel
                R += 110
                if (R > 255) {
                    R = 255
                }
                G += 65
                if (G > 255) {
                    G = 255
                }
                B += 20
                if (B > 255) {
                    B = 255
                }

                // set new pixel color to output image
                bmOut.setPixel(x, y, argb(A, R, G, B))
            }
        }

        return bmOut
    }

    fun rotate(source: Bitmap): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(90F)
        val rot = source.copy(Bitmap.Config.ARGB_8888, true)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun pixels(original: Bitmap): Bitmap? {

        // Create mutable Bitmap to invert, argument true makes it mutable
        val pix = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = pix.width
        val height = pix.height

        val tamPix = 80
        var contPix = 0
        var R = 0
        var B = 0
        var G = 0

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (contPix == tamPix || x == 0) {
                    R = red(pix.getPixel(x, y))
                    G = red(pix.getPixel(x, y))
                    B = red(pix.getPixel(x, y))
                    contPix = 0
                }
                pix.setPixel(x, y, rgb(R, G, B))
                contPix++
            }
        }

        // Return inverted Bitmap
        return pix
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