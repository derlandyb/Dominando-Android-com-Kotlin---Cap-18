package br.com.belchior.derlandy.hoteis.repository.room

import androidx.lifecycle.LiveData
import br.com.belchior.derlandy.hoteis.model.Hotel
import br.com.belchior.derlandy.hoteis.repository.HotelRepository
import br.com.belchior.derlandy.hoteis.repository.http.Status

class RoomRepository(database: HotelDatabase) : HotelRepository {

    private val hotelDao = database.hotelDao()

    override fun save(hotel: Hotel) {
        if (hotel.id == 0L){
            hotel.status = Status.INSERT
            val id = hotelDao.insert(hotel)
            hotel.id = id
        } else {
            hotel.status = Status.UPDATE
            hotelDao.update(hotel)
        }
    }

    override fun insert(hotel: Hotel): Long {
        return hotelDao.insert(hotel)
    }

    override fun update(hotel: Hotel) {
        hotelDao.update(hotel)
    }

    override fun remove(vararg hotels: Hotel) {
        hotelDao.delete(*hotels)
    }

    override fun hotelById(id: Long): LiveData<Hotel> {
        return hotelDao.hotelById(id)
    }

    override fun search(term: String): LiveData<List<Hotel>> {
        return hotelDao.search(term)
    }

    override fun hotelByServerId(serverId: Long): Hotel? {
        return hotelDao.hotelByServerId(serverId)
    }

    override fun pending(): List<Hotel> {
        return hotelDao.pending()
    }
}