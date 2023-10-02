package com.returntolife.accessibilityutils

import android.util.Base64
import com.blankj.utilcode.util.SPUtils
import java.lang.Exception
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class AuthUtils {

    init {
        System.loadLibrary("my_auth")
    }

    companion object {
        private const val KEY = "auth_key"


    }

    external fun checkAuthByNative(s: String): Boolean

    fun isAuth(): Boolean {
        return SPUtils.getInstance().getBoolean(KEY)

    }


    fun checkAuth(key: String): Boolean {
        try {
            val result: Boolean = checkAuthByNative(key)
            if (result) {
                SPUtils.getInstance().put(KEY, true)
            }

            return result
        } catch (e: Exception) {

        }

        return false

    }


}