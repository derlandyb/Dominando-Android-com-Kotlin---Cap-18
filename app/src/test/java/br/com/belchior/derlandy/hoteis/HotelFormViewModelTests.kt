package br.com.belchior.derlandy.hoteis

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import br.com.belchior.derlandy.hoteis.form.HotelFormViewModel
import br.com.belchior.derlandy.hoteis.model.Hotel
import br.com.belchior.derlandy.hoteis.repository.HotelRepository
import com.nhaarman.mockitokotlin2.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*


class HotelFormViewModelTests {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel : HotelFormViewModel
    private val mockedRepo = mock<HotelRepository>()
    private val anHotelID = Random().nextLong()
    private val anHotel = Hotel(
        id = anHotelID,
        name = "Ritz Recife",
        address = "Praia de Boa Viagem, SN",
        rating = 5.0f
    )

    @Before fun before_each_test() {
        viewModel = HotelFormViewModel(mockedRepo)
    }

    @Test fun given_an_existing_ID_should_load_the_hotel() {
        //Given
        val liveData = MutableLiveData<Hotel>().apply {
            value = anHotel
        }

        //When
        whenever(mockedRepo.hotelById(any()))
            .thenReturn(liveData)

        //Then
        viewModel.loadHotel(anHotelID).observeForever{hotel ->
            assertThat(hotel).isEqualTo(anHotel)
        }
    }

    @Test fun given_valid_hotel_info_should_save_a_hotel() {
        //Given
        val info = Hotel(
            name = "Ritz Recife",
            address = "Praia de Boa Viagem, Recife/PE",
            rating = 4.8f
        )

        //When
        val saved = viewModel.saveHotel(info)
        whenever(mockedRepo.save(any())).thenAnswer {Unit}

        //Then
        assertThat(saved).isTrue()
        verify(mockedRepo, times(1)).save(any())
        verifyNoMoreInteractions(mockedRepo)
    }

    @Test fun given_invalid_hotel_info_should_falil_at_save_the_hotel() {
        //Given
        val invalidInfo = Hotel(
            name = "Y",
            address = "WWW",
            rating = 4.8f
        )

        //When
        val saved = viewModel.saveHotel(invalidInfo)

        //Then
        assertThat(saved).isFalse()
        verifyZeroInteractions(mockedRepo)
    }
}