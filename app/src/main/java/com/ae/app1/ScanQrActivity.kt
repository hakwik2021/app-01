package com.ae.app1

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import com.ae.app1.Constants.Companion.EXT_PNG
import com.ae.app1.Constants.Companion.TAG_ACTIVITY_SCAN_QR
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_scan_qr.*
import java.io.ByteArrayOutputStream

class ScanQrActivity : AppCompatActivity() {

    private val mDb by lazy { FirebaseFirestore.getInstance() }
    private val mStorage by lazy { FirebaseStorage.getInstance() }
    private val mStorageRef by lazy { mStorage.getReference(Constants.ITEMS) }

    private var mImageUri: Uri? = null
    private lateinit var mQrBitmap: Bitmap

    private var mItem: Item? = null
    private lateinit var mItemId: String

    private lateinit var csvScanner: CodeScanner


    // -------- Methods - Overridden  --------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_qr)
        Helper.log(TAG_ACTIVITY_SCAN_QR, "onCreate")

        if (mItem == null) {
            mItemId = ""
        }

        val scanner = findViewById<CodeScannerView>(R.id.csvScanner)
        csvScanner = CodeScanner(this, scanner)

        scanner.setOnClickListener {
            csvScanner.startPreview()
        }

        csvScanner.setDecodeCallback {
            runOnUiThread {
                tvOutput.text = it.text
                val str = it.text
                mImageUri = generateQrCodeBitmapUri(str)
                saveItem(str)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Helper.log(TAG_ACTIVITY_SCAN_QR, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Helper.log(TAG_ACTIVITY_SCAN_QR, "onResume")
        requestCameraPermission()
        requestWriteExternalStoragePermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        Helper.log(TAG_ACTIVITY_SCAN_QR, "onDestroy")
    }

    // -------- Methods - User Defined --------

    private fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path =
            MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }

    private fun generateQrCodeBitmapUri(str: String): Uri? {
        val list = str.split(";")
        val name = list[0]
        val postal = list[1]
        val phone = list[2]
        val temperature = list[3]
        val url = ""

        val qrgEncoder = QRGEncoder(
            "${name};${postal};${phone};${temperature};${url}",
            null,
            QRGContents.Type.TEXT,
            500
        )
        try {
            mQrBitmap = qrgEncoder.bitmap
            return getImageUriFromBitmap(this, mQrBitmap)
        } catch (e: Exception) {
            Helper.log(TAG_ACTIVITY_SCAN_QR, "error: $e")
        }
        return null
    }

    private fun saveItem(str: String) {
        val itemsRef = mDb.collection(Constants.ITEMS)

        val list = str.split(";")
        val name = list[0]
        val postal = list[1]
        val phone = list[2]
        val temperature = list[3]
        val url = ""

        Helper.log(
            TAG_ACTIVITY_SCAN_QR,
            "str = $name, $postal, $phone, $temperature, $url"
        )

        if (name.trim().isEmpty() || postal.trim().isEmpty()) {
            Helper.showToast(this, "Please insert a name and postal")
            return
        }

        val documentId = if (mItemId.isEmpty()) {
            itemsRef.document().id
        } else {
            mItemId
        }

        //mQrBitmap.
        //mImageUri = Uri.parse("android.resource://${this.packageName}/${R.drawable.qr_code}")

        if (mImageUri != null) {
            Helper.log(TAG_ACTIVITY_SCAN_QR, "mImageUri=$mImageUri")

            // Create the filename
            val imageFilename = "${documentId}${EXT_PNG}"
            Helper.log(TAG_ACTIVITY_SCAN_QR, "imageFilename=$imageFilename")

            // fileRef points to the filename
            val filenameRef = mStorageRef.child(imageFilename)

            // Upload the file
            val uploadTask = filenameRef.putFile(mImageUri!!)
            val urlTask = uploadTask.continueWithTask { task ->
                if (task.isSuccessful) {
                    Helper.log(TAG_ACTIVITY_SCAN_QR, "File added")
                    filenameRef.downloadUrl
                } else {
                    task.exception?.let {
                        Helper.showToast(this, "File not added. Error: ${it.message}")
                        throw it
                    }
                }
            }
            // Get a download URL
            urlTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Save item in Cloud Firestore
                    val item = Item(name, postal, phone, temperature, task.result.toString())
                    item.id = documentId
                    itemsRef.document(documentId).set(item).addOnSuccessListener {
                        Helper.log(TAG_ACTIVITY_SCAN_QR, "Item added")
                        Helper.showToast(this, "Item added")
                    }.addOnFailureListener {
                        Helper.log(TAG_ACTIVITY_SCAN_QR, "Item not added")
                        Helper.showToast(this, "Item not added")
                    }
                } else {
                    // Handle failures
                    Helper.showToast(this, "urlTask fail")
                }
            }
        } else {
            // Save item in Cloud Firestore
            val item = Item(name, postal, phone, temperature, "")
            item.id = documentId
            itemsRef.document(documentId).set(item).addOnSuccessListener {
                Helper.log(TAG_ACTIVITY_SCAN_QR, "Item added")
                Helper.showToast(this, "Item added")
            }.addOnFailureListener {
                Helper.log(TAG_ACTIVITY_SCAN_QR, "Item not added")
                Helper.showToast(this, "Item not added")
            }
        }
        finish()
    }

    private fun requestCameraPermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    Helper.log(TAG_ACTIVITY_SCAN_QR, "onPermissionGranted")
                    //Helper.showToast(this@ScanActivity, "onPermissionGranted")
                    csvScanner.startPreview()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Helper.log(TAG_ACTIVITY_SCAN_QR, "onPermissionDenied")
                    //Helper.showToast(this@ScanActivity, "Camera permission is required")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?, token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }

    private fun requestWriteExternalStoragePermission() {
        Dexter.withContext(this)
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    Helper.log(TAG_ACTIVITY_SCAN_QR, "onPermissionGranted")
                    //Helper.showToast(this@ScanActivity, "onPermissionGranted")
                    //csvScanner.startPreview()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Helper.log(TAG_ACTIVITY_SCAN_QR, "onPermissionDenied")
                    //Helper.showToast(this@ScanActivity, "Camera permission is required")
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?, token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }
            }).check()
    }
}