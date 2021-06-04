package com.ae.app1

import android.os.Bundle
import android.view.View.INVISIBLE
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_qr.*

class CreateQrActivity : AppCompatActivity() {

    private var mItem: Item? = null
    private lateinit var mItemId: String

    // -------- Methods - Overridden  --------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_qr)
        Helper.log(Constants.TAG_ACTIVITY_CREATE_QR, "onCreate")
        Helper.showToast(this, "onCreate")

        try {
            mItem = intent.getSerializableExtra(Constants.ITEM) as Item?
        } catch (e: Exception) {
            Helper.log(Constants.TAG_ACTIVITY_CREATE_QR, "${e.message}")
        }

        if (mItem != null) {

            mItemId = mItem!!.id
            etName.setText(mItem!!.name)
            etPostal.setText(mItem!!.postal)
            etPhone.setText(mItem!!.phone)
            etTemperature.setText(mItem!!.temperature)
            btnCreateQrCode.visibility = INVISIBLE

            Picasso.with(this).load(mItem!!.url)
                .placeholder(R.drawable.ic_android)
                .fit()
                .centerInside()
                .into(ivOutput)
        }

        btnCreateQrCode.setOnClickListener {
            val name = etName.text.toString()
            val postal = etPostal.text.toString()
            val phone = etPhone.text.toString()
            val temperature = etTemperature.text.toString()
            val url = ""
            val item = Item(name, postal, phone, temperature, url)
            generateQrCodeUsingObject(item)
        }
    }

    // -------- Methods - User Defined --------

    private fun generateQrCodeUsingObject(item: Item) {

        val qrgEncoder = QRGEncoder(
            "${item.name};${item.postal};${item.phone};${item.temperature};",
            null,
            QRGContents.Type.TEXT,
            500
        )
        try {
            val qrBitmap = qrgEncoder.bitmap
            ivOutput.setImageBitmap(qrBitmap)
        } catch (e: Exception) {
            Helper.log(Constants.TAG_ACTIVITY_CREATE_QR, "error: $e")
        }
    }
}