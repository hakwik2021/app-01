package com.ae.app1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ae.app1.Constants.Companion.TAG_ACTIVITY_SIGN_IN
import com.ae.app1.Helper.Companion.openActivity
import com.ae.app1.Helper.Companion.openActivityClearTask
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    // -------- Methods - Overridden  --------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        Helper.log(TAG_ACTIVITY_SIGN_IN, "onCreate")
        Helper.showToast(this, "onCreate")

        mAuth = FirebaseAuth.getInstance()

        btnSignIn.setOnClickListener {
            signInUser()
        }

        btnSignUp.setOnClickListener {
            openActivity(this, SignUpActivity::class.java)
        }

    }

    // -------- Methods - User Defined --------

    private fun isInputValid(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            etEmail.error = "email required"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "password required"
            return false
        }
        return true
    }

    private fun signInUser() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        if (isInputValid(email, password)) {

            mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Helper.log(TAG_ACTIVITY_SIGN_IN, "SignIn successful")
                    Helper.showToast(this, "SignIn successful")
                    // navigate to a different activity
                    openActivityClearTask(this, MainActivity::class.java)
                }.addOnFailureListener {
                    Helper.log(TAG_ACTIVITY_SIGN_IN, "SignIn failed: ${it.message}")
                    Helper.showToast(this, "SignIn failed: ${it.message}")
                }
        }
    }
}