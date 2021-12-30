package com.example.fixerapp.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fixerapp.R
import com.example.fixerapp.databinding.FragmentExchangeListBinding
import com.example.fixerapp.domain.ExchangeRate
import com.example.fixerapp.ui.SharedViewModel
import com.example.fixerapp.ui.adapters.ExchangeRateListAdapter
import com.example.fixerapp.util.Constants.NO_INTERNET
import com.example.fixerapp.util.data.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeListFragment : Fragment() {

    private var _binding: FragmentExchangeListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        val progressBar = binding.progressBar
        val ratesRecyclerView = binding.exchangeRatesList
        val rateListAdapter = ExchangeRateListAdapter(viewModel.getLoadedRates())

        ratesRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(requireContext())
            this.adapter = rateListAdapter
            this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val lastVisiblePosition: Int = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (lastVisiblePosition == recyclerView.adapter!!.itemCount - 1) {
                        viewModel.loadRates()
                    }
                }
            })
        }

        viewModel.rates.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    progressBar.visibility = View.GONE
                    response.data?.let { data ->

                        rateListAdapter.updateData(data as ArrayList<ExchangeRate>)
                        Toast.makeText(requireActivity(),"Exchange rates loaded successfully!", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    progressBar.visibility = View.GONE
                    response.message?.let { message ->
                        if (message == NO_INTERNET)
                            noInternetDialog()
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun noInternetDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Oops!")
            .setIcon(R.drawable.ic_round_error_outline_24)
            .setMessage("It looks like You don't have internet connection. Would You like to turn internet on now?")
            .setPositiveButton("Mobile Data") { _, _ ->
                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
            }
            .setNegativeButton("Wi-Fi") { _, _ ->
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
            .setNeutralButton("No") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}