package com.udacity.asteroidradar.utils

import com.udacity.asteroidradar.domain.Asteroid

interface OnItemClickListener {
    fun onItemClicked(asteroid: Asteroid)
}