package com.ae.app1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ae.app1.Constants.Companion.TAG_ACTIVITY_FIRST
import kotlinx.android.synthetic.main.activity_first.*

/**
 * android project      : app1
 * android project id   : com.ae.app1
 * fb project           : fb-app1
 *
 * description          :
 *
 * firebase services    :
 *      1. Authentication
 *          - Click "Get started"
 *          - Enable Email/Password
 *      2. Cloud Firestore (CRUD functions)
 *          - Click "Create database"
 *          - Click "Start in production mode", Next
 *          - Select "asia-east2", Enable, Select Rules: Add "allow read, write: if request.auth.uid != null;", Click "Publish"
 *      3. Firebase Storage
 *          - Select "Rules", Add "allow read, write: if request.auth.uid != null;", Click "Publish"
 */
class FirstActivity : AppCompatActivity() {

    // -------- Methods - Overridden  --------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)

        Helper.log(TAG_ACTIVITY_FIRST,"onCreate")
        Helper.showToast(this, "onCreate")

        btnCustomer.setOnClickListener {
            Helper.openActivity(this, CreateQrActivity::class.java)
        }

        btnEstablishment.setOnClickListener {
            Helper.openActivity(this, SignInActivity::class.java)
        }
    }

    // -------- Methods - User Defined --------
}