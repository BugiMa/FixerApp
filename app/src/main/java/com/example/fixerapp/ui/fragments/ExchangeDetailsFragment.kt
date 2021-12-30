package com.example.fixerapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.fixerapp.databinding.FragmentExchangeDetailsBinding
import com.example.fixerapp.ui.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToLong

@AndroidEntryPoint
class ExchangeDetailsFragment : Fragment() {

    private var _binding: FragmentExchangeDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: ExchangeDetailsFragmentArgs by navArgs()

    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        val dateTextView = binding.date
        val toCurrencyLabelTextView = binding.toCurrencyLabel
        val toCurrencyTextView = binding.toCurrency

        val rateData = viewModel.getRateByIndex(args.repoIndex)

        dateTextView.text = rateData.date

        toCurrencyLabelTextView.text = rateData.rate.first
        toCurrencyTextView.text = rateData.rate.second.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}