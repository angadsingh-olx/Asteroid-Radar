/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.api.getFormattedDate
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.NasaDataRepository
import retrofit2.HttpException
import java.util.*

class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = NasaDataRepository(database)

        return try {
            val today = Calendar.getInstance()
            database.asteroidDao.deleteDataBeforeDate(getFormattedDate(today))

            repository.getListOfAsteroids(
                getFormattedDate(today),
                getFormattedDate(today.apply {
                    this.add(Calendar.DAY_OF_YEAR, 7)
                })
            )
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}