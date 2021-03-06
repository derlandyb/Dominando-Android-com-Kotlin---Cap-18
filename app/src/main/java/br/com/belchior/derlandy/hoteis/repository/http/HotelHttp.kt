package br.com.belchior.derlandy.hoteis.repository.http

import android.util.Log
import br.com.belchior.derlandy.hoteis.auth.Auth
import br.com.belchior.derlandy.hoteis.model.Hotel
import br.com.belchior.derlandy.hoteis.repository.HotelRepository
import br.com.belchior.derlandy.hoteis.repository.imagefiles.FindHotelPicture
import okhttp3.MultipartBody
import okhttp3.RequestBody


class HotelHttp (private val service: HotelHttpApi,
                 private val repository: HotelRepository,
                 private val pictureFinder: FindHotelPicture,
                 private val auth: Auth) {

    private val currentUser: String
        get() = auth.getUserId() ?: ""

    fun synchronizeWithServer() {
        if(currentUser.isBlank()) {
            throw SecurityException("Usuráio não autenticado")
        } else {
            sendPendingData()
            updateLocal()
        }
    }

    private fun sendPendingData() {
        val pendingHotels = repository.pending()
        pendingHotels.forEach { hotel ->
            when(hotel.status) {
                Status.INSERT -> {
                    val result = service.insert(currentUser, hotel).execute()
                    if(result.isSuccessful) {
                        hotel.serverId = result.body()?.id ?: 0
                        hotel.status = Status.OK
                        uploadHotelPhoto(hotel)
                        repository.update(hotel)
                    }
                }
                Status.DELETE -> {
                    val serverId = hotel.serverId ?: 0L
                    if (serverId != 0L) {
                        val result = service.delete(currentUser, serverId).execute()
                        if (result.isSuccessful) {
                            repository.remove(hotel)
                        }
                    } else {
                        repository.remove(hotel)
                    }
                }
                Status.UPDATE -> {
                    val result = if (hotel.serverId == 0L) {
                        service.insert(currentUser, hotel).execute()
                    } else {
                        service.update(currentUser, hotel.serverId ?: 0, hotel).execute()
                    }

                    if (result.isSuccessful) {
                        hotel.serverId = result.body()?.id ?: 0
                        hotel.status = Status.OK
                        uploadHotelPhoto(hotel)
                        repository.update(hotel)
                    }
                }
            }
        }
    }

    private fun uploadHotelPhoto(hotel: Hotel) {
        if(hotel.photoUrl.isNotEmpty() && hotel.photoUrl.startsWith("content://")) {
            val execution = uploadFile(hotel)
            when (execution) {
                is UploadResult -> {
                    Log.d("NGVL", "Upload efetuado com sucesso")
                }
                is NoUploadPerformed -> {
                    Log.e("NGVL", "Erro ao efetuar upload")
                }
            }
        }
    }

    private fun uploadFile(hotel: Hotel): UploadExecution? {
        return try {
            val (sourceFile, mediaType) = pictureFinder.pictureFile(hotel)
            val toUpload = RequestBody.create(mediaType, sourceFile)
            val body = MultipartBody.Part.createFormData("hotel_photo", sourceFile.name, toUpload)
            val description = RequestBody.create(MultipartBody.FORM, hotel.serverId.toString())
            val response = service.uploadPHoto(description, body).execute()
            if(response.isSuccessful) hotel.photoUrl = "${BASE_URL.replace("hotels", "")}${response.body()?.photoUrl}"
            response.body()
                ?.let { it }
                ?: throw Throwable("Error at upload")
        } catch (error: Throwable) {
            NoUploadPerformed
        }
    }

    private fun updateLocal() {
        val response = service.listHotels(currentUser).execute()
        if (response.isSuccessful) {
            val hotelsInServer = response.body()
                ?.map { hotel ->
                    hotel.apply {
                        val id = hotel.id
                        hotel.serverId = id
                        hotel.id = 0
                    }
                }
            hotelsInServer?.forEach { hotel ->
                hotel.status = Status.OK
                val localHotel = repository.hotelByServerId(hotel.serverId ?: 0)

                if (localHotel == null) {
                    repository.insert(hotel)
                } else {
                    hotel.id = localHotel.id
                    repository.update(hotel)
                }
            }
        }
    }

    companion object {
        const val BASE_URL = "http://192.168.15.4/"
    }
}