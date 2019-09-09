package br.com.belchior.derlandy.hoteis.repository.imagefiles

import br.com.belchior.derlandy.hoteis.model.Hotel

interface FindHotelPicture {
    fun pictureFile(hotel: Hotel): PictureToUpload
}