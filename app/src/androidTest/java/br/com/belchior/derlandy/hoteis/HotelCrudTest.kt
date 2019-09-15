package br.com.belchior.derlandy.hoteis

import androidx.room.Room
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import br.com.belchior.derlandy.hoteis.common.HotelActivity
import br.com.belchior.derlandy.hoteis.repository.HotelRepository
import br.com.belchior.derlandy.hoteis.repository.room.HotelDatabase
import br.com.belchior.derlandy.hoteis.repository.room.RoomRepository
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasToString
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module.module
import org.koin.standalone.StandAloneContext.loadKoinModules

@RunWith(AndroidJUnit4ClassRunner::class)
class HotelCrudTest {
    @get:Rule
    val activityRule = ActivityTestRule(HotelActivity::class.java)

    init {
        loadKoinModules(module {
         single(override = true) {
             val db = Room.inMemoryDatabaseBuilder(
                 InstrumentationRegistry.getInstrumentation().context,
                 HotelDatabase::class.java
             )
                 .allowMainThreadQueries()
                 .build()

             RoomRepository(db) as HotelRepository
         }
        })
    }

    @Test fun crudTest() {
        add()
        edit()
        delete()
    }

    private fun add() {
        onView(withId(R.id.fabAdd)).perform(click())
        fillHotelForm(NEW_HOTEL_NAME, NEW_HOTEL_ADDRESS)
        listViewHasHotelWithName(NEW_HOTEL_NAME)
    }

    private fun edit() {
        clickOnHotelName(NEW_HOTEL_NAME)
        onView(withId(R.id.action_edit)).perform(click())
        fillHotelForm(CHANGED_HOTEL_NAME, CHANGED_HOTEL_ADDRESS)
        onView(withId(R.id.txtName)).check(matches(withText(CHANGED_HOTEL_NAME)))
        onView(withId(R.id.txtAddress)).check(matches(withText(CHANGED_HOTEL_ADDRESS)))
        pressBack()
        listViewHasHotelWithName(CHANGED_HOTEL_NAME)
    }

    private fun delete() {
        onData(hasToString(containsString(CHANGED_HOTEL_NAME)))
            .inAdapterView(withId(android.R.id.list)).atPosition(0)
            .perform(longClick())

        onView(withId(R.id.action_delete)).perform(click())
    }

    private fun fillHotelForm(name: String, address: String) {
        onView(withId(R.id.edtName)).perform(replaceText(name))
        onView(withId(R.id.edtAddress)).perform(replaceText(address))
        onView(withId(R.id.edtAddress)).perform(pressImeActionButton())
        closeSoftKeyboard()
    }

    private fun listViewHasHotelWithName(name: String) {
        onData(hasToString(containsString(name)))
            .inAdapterView(withId(android.R.id.list))
            .atPosition(0)
            .onChildView(withId(R.id.txtName))
            .check(matches(withText(containsString(name))))
    }

    private fun clickOnHotelName(name: String) {
        onData(hasToString(containsString(name)))
            .inAdapterView(withId(android.R.id.list))
            .atPosition(0)
            .perform(click())
    }

    companion object {
        private const val NEW_HOTEL_NAME = "Hotel de Teste"
        private const val NEW_HOTEL_ADDRESS = "Rua tal"
        private const val CHANGED_HOTEL_NAME = "Hotel Modificado"
        private const val CHANGED_HOTEL_ADDRESS = "Rua modificada"
    }
}