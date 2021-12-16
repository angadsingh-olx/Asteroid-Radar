package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Filter
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.utils.OnItemClickHandler

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModel.Factory(activity?.application!!)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = MainRecyclerViewAdapter(itemClickListener = object : OnItemClickHandler {
            override fun onItemClicked(asteroid: Asteroid) {
                viewModel.onItemClicked(asteroid)
            }
        })
        binding.asteroidRecycler.adapter = adapter

        setHasOptionsMenu(true)

        viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer {
            binding.pictureOfDay = it
        })

        viewModel.navigateToSelectedItem.observe(viewLifecycleOwner, Observer {
            if ( null != it ) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.show_week_menu -> viewModel.onFilterChanged(Filter.WEEK)
            R.id.show_today_menu -> viewModel.onFilterChanged(Filter.TODAY)
            R.id.show_saved_menu -> viewModel.onFilterChanged(Filter.SAVED)
        }
        return true
    }
}
