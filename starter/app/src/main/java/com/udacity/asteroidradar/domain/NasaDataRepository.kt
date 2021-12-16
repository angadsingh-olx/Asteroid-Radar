package com.udacity.asteroidradar.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class NasaDataRepository(val database: AsteroidDatabase) {

    var pictureOfDayLifeData: MutableLiveData<PictureOfDay> = MutableLiveData()

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getListOfAsteroids()) {
        it.asDomainModel()
    }

    suspend fun getPictureOfTheDay(){
        withContext(Dispatchers.IO) {

            val result = Network.apiService.getPictureOfTheDay("").body()!!
            pictureOfDayLifeData.postValue(result)
        }
    }

    suspend fun getListOfAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {
            val response = Network.apiService.getNeoFeedData("", startDate, endDate)
            if (response.isSuccessful) {
                val result = parseAsteroidsJsonResult(JSONObject(response.body()!!))
                database.asteroidDao.insertAllAsteroid(result.asDatabaseModel())
            }
        }
    }
}