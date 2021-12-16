package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.getFormattedDate
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.domain.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(application: Application) : ViewModel() {

    private var currentJob: Job? = null

    private val repository = NasaDataRepository(getDatabase(application))
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    private val _asteroidList = repository.asteroids

    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay
    private val _pictureOfDay = repository.pictureOfDayLifeData

    val navigateToSelectedItem: LiveData<Asteroid>
        get() = _navigateToSelectedItem
    private val _navigateToSelectedItem = MutableLiveData<Asteroid>()

    val state: LiveData<State>
        get() = _state
    private val _state = MutableLiveData<State>()

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
                _state.value = State.LOADING
                currentJob = viewModelScope.launch {
                    kotlin.runCatching {
                        repository.getListOfAsteroids(
                            getFormattedDate(Calendar.getInstance()),
                            getFormattedDate(Calendar.getInstance().apply {
                                this.add(Calendar.DAY_OF_YEAR, 7)
                            })
                        )
                    }.onSuccess {
                        _state.value = State.DONE
                    }.onFailure {
                        _state.value = State.ERROR
                    }
                }
            }

            Filter.TODAY -> {
                _state.value = State.LOADING
                currentJob = viewModelScope.launch {
                    kotlin.runCatching {
                        repository.getListOfAsteroids(
                            getFormattedDate(Calendar.getInstance()),
                            getFormattedDate(Calendar.getInstance())
                        )
                    }.onSuccess {
                        _state.value = State.DONE
                    }.onFailure {
                        _state.value = State.ERROR
                    }
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