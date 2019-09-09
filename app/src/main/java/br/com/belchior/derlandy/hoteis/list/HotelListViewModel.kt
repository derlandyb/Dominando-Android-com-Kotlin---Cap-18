package br.com.belchior.derlandy.hoteis.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.work.WorkInfo
import br.com.belchior.derlandy.hoteis.common.SingleLiveEvent
import br.com.belchior.derlandy.hoteis.model.Hotel
import br.com.belchior.derlandy.hoteis.repository.HotelRepository
import br.com.belchior.derlandy.hoteis.repository.http.HotelSyncWorker
import br.com.belchior.derlandy.hoteis.repository.http.Status

class HotelListViewModel (private val repository: HotelRepository): ViewModel(){

    var hotelIdSelected: Long = -1

    private val searchTerm = MutableLiveData<String>()
    private val hotels = Transformations.switchMap(searchTerm) {term ->
        repository.search("%$term%")
    }

    private val inDeleteMode = MutableLiveData<Boolean>().apply {
        value = false
    }

    private val selectedItems = mutableListOf<Hotel>()
    private val selectionCount = MutableLiveData<Int>()
    private val selectedHotels = MutableLiveData<List<Hotel>>().apply {
        value = selectedItems
    }
    private val deletedItens = mutableListOf<Hotel>()
    private val showDeletedMessage = SingleLiveEvent<Int>()
    private val showDetailsCommand = SingleLiveEvent<Hotel>()

    var syncStatus: LiveData<WorkInfo>? = null

    fun getSearchTerm(): LiveData<String>? = searchTerm
    fun getHotels(): LiveData<List<Hotel>>? = hotels
    fun selectionCount(): LiveData<Int> = selectionCount
    fun selectedHotels(): LiveData<List<Hotel>> = selectedHotels
    fun showDeletedMessage(): LiveData<Int> = showDeletedMessage
    fun showDetailsCommand(): LiveData<Hotel> = showDetailsCommand
    fun isInDeleteMode(): LiveData<Boolean> = inDeleteMode

    fun startSync(context: Context): LiveData<WorkInfo>?{
        syncStatus = HotelSyncWorker.start(context)

        return syncStatus
    }

    fun selectHotel(hotel: Hotel) {
        if(inDeleteMode.value == true) {
            toogleHotelSelected(hotel)
            if(selectedItems.size == 0) {
                inDeleteMode.value = false
            } else {
                selectionCount.value = selectedItems.size
                selectedHotels.value = selectedItems
            }

        } else {
            showDetailsCommand.value = hotel
        }
    }

    private fun toogleHotelSelected(hotel: Hotel) {
        val existing = selectedItems.find { it.id == hotel.id }
        if (existing == null) {
            selectedItems.add(hotel)
        } else {
            selectedItems.removeAll{ it.id == hotel.id }
        }
    }

    fun search(term: String = "") {
        searchTerm.value = term
    }

    fun setInDeleteMode(deleteMode: Boolean) {
        if(!deleteMode) {
            selectionCount.value = 0
            selectedItems.clear()
            selectedHotels.value = selectedItems
            showDeletedMessage.value = selectedItems.size
        }

        inDeleteMode.value = deleteMode
    }

    fun deleteSelected() {
        selectedItems.forEach{ hotel ->
            hotel.status = Status.DELETE
            repository.update(hotel)
        }
        deletedItens.clear()
        deletedItens.addAll(selectedItems)
        setInDeleteMode(false)
        showDeletedMessage.value = deletedItens.size
    }

    fun undoDelete() {
        if(deletedItens.isNotEmpty()) {
            for (hotel in deletedItens) {
                hotel.id = 0L
                repository.save(hotel)
            }
        }
    }
}