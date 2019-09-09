package br.com.belchior.derlandy.hoteis.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.belchior.derlandy.hoteis.repository.http.Status
import br.com.belchior.derlandy.hoteis.repository.sqlite.COLUMN_ID
import br.com.belchior.derlandy.hoteis.repository.sqlite.COLUMN_SERVER_ID
import br.com.belchior.derlandy.hoteis.repository.sqlite.TABLE_HOTEL
import com.google.gson.annotations.SerializedName

@Entity(tableName = TABLE_HOTEL, indices = [Index(COLUMN_SERVER_ID, unique = true)])
data class Hotel (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COLUMN_ID)
    var id:Long = 0,
    var name:String = "",
    var address: String = "",
    var rating: Float = 0.0F,
    @SerializedName("photo_url")
    var photoUrl: String = "",
    var serverId: Long? = null,
    var status: Int = Status.INSERT
) {
    override fun toString(): String = name
}