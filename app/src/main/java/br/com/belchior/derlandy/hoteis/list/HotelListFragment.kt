package br.com.belchior.derlandy.hoteis.list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.app.JobIntentService
import androidx.fragment.app.ListFragment
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.WorkInfo
import br.com.belchior.derlandy.hoteis.R
import br.com.belchior.derlandy.hoteis.model.Hotel
import br.com.belchior.derlandy.hoteis.repository.http.HotelIntentService
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_list_hotel.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HotelListFragment : ListFragment(),
    HotelListView,
    ActionMode.Callback,
    AdapterView.OnItemLongClickListener,
    SwipeRefreshLayout.OnRefreshListener{

    private val viewModel: HotelListViewModel by sharedViewModel()

    private var actionMode: ActionMode? = null

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        startSync()
//    }

//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        LocalBroadcastManager.getInstance(requireContext())
//            .registerReceiver(serviceReceiver, IntentFilter(HotelIntentService.ACTION_SYNC))
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        LocalBroadcastManager.getInstance(requireContext())
//            .unregisterReceiver(serviceReceiver)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_hotel, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh.setOnRefreshListener(this)
        swipeRefresh.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorAccent,
            R.color.colorPrimaryDark
        )
    }

    override fun onRefresh() {
        startSync()
    }

    private fun startSync() {
//        JobIntentService.enqueueWork(requireContext(),
//            HotelIntentService::class.java, 0,
//            Intent(requireContext(), HotelIntentService::class.java)
//        )
        viewModel.syncStatus?.removeObserver(syncObserver)
        viewModel.startSync(requireContext())?.observe(viewLifecycleOwner, syncObserver)
    }

    private var syncObserver = Observer<WorkInfo> {
        swipeRefresh.isRefreshing = it.state == WorkInfo.State.RUNNING || it.state == WorkInfo.State.ENQUEUED
    }

//    private val serviceReceiver : BroadcastReceiver by lazy {
//        object : BroadcastReceiver() {
//            override fun onReceive(context: Context, intent: Intent) {
//                swipeRefresh.isRefreshing = false
//                if(!intent.getBooleanExtra(HotelIntentService.EXTRA_SUCCESS, false)) {
//                    Toast.makeText(activity, R.string.error_sync, Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        }
//    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(savedInstanceState == null) {
            startSync()
        } else {
            viewModel.syncStatus?.observe(viewLifecycleOwner, syncObserver)
        }
        listView.onItemLongClickListener = this
        viewModel.showDetailsCommand().observe(viewLifecycleOwner, Observer { hotel ->
            if (hotel != null) {
                showHotelDetails(hotel)
            }
        })

        viewModel.isInDeleteMode().observe(viewLifecycleOwner, Observer {deleteMode ->

            if(deleteMode == true) {
                showDeleteMode()
            } else {
                hideDeleteMode()
            }
        })

        viewModel.selectedHotels().observe(viewLifecycleOwner, Observer { hotels ->
            if(hotels != null) {
                showSelectedHotels(hotels)
            }
        })

        viewModel.selectionCount().observe(viewLifecycleOwner, Observer { count ->
            if(count != null) {
                updateSelectionCountText(count)
            }
        })

        viewModel.showDeletedMessage().observe(viewLifecycleOwner, Observer { count ->
            if(count != null && count > 0) {
                showMessageHotelsDeleted(count)
            }
        })

        viewModel.getHotels()?.observe(viewLifecycleOwner, Observer { hotels ->
            if(hotels != null) {
                showHotels(hotels)
            }
        })

        if(viewModel.getHotels()?.value ==  null) {
            search()
        }
    }

    override fun showHotels(hotels: List<Hotel>) {
        val adapter = HotelAdapter(requireContext(), hotels)
        listAdapter = adapter
    }

    override fun onItemLongClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long): Boolean {
        val consumed = (actionMode == null)

        if(consumed) {
            val hotel = parent?.getItemAtPosition(position) as Hotel
            viewModel.setInDeleteMode(true)
            viewModel.selectHotel(hotel)
        }

        return consumed
    }

    override fun showHotelDetails(hotel: Hotel) {
        if(activity is OnHotelClickListener) {
            val listener = activity as OnHotelClickListener
            listener.onHotelClick(hotel)
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val hotel = l.getItemAtPosition(position) as Hotel
        viewModel.selectHotel(hotel)
    }

    fun search(text: String = "") {
        viewModel.search(text)
    }

    override fun showDeleteMode() {
        val appCompatActivity = (activity as AppCompatActivity)
        actionMode = appCompatActivity.startSupportActionMode(this)
        listView.onItemLongClickListener = null
        listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
    }

    override fun hideDeleteMode() {
        listView.onItemLongClickListener = this
        for(i in 0 until listView.count) {
            listView.setItemChecked(i, false)
        }

        listView.post{
            actionMode?.finish()
            listView.choiceMode = ListView.CHOICE_MODE_NONE
        }
    }

    override fun showSelectedHotels(hotels: List<Hotel>) {
        listView.post {
            for(i in 0 until listView.count) {
                val hotel = listView.getItemAtPosition(i) as Hotel
                if(hotels.find{ it.id == hotel.id } != null) {
                    listView.setItemChecked(i, true)
                }
            }
        }
    }

    override fun updateSelectionCountText(count: Int) {
        view?.post {
            actionMode?.title = resources.getQuantityString(R.plurals.list_hotel_selected, count, count)
        }
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_delete) {
            viewModel.deleteSelected()
            return true
        }

        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        activity?.menuInflater?.inflate(R.menu.hotel_delete_list, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean  = false

    override fun onDestroyActionMode(mode: ActionMode?) {
        actionMode = null
        viewModel.setInDeleteMode(false)
    }

    override fun showMessageHotelsDeleted(count: Int) {
        Snackbar.make(listView, getString(R.string.message_hotels_deleted, count),
        Snackbar.LENGTH_LONG)
        .setAction(R.string.undo) {
            viewModel.undoDelete()
        }
        .show()
    }

    interface OnHotelClickListener{
        fun onHotelClick(hotel: Hotel)
    }
}