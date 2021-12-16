package com.udacity.asteroidradar.utils

import com.udacity.asteroidradar.Asteroid

interface OnItemClickHandler {
    fun onItemClicked(asteroid: Asteroid)
}