package br.com.belchior.derlandy.hoteis.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import br.com.belchior.derlandy.hoteis.auth.AuthManager
import br.com.belchior.derlandy.hoteis.login.LoginActivity
import org.koin.android.ext.android.inject

abstract class BaseActivity: AppCompatActivity()  {
    val authManager: AuthManager by inject()

    override fun onStart() {
        super.onStart()
        verifyuserLoggedIn()
    }

    fun verifyuserLoggedIn() {
        val account = authManager.getUserAccount()
        if(account ==  null) {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            })
            finish()
        }
    }
}
