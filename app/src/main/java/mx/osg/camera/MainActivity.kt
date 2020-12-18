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
import kotlin.math.pow
import kotlin.random.Random


private const val REQUEST_CODE = 42
private lateinit var photoFile: File

@Suppress("DEPRECATION")
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
        textItems.add(CarouselPicker.TextItem("RGB", 9))   //6
        // Convolutions
        textItems.add(CarouselPicker.TextItem("Smooth", 9))   //7
        textItems.add(CarouselPicker.TextItem("Gaussian Blur", 9))   //8
        textItems.add(CarouselPicker.TextItem("Sharpen", 9))   //9
        textItems.add(CarouselPicker.TextItem("Mean", 9))   //10
        textItems.add(CarouselPicker.TextItem("Embossing", 9))   //11
        textItems.add(CarouselPicker.TextItem("Edge", 9))   //12
        // Zoom
        textItems.add(CarouselPicker.TextItem("Zoom", 9))   //13
        // Extras
        textItems.add(CarouselPicker.TextItem("Loose", 9))   //14
        textItems.add(CarouselPicker.TextItem("Vice", 9))   //15
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val imageTaken = rotateBitmap(BitmapFactory.decodeFile(photoFile.absolutePath))
            imageView.setImageBitmap(imageTaken)

            btnSavePicture.visibility = View.VISIBLE

            btnAplicar.setOnClickListener {

                when (carouselPicker.currentItem) {
                    0 -> try {
                        imageView.setImageBitmap(imageTaken)
                    } catch (e: Exception) {
                    }
                    1 -> try {
                        imageView.setImageBitmap(negative(imageTaken))
                    } catch (e: Exception) {
                    }
                    2 -> try {
                        imageView.setImageBitmap(grayScale(imageTaken))
                    } catch (e: Exception) {
                    }
                    3 -> try {
                        imageView.setImageBitmap(bright(imageTaken))
                    } catch (e: Exception) {
                    }
                    4 -> try {
                        imageView.setImageBitmap(contrast(imageTaken))
                    } catch (e: Exception) {
                    }
                    5 -> try {
                        imageView.setImageBitmap(gamma(imageTaken))
                    } catch (e: Exception) {
                    }
                    6 -> try {
                        imageView.setImageBitmap(rgbFilter(imageTaken))
                    } catch (e: Exception) {
                    }
                    7 -> try {
                        imageView.setImageBitmap(smooth(imageTaken))
                    } catch (e: Exception) {
                    }
                    8 -> try {
                        imageView.setImageBitmap(gaussianBlur(imageTaken))
                    } catch (e: Exception) {
                    }
                    9 -> try {
                        imageView.setImageBitmap(sharpen(imageTaken))
                    } catch (e: Exception) {
                    }
                    10 -> try {
                        imageView.setImageBitmap(mean(imageTaken))
                    } catch (e: Exception) {
                    }
                    11 -> try {
                        imageView.setImageBitmap(embossing(imageTaken))
                    } catch (e: Exception) {
                    }
                    12 -> try {
                        imageView.setImageBitmap(edge(imageTaken))
                    } catch (e: Exception) {
                    }
                    13 -> try {
                        imageView.setImageBitmap(zoom(imageTaken))
                    } catch (e: Exception) {
                    }
                    14 -> try {
                        imageView.setImageBitmap(loose(imageTaken))
                    } catch (e: Exception) {
                    }
                    15 -> try {
                        imageView.setImageBitmap(vice(imageTaken))
                    } catch (e: Exception) {
                    }
                    16 -> try {
                        imageView.setImageBitmap(sepia(imageTaken))
                    } catch (e: Exception) {
                    }
                    17 -> try {
                        imageView.setImageBitmap(rotate(imageTaken))
                    } catch (e: Exception) {
                    }
                    18 -> try {
                        imageView.setImageBitmap(pixels(imageTaken))
                    } catch (e: Exception) {
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
    private fun rotateBitmap(source: Bitmap): Bitmap {
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
            ((i / 255.0f.toDouble()).pow(1.0f / brightness.toDouble()) * 255).toInt()
        for (p in pixels.indices) {
            val r = red(pixels[p])
            val g = green(pixels[p])
            val b = blue(pixels[p])
            val newR = powers[r]
            val newG = powers[g]
            val newB = powers[b]
            pixels[p] = rgb(newR, newG, newB)
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
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var pixel: Int

        // Get through all pixels
        for (x in 0 until width) {
            for (y in 0 until height) {

                // get pixel color
                pixel = cont.getPixel(x, y)
                a = alpha(pixel)

                // apply filter contrast for every channel R, G, B
                r = red(pixel)
                r = (((r / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (r < 0) r = 0
                else if (r > 255) r = 255

                g = green(pixel)
                g = (((g / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (g < 0) g = 0
                else if (g > 255) g = 255

                b = blue(pixel)
                b = (((b / 255.0 - 0.5) * 2.5 + 0.5) * 255.0).toInt()
                if (b < 0) b = 0
                else if (b > 255) b = 255

                // set new pixel color to output bitmap
                cont.setPixel(x, y, argb(a, r, g, b))
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
            ((i / 255.0f.toDouble()).pow(1.0f / gamma.toDouble()) * 255).toInt()
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

    private fun convolution(
        ori: Bitmap,
        mod: Bitmap,
        matrix: Array<Array<Int>>,
        sumMatrix: Int
    ): Bitmap {
        val w = ori.width
        val h = ori.height

        //apply the filter
        val r =
            arrayOf(
                0, 0, 0,
                0, 0, 0,
                0, 0, 0
            )

        val g =
            arrayOf(
                0, 0, 0,
                0, 0, 0,
                0, 0, 0
            )

        val b =
            arrayOf(
                0, 0, 0,
                0, 0, 0,
                0, 0, 0
            )

        var rP = 0
        var gP = 0
        var bP = 0

        for (i in 2 until w - 2) {
            for (j in 2 until h - 2) {

                val pixelTL = ori.getPixel(i - 1, j - 1)
                r[1] = red(pixelTL) * matrix[0][0]
                g[1] = green(pixelTL) * matrix[0][0]
                b[1] = blue(pixelTL) * matrix[0][0]
                val pixelT = ori.getPixel(i, j - 1)
                r[2] = red(pixelT) * matrix[1][0]
                g[2] = green(pixelT) * matrix[1][0]
                b[2] = blue(pixelT) * matrix[1][0]
                val pixelTR = ori.getPixel(i + 1, j - 1)
                r[3] = red(pixelTR) * matrix[2][0]
                g[3] = green(pixelTR) * matrix[2][0]
                b[3] = blue(pixelTR) * matrix[2][0]

                val pixelML = ori.getPixel(i - 1, j)
                r[0] = red(pixelML) * matrix[0][1]
                g[0] = green(pixelML) * matrix[0][1]
                b[0] = blue(pixelML) * matrix[0][1]
                val pixeMT = ori.getPixel(i, j)
                r[4] = red(pixeMT) * matrix[1][1]
                g[4] = green(pixeMT) * matrix[1][1]
                b[4] = blue(pixeMT) * matrix[1][1]
                val pixelMR = ori.getPixel(i + 1, j)
                r[8] = red(pixelMR) * matrix[2][1]
                g[8] = green(pixelMR) * matrix[2][1]
                b[8] = blue(pixelMR) * matrix[2][1]

                val pixelBR = ori.getPixel(i + 1, j + 1)
                r[5] = red(pixelBR) * matrix[2][2]
                g[5] = green(pixelBR) * matrix[2][2]
                b[5] = blue(pixelBR) * matrix[2][2]
                val pixelB = ori.getPixel(i, j + 1)
                r[6] = red(pixelB) * matrix[1][2]
                g[6] = green(pixelB) * matrix[1][2]
                b[6] = blue(pixelB) * matrix[1][2]
                val pixelBL = ori.getPixel(i - 1, j + 1)
                r[7] = red(pixelBL) * matrix[0][2]
                g[7] = green(pixelBL) * matrix[0][2]
                b[7] = blue(pixelBL) * matrix[0][2]

                for (x in 0 until 9) {
                    rP += r[x]
                    gP += g[x]
                    bP += b[x]
                }

                rP /= sumMatrix
                gP /= sumMatrix
                bP /= sumMatrix

                if (rP > 255) rP = 255
                else if (rP < 0) rP = 0

                if (gP > 255) gP = 255
                else if (gP < 0) gP = 0

                if (bP > 255) bP = 255
                else if (bP < 0) bP = 0

                mod.setPixel(i, j, argb(255, rP, gP, bP))
            }
        }

        // return new bitmap
        return mod
    }

    private fun smooth(original: Bitmap): Bitmap? {
        val kernel = arrayOf(
            arrayOf(1, 1, 1),
            arrayOf(1, -6, 1),
            arrayOf(1, 1, 1)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gSmo = original.copy(Bitmap.Config.ARGB_8888, true)
        val gSmo2 = original.copy(Bitmap.Config.ARGB_8888, true)

        var sum = 1

        for (y in 0 until 3)
            for (x in 0 until 3)
                sum += kernel[x][y]

        // Return Bitmap
        return convolution(gSmo, gSmo2, kernel, sum)
    }

    // Apply gaussianBlur effect on the bitmap
    private fun gaussianBlur(original: Bitmap): Bitmap? {
        val gauss = arrayOf(
            arrayOf(1, 2, 1),
            arrayOf(2, 4, 2),
            arrayOf(1, 2, 1)
        )

        var sum = 0

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gGauss = original.copy(Bitmap.Config.ARGB_8888, true)
        val gGauss2 = original.copy(Bitmap.Config.ARGB_8888, true)

        for (y in 0 until 3)
            for (x in 0 until 3)
                sum += gauss[x][y]

        // Return Bitmap
        return convolution(gGauss, gGauss2, gauss, sum)
    }

    // Apply sharpen effect on the bitmap
    private fun sharpen(original: Bitmap): Bitmap? {
        val kernel = arrayOf(
            arrayOf(-2, -1, 0),
            arrayOf(-1, 1, 1),
            arrayOf(0, 1, 2)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gSharp = original.copy(Bitmap.Config.ARGB_8888, true)
        val gSharp2 = original.copy(Bitmap.Config.ARGB_8888, true)

        val sum = 3

        // Return Bitmap
        return convolution(gSharp, gSharp2, kernel, sum)
    }

    private fun mean(original: Bitmap): Bitmap? {
        val kernel = arrayOf(
            arrayOf(1, 1, 1),
            arrayOf(1, 1, 1),
            arrayOf(1, 1, 1)
        )


        // Create mutable Bitmap to invert, argument true makes it mutable
        val gMean = original.copy(Bitmap.Config.ARGB_8888, true)
        val gMean2 = original.copy(Bitmap.Config.ARGB_8888, true)

        var sum = 0

        for (y in 0 until 3)
            for (x in 0 until 3)
                sum += kernel[x][y]

        // Return Bitmap
        return convolution(gMean, gMean2, kernel, sum)
    }

    private fun embossing(original: Bitmap): Bitmap? {
        val kernel = arrayOf(
            arrayOf(-1, 0, -1),
            arrayOf(0, 4, 0),
            arrayOf(-1, 0, -1)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gEmb = original.copy(Bitmap.Config.ARGB_8888, true)
        val gEmb2 = original.copy(Bitmap.Config.ARGB_8888, true)

        val sum = 1

        // Return Bitmap
        return convolution(gEmb, gEmb2, kernel, sum)
    }

    private fun edge(original: Bitmap): Bitmap? {
        val kernel = arrayOf(
            arrayOf(1, 1, 1),
            arrayOf(0, 0, 0),
            arrayOf(-1, -1, -1)
        )

        // Create mutable Bitmap to invert, argument true makes it mutable
        val gEdg = original.copy(Bitmap.Config.ARGB_8888, true)
        val gEdg2 = original.copy(Bitmap.Config.ARGB_8888, true)

        val sum = 1

        // Return Bitmap
        return convolution(gEdg, gEdg2, kernel, sum)
    }


    private fun zoom(original: Bitmap): Bitmap? {
        // Create mutable Bitmap to zoom in, argument true makes it mutable
        val zoomed = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = original.width / 2
        val height = original.height / 2

        zoomed.reconfigure(width, height, Bitmap.Config.ARGB_8888)
        return zoomed.copy(Bitmap.Config.ARGB_8888, true)
    }


    private fun loose(original: Bitmap): Bitmap? {
        // Create mutable Bitmap to invert, argument true makes it mutable
        val pix = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = pix.width
        val height = pix.height

        val tamPix = 80
        var contPix = 0
        var r = 0
        var g = 0
        var b = 0

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (contPix == tamPix) {
                    r = red(pix.getPixel(x, y))
                    g = green(pix.getPixel(x, y))
                    b = blue(pix.getPixel(x, y))
                    contPix = 0
                }
                pix.setPixel(x, y, rgb(r, g, b))
                contPix++
            }
        }

        // Return inverted Bitmap
        return pix
    }

    private fun vice(original: Bitmap): Bitmap? {

        val width = original.width
        val height = original.height
        val bmOut = Bitmap.createBitmap(width, height, original.config)
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var pixel: Int

        for (x in 0 until width)
        {
            for (y in 0 until height)
            {
                // get pixel color
                pixel = original.getPixel(x, y)
                // apply filtering on each channel R, G, B
                a = alpha(pixel)
                r = (red(pixel) * 5)
                g = (green(pixel) * 2)
                b = (blue(pixel) * 5)
                // set new color pixel to output bitmap
                bmOut.setPixel(x, y, argb(a, r, g, b))
            }
        }

        return bmOut
    }

    private fun sepia(src: Bitmap): Bitmap? {
        // image size
        val width = src.width
        val height = src.height

        // create output bitmap
        val bmOut = Bitmap.createBitmap(width, height, src.config)

        // constant grayscale
        val gsr = 0.3
        val gsg = 0.59
        val gsb = 0.11

        // color information
        var a: Int
        var r: Int
        var g: Int
        var b: Int
        var pixel: Int

        // scan through all pixels
        for (x in 0 until width) {
            for (y in 0 until height) {
                // get pixel color
                pixel = src.getPixel(x, y)

                // get color on each channel
                a = alpha(pixel)
                r = red(pixel)
                g = green(pixel)
                b = blue(pixel)

                // apply grayscale sample
                r = (gsr * r + gsg * g + gsb * b).toInt()
                g = r
                b = g

                // apply intensity level for sepia-toning on each channel
                r += 110
                if (r > 255) {
                    r = 255
                }
                g += 65
                if (g > 255) {
                    g = 255
                }
                b += 20
                if (b > 255) {
                    b = 255
                }

                // set new pixel color to output image
                bmOut.setPixel(x, y, argb(a, r, g, b))
            }
        }

        return bmOut
    }

    private fun rotate(source: Bitmap): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(90F)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun pixels(original: Bitmap): Bitmap? {

        // Create mutable Bitmap to invert, argument true makes it mutable
        val pix = original.copy(Bitmap.Config.ARGB_8888, true)

        // Get info about Bitmap
        val width = pix.width
        val height = pix.height

        val tamPix = 300
        var contPix = 0
        var r = 0
        var g = 0
        var b = 0

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (contPix == tamPix || x == 0) {
                    r = red(pix.getPixel(x, y))
                    g = green(pix.getPixel(x, y))
                    b = blue(pix.getPixel(x, y))
                    contPix = 0
                }
                pix.setPixel(x, y, rgb(r, g, b))
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