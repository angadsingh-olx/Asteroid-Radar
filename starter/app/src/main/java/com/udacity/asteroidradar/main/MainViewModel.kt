package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.api.getFormattedDate
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.NasaDataRepository
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : ViewModel() {

    val repository = NasaDataRepository(getDatabase(application))
    val asteroidList = repository.asteroids

    val pictureOfDay = repository.pictureOfDayLifeData

    init {
        viewModelScope.launch {
            repository.getListOfAsteroids(getFormattedDate(Calendar.getInstance()), getFormattedDate(Calendar.getInstance().apply {
                this.add(Calendar.DAY_OF_YEAR, 7)
            }))
            repository.getPictureOfTheDay()
        }
    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}