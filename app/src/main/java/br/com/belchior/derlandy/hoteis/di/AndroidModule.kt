package br.com.belchior.derlandy.hoteis.di

import android.content.Context
import br.com.belchior.derlandy.hoteis.auth.Auth
import br.com.belchior.derlandy.hoteis.auth.AuthManager
import br.com.belchior.derlandy.hoteis.details.HotelDetailsViewModel
import br.com.belchior.derlandy.hoteis.form.HotelFormViewModel
import br.com.belchior.derlandy.hoteis.list.HotelListViewModel
import br.com.belchior.derlandy.hoteis.repository.HotelRepository
import br.com.belchior.derlandy.hoteis.repository.http.HotelHttp
import br.com.belchior.derlandy.hoteis.repository.http.HotelHttpApi
import br.com.belchior.derlandy.hoteis.repository.imagefiles.FindHotelPicture
import br.com.belchior.derlandy.hoteis.repository.imagefiles.ImageGalleryPictureFinder
import br.com.belchior.derlandy.hoteis.repository.room.HotelDatabase
import br.com.belchior.derlandy.hoteis.repository.room.RoomRepository
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val androidModule = module {
    single { this }
    single {
        RoomRepository(HotelDatabase.getDatabase(context = get())) as HotelRepository
    }

    single {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(logging)
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(HotelHttp.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient.build())
            .build()

        retrofit.create<HotelHttpApi>(HotelHttpApi::class.java)
    }

    factory {
        val context = get() as Context
        val resolver = context.contentResolver
        val uploadDir = context.externalCacheDir ?: context.cacheDir
        ImageGalleryPictureFinder(uploadDir, resolver) as FindHotelPicture
    }

    factory {
        HotelHttp(
            service = get(),
            repository = get(),
            pictureFinder = get(),
            auth = get()
        )
    }

    viewModel{
        HotelListViewModel(repository = get())
    }

    viewModel {
        HotelDetailsViewModel(repository = get())
    }

    viewModel {
        HotelFormViewModel(repository = get())
    }

    single {
        val manager: AuthManager = get()
        manager as Auth
    }

    single {
        AuthManager(context = get())
    }
}