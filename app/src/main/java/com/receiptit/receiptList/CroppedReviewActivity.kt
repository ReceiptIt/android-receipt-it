package com.receiptit.receiptList

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.receiptit.R
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.widget.ImageView
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_cropped_review.*
import kotlinx.android.synthetic.main.activity_receipt_list.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CroppedReviewActivity : AppCompatActivity() {

    private var ACTIVITY_RESULT_STITCH = 5
    private var ACTIVITY_RESULT_STITCH_REVIEW = 6

    private lateinit var picture: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cropped_review)
        picture = intent.extras?.get("picture") as String


        val imageView = findViewById<ImageView>(R.id.iv_cropped_img)

        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(picture, bmOptions)

        imageView.setImageBitmap(bitmap)

        btn_send.setOnClickListener {
            setResult(Activity.RESULT_FIRST_USER, Intent())
            finish()
        }

        btn_dismiss.setOnClickListener {
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
        }

        btn_stitch.setOnClickListener {
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
        } else if (requestCode == ACTIVITY_RESULT_STITCH_REVIEW && resultCode == Activity.RESULT_OK && data != null) {
            // only singe image is allowed to select
            val stitchedImage = data.extras?.get("return_picture") as String
            val intent = Intent()
            intent.putExtra("return_picture", stitchedImage)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (requestCode == ACTIVITY_RESULT_STITCH_REVIEW && resultCode == Activity.RESULT_CANCELED && data != null) {
            // only singe image is allowed to select
            setResult(Activity.RESULT_CANCELED, Intent())
            finish()
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
        } catch (e: FileNotFoundException) {
            Log.d("gg", "File not found: " + e.message)
        } catch (e: FileNotFoundException) {
            Log.d("gg", "Error accessing file: " + e.message)
        }

        val intent = Intent(this, StitchedReviewActivity::class.java)
        intent.putExtra("picture", filepath.absolutePath)
        intent.putExtra("original_picture", picture)
        startActivityForResult(intent, ACTIVITY_RESULT_STITCH_REVIEW)
    }
}
