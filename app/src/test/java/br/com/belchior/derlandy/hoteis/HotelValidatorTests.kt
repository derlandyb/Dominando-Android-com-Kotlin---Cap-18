package br.com.belchior.derlandy.hoteis

import br.com.belchior.derlandy.hoteis.form.HotelValidator
import br.com.belchior.derlandy.hoteis.model.Hotel
import junit.framework.Assert.assertTrue
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class HotelValidatorTests {

    private val validator by lazy {
        HotelValidator()
    }

    private val validInfo = Hotel(
        name = "Ritz Lagoa da Anta",
        address = "Av. Brigadeiro Eduardo Gomes de Brito, 546, Maceió/AL",
        rating = 4.9f
    )

    @Test fun should_validate_info_for_a_valid_hotel() {
        assertTrue(validator.validate(validInfo))

        assertThat(validator.validate(validInfo)).isTrue()
    }

    @Test fun should_not_validate_info_whitout_a_hotel_name() {
        val missingName = validInfo.copy(name = "")
        assertThat(validator.validate(missingName)).isFalse()
    }

    @Test fun should_not_validate_info_whitout_a_hotel_address() {
        val missingAddress = validInfo.copy(address = "")
        assertThat(validator.validate(missingAddress)).isFalse()
    }

    @Test fun should_not_validate_info_when_name_is_outside_assepted_size() {
        val nameTooShort = validInfo.copy(name = "I")
        assertThat(validator.validate(nameTooShort)).isFalse()
    }

    @Test fun should_not_validate_info_when_address_is_outside_accepted_size() {
        val bigAddress = "v. Brigadeiro Eduardo Gomes de Brito, 546 - " +
        "Lagora da Anta, Maceió - AL, 57038-230, Brazil"

        val addressTooLong = validInfo.copy(address = bigAddress)
        assertThat(validator.validate(addressTooLong)).isFalse()
    }
}