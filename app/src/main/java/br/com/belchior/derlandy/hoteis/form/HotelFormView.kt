package br.com.belchior.derlandy.hoteis.form

import br.com.belchior.derlandy.hoteis.model.Hotel

interface HotelFormView {
    fun showHotel(hotel: Hotel)
    fun errorInvalidHotel()
    fun errorSaveHotel()
}