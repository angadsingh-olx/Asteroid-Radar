package com.udacity.asteroidradar.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.getFormattedDate
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

class NasaDataRepository(val database: AsteroidDatabase) {

    private var _pictureOfDayLifeData: MutableLiveData<PictureOfDay> = MutableLiveData()

    val pictureOfDayLifeData: LiveData<PictureOfDay>
        get() = _pictureOfDayLifeData

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getListOfAsteroids(
            getFormattedDate(Calendar.getInstance()))) {
        it.asDomainModel()
    }

    suspend fun getPictureOfTheDay() {
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                Network.apiService.getPictureOfTheDay(BuildConfig.NASA_API_KEY).body()!!
            }.onSuccess {
                _pictureOfDayLifeData.postValue(it)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    suspend fun getListOfAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            kotlin.runCatching {
                Network.apiService.getNeoFeedData(BuildConfig.NASA_API_KEY, startDate, endDate)
            }.onSuccess {
                if (it.isSuccessful) {
                    database.asteroidDao.clearDB()
                    val result = parseAsteroidsJsonResult(JSONObject(it.body()!!))
                    database.asteroidDao.insertAllAsteroid(result.asDatabaseModel())
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    suspend fun getSavedListOfAsteroids() {

    }
}