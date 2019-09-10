package br.com.belchior.derlandy.hoteis.common

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import br.com.belchior.derlandy.hoteis.R
import br.com.belchior.derlandy.hoteis.details.HotelDetailsActivity
import br.com.belchior.derlandy.hoteis.details.HotelDetailsFragment
import br.com.belchior.derlandy.hoteis.form.HotelFormFragment
import br.com.belchior.derlandy.hoteis.list.HotelListFragment
import br.com.belchior.derlandy.hoteis.list.HotelListViewModel
import br.com.belchior.derlandy.hoteis.login.UserProfileFragment
import br.com.belchior.derlandy.hoteis.model.Hotel
import kotlinx.android.synthetic.main.activity_hotel.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HotelActivity : BaseActivity(),
    HotelListFragment.OnHotelClickListener,
    SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

    companion object {
        const val EXTRA_SEARCH_TERM = "lastSearchTerm"
        const val EXTRA_HOTEL_ID_SELECTED = "hotelIdSelected"
    }


    private var searchView:SearchView? = null
    private val viewModel: HotelListViewModel by viewModel()

    private val listFragment : HotelListFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragmentList) as HotelListFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel)
        fabAdd.setOnClickListener {
            listFragment.hideDeleteMode()
            HotelFormFragment.newInstance().open(supportFragmentManager)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.hotel, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        searchItem?.setOnActionExpandListener(this)
        searchView = searchItem?.actionView as SearchView
        searchView?.queryHint = getString(R.string.hint_search)
        searchView?.setOnQueryTextListener(this)

        if(viewModel.getSearchTerm()?.value?.isNotEmpty() == true) {
            Handler().post{
                val query = viewModel.getSearchTerm()?.value
                searchItem.expandActionView()
                searchView?.setQuery(query, true)
                searchView?.clearFocus()
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_info -> AboutDialogFragment().show(supportFragmentManager, "sobre")
            R.id.action_user_info -> UserProfileFragment().open(supportFragmentManager)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onHotelClick(hotel: Hotel) {
        if(isTablet()) {
            viewModel.hotelIdSelected = hotel.id
            showDetailsFragment(hotel.id)
        } else {
            showHotelDetailsActivity(hotel.id)
        }
    }

    override fun onQueryTextSubmit(query: String?) = true

    override fun onQueryTextChange(newText: String?): Boolean {
        listFragment.search(newText ?: "")
        return true
    }

    override fun onMenuItemActionExpand(menu: MenuItem?) = true

    override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {

        listFragment.search()
        return true
    }

    private fun showHotelDetailsActivity(hotelId: Long) {
        HotelDetailsActivity.open(this, hotelId)
    }

    private fun showDetailsFragment(hotelId: Long) {
        val fragment = HotelDetailsFragment.newInstance(hotelId)
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.details, fragment,
                HotelDetailsFragment.TAG_DETAILS
            )
            .commit()
    }

    private fun isTablet() = resources.getBoolean(R.bool.tablet)
}