package br.com.belchior.derlandy.hoteis.repository.http

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.lang.Exception

class HotelSyncWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters), KoinComponent{

    override fun doWork(): Result {
        val hotelHttp: HotelHttp by inject()

        return try {
            hotelHttp.synchronizeWithServer()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {

        fun start(context: Context) : LiveData<WorkInfo> {
            val workManager = WorkManager.getInstance(context)
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequest.Builder(HotelSyncWorker::class.java)
                .setConstraints(constraints)
                .build()

            workManager.enqueue(request)

            return workManager.getWorkInfoByIdLiveData(request.id)
        }
    }
}