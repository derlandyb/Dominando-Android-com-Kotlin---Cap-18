package br.com.belchior.derlandy.hoteis.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.belchior.derlandy.hoteis.model.Hotel
import br.com.belchior.derlandy.hoteis.repository.HotelRepository

class HotelDetailsViewModel(private val repository: HotelRepository) : ViewModel() {

    fun loadHotelDetais(id: Long): LiveData<Hotel> {
        return repository.hotelById(id)
    }
}