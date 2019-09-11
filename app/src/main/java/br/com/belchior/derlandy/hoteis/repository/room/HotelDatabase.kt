package br.com.belchior.derlandy.hoteis.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import br.com.belchior.derlandy.hoteis.model.Hotel
import br.com.belchior.derlandy.hoteis.repository.sqlite.DATABASE_NAME
import br.com.belchior.derlandy.hoteis.repository.sqlite.DATABASE_VERSION

@Database(entities = [Hotel::class], version = DATABASE_VERSION, exportSchema = false)
abstract class HotelDatabase : RoomDatabase() {

    abstract fun hotelDao(): HotelDao

    companion object {
        private var instance: HotelDatabase? = null

        fun getDatabase(context: Context): HotelDatabase {

            if (instance ==  null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    HotelDatabase::class.java,
                    DATABASE_NAME
                )
                .allowMainThreadQueries()
                .build()
            }

            return instance as HotelDatabase

        }

        fun destroyInstance() {
            instance = null
        }
    }
}