package br.com.belchior.derlandy.hoteis.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import br.com.belchior.derlandy.hoteis.R
import br.com.belchior.derlandy.hoteis.auth.AuthManager
import br.com.belchior.derlandy.hoteis.common.HotelActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity() {

    private val authManager: AuthManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnSignIn.setOnClickListener{ sigIn() }
    }

    private fun sigIn() {
        val signInIntent = authManager.getSignInIntent()
        startActivityForResult(signInIntent, REQUEST_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_SIGN_IN) {
            handleSignInResult(data)
        } else {
            checkGooglePlayServices()
        }
    }

    private fun checkGooglePlayServices() {
        val api = GoogleApiAvailability.getInstance()
        val resultcode = api.isGooglePlayServicesAvailable(this)
        if(resultcode != ConnectionResult.SUCCESS) {
            if(api.isUserResolvableError(resultcode)) {
                val dialog = api.getErrorDialog(this, resultcode, REQUEST_PLAY_SERVICES)
                dialog.setOnCancelListener {
                    finish()
                }
                dialog.show()
            }else{
                Toast.makeText(this, R.string.error_play_services_hot_supported, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSignInResult(intent: Intent?) {
        try {
            GoogleSignIn.getSignedInAccountFromIntent(intent).getResult(ApiException::class.java)
            startActivity(Intent(this, HotelActivity::class.java))
            finish()
        } catch (e: ApiException) {
            Toast.makeText(this, R.string.error_login_failed, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_SIGN_IN = 1000
        private const val REQUEST_PLAY_SERVICES = 2000
    }
}
