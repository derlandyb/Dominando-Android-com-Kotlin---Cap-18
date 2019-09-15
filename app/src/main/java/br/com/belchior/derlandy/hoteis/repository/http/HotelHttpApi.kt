package br.com.belchior.derlandy.hoteis.repository.http

import br.com.belchior.derlandy.hoteis.model.Hotel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface HotelHttpApi {

    companion object {
        const val WEB_SERVICE = "hotels"
    }

    @GET("$WEB_SERVICE/{user}")
    fun listHotels(@Path("user") user: String): Call<List<Hotel>>

    @GET("$WEB_SERVICE/{user}/{hotelId}")
    fun hotelById(@Path("user") user: String, @Path("hotelId") hotelId: Long): Call<Hotel?>

    @POST("$WEB_SERVICE/{user}")
    fun insert(@Path("user") user: String, @Body hotel: Hotel): Call<IdResult>

    @PUT("$WEB_SERVICE/{user}/{hotelId}")
    fun update(@Path("user") user: String, @Path("hotelId") hotelId: Long, @Body hotel: Hotel): Call<IdResult>

    @DELETE("$WEB_SERVICE/{user}/{hotelId}")
    fun delete(@Path("user") user: String, @Path("hotelId") hotelId: Long): Call<IdResult>

    @Multipart
    @POST("$WEB_SERVICE/upload")
    fun uploadPHoto(
        @Part("id") hotelId: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<UploadResult>
}

