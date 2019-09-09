package br.com.belchior.derlandy.hoteis.details

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import br.com.belchior.derlandy.hoteis.R
import br.com.belchior.derlandy.hoteis.common.BaseActivity

class HotelDetailsActivity : BaseActivity() {

    private val hotelId:Long by lazy { intent.getLongExtra(EXTRA_HOTEL_ID, -1) }

    companion object {

        private const val EXTRA_HOTEL_ID = "hotelId"

        fun open(activity: Activity, hotelId: Long) {
            activity.startActivityForResult(Intent(activity, HotelDetailsActivity::class.java).apply {
                putExtra(EXTRA_HOTEL_ID, hotelId)
            }, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_details)

        if(savedInstanceState == null) {
            showHotelDetailsFragment()
        }
    }

    private fun showHotelDetailsFragment() {
        val fragment = HotelDetailsFragment.newInstance(hotelId)
        supportFragmentManager.
            beginTransaction().
            replace(
                R.id.details, fragment,
                HotelDetailsFragment.TAG_DETAILS
            )
            .commit()
    }
}
