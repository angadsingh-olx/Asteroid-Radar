package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.getFormattedDate
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.domain.Filter
import com.udacity.asteroidradar.domain.NasaDataRepository
import com.udacity.asteroidradar.domain.PictureOfDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : ViewModel() {

    private var currentJob: Job? = null

    private val repository = NasaDataRepository(getDatabase(application))
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    val _asteroidList = repository.asteroids

    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay
    private val _pictureOfDay = repository.pictureOfDayLifeData

    val navigateToSelectedItem: LiveData<Asteroid>
        get() = _navigateToSelectedItem
    private val _navigateToSelectedItem = MutableLiveData<Asteroid>()

    init {
        viewModelScope.launch {
            repository.getPictureOfTheDay()
            onFilterChanged(Filter.WEEK)
        }
    }

    fun onFilterChanged(filter: Filter = Filter.WEEK) {
        currentJob?.cancel()
        when (filter) {
            Filter.WEEK -> {
                currentJob = viewModelScope.launch {
                    repository.getListOfAsteroids(
                        getFormattedDate(Calendar.getInstance()),
                        getFormattedDate(Calendar.getInstance().apply {
                            this.add(Calendar.DAY_OF_YEAR, 7)
                        })
                    )
                }
            }

            Filter.TODAY -> {
                currentJob = viewModelScope.launch {
                    repository.getListOfAsteroids(
                        getFormattedDate(Calendar.getInstance()),
                        getFormattedDate(Calendar.getInstance())
                    )
                }
            }

            Filter.SAVED -> {
                currentJob = viewModelScope.launch {
                    repository.getListOfAsteroids(
                        getFormattedDate(Calendar.getInstance()),
                        getFormattedDate(Calendar.getInstance())
                    )
                }
            }
        }
    }

    fun onItemClicked(asteroid: Asteroid) {
        _navigateToSelectedItem.postValue(asteroid)
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedItem.value = null
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