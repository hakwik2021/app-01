package com.ae.app1

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

class Helper {
    companion object {

        /**
         * logcat: show log output in logcat window
         */
        fun log(tag: String, message: String) {
            Log.d(tag, message)
        }

        /**
         * showToast: show a toast message
         */
        fun showToast(ctx: Context, message: String) {
            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
        }

        fun openActivity(ctx: Context, cls: Class<*>) {
            val intent = Intent(ctx, cls)
            startActivity(ctx, intent, null)
        }

        fun openActivityClearTask(ctx: Context, cls: Class<*>) {
            val intent = Intent(ctx, cls)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(ctx, intent, null)
        }
    }
}