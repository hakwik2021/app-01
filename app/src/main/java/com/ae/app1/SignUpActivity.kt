package com.ae.app1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ae.app1.Constants.Companion.TAG_ACTIVITY_SIGN_UP
import com.ae.app1.Helper.Companion.openActivityClearTask
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null

    // -------- Methods - Overridden  --------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        Helper.log(TAG_ACTIVITY_SIGN_UP,"onCreate")

        mAuth = FirebaseAuth.getInstance()
        btnSignUp.setOnClickListener {
            Helper.log(TAG_ACTIVITY_SIGN_UP,"btnSignUp")
            signUpUser()
        }
        btnSignIn.setOnClickListener {
            finish()
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

    private fun signUpUser() {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        Helper.log(TAG_ACTIVITY_SIGN_UP,"email= $email , password= $password")

        if (isInputValid(email, password)) {

            mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    Helper.log(TAG_ACTIVITY_SIGN_UP, "SignUp successful")
                    Helper.showToast(this, "SignUp successful")
                    openActivityClearTask(this, MainActivity::class.java)
                }.addOnFailureListener {
                    Helper.log(TAG_ACTIVITY_SIGN_UP, "SignUp failed: ${it.message}")
                    Helper.showToast(this,"SignUp failed: ${it.message}")
                }
        }
    }

}
