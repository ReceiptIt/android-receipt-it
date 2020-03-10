package com.receiptit.receiptList

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import com.receiptit.R
import kotlinx.android.synthetic.main.activity_stitched_review.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class StitchedReviewActivity : AppCompatActivity() {
    private var ACTIVITY_RESULT_STITCH = 5
    private var ACTIVITY_RESULT_STITCH_REVIEW = 6

    private lateinit var result_picture: String
    private lateinit var stitchedImage: String
    private lateinit var picture: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stitched_review)
        result_picture = intent.extras?.get("picture") as String
        picture = intent.extras?.get("original_picture") as String
        stitchedImage = result_picture

        val imageView = findViewById<ImageView>(R.id.iv_stitched_img)

        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(result_picture, bmOptions)

        imageView.setImageBitmap(bitmap)

        btn_send.setOnClickListener {
            val intent = Intent()
            intent.putExtra("return_picture", stitchedImage)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        btn_dismiss.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }

        btn_re_stitch.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
                .setDataAndType(FileProvider.getUriForFile(this, applicationContext.packageName, File(picture)),"image/*")
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, ACTIVITY_RESULT_STITCH)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_RESULT_STITCH && resultCode == Activity.RESULT_OK && data != null) {
            // only singe image is allowed to select
            val images =
                listOf( FileProvider.getUriForFile(this, applicationContext.packageName, File(picture)), data.data!!)
            processImages(images)
        }
    }

    private fun processImages(uris: List<Uri>) {

        val first = MediaStore.Images.Media.getBitmap(this.contentResolver, uris[0])
        val second = MediaStore.Images.Media.getBitmap(this.contentResolver, uris[1])

        val width = Math.max(first.width, second.width)
        val height = first.height + second.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas =  Canvas(result)
        canvas.drawBitmap(first, 0f, 0f, null)
        canvas.drawBitmap(second, 0f, first.height.toFloat(), null)

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val filepath = File.createTempFile(
            "STITCHED_JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )

        try {
            val fos =  FileOutputStream(filepath)
            result.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.close()
            stitchedImage = filepath.absolutePath
            iv_stitched_img.setImageBitmap(null)
            iv_stitched_img.setImageBitmap(result)
        } catch (e: FileNotFoundException) {
            Log.d("gg", "File not found: " + e.message)
        } catch (e: FileNotFoundException) {
            Log.d("gg", "Error accessing file: " + e.message)
        }

    }
}
