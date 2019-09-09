package br.com.belchior.derlandy.hoteis.details

import br.com.belchior.derlandy.hoteis.model.Hotel

interface HotelDetailsView {
    fun showHotelDetails(hotel: Hotel)
    fun errorHotelNotFound()
}