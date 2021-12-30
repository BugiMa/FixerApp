package com.example.fixerapp.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.fixerapp.FixerAppApplication
import com.example.fixerapp.data.remote.ExchangeRatesResponse
import com.example.fixerapp.data.mapper.toDomain
import com.example.fixerapp.domain.ExchangeRate
import com.example.fixerapp.repo.IFixerAppRepository
import com.example.fixerapp.util.Constants.CONVERSION_ERROR
import com.example.fixerapp.util.Constants.NETWORK_FAILURE
import com.example.fixerapp.util.Constants.NO_INTERNET
import com.example.fixerapp.util.data.Resource
import com.example.fixerapp.util.data.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class SharedViewModel @Inject constructor(
    app: Application,
    private val repository: IFixerAppRepository
): AndroidViewModel(app) {

    private var date = "YYYY-MM-DD"
    private var loadedRates = ArrayList<ExchangeRate>()
    private val _rates = SingleLiveEvent<Resource<List<ExchangeRate>>>()
    val rates: LiveData<Resource<List<ExchangeRate>>> get() = _rates

    init {
        setDate()
        loadRates()
    }

    fun getLoadedRates() : ArrayList<ExchangeRate> {
        return loadedRates
    }

    fun getRateByIndex(index: Int): ExchangeRate {
        return loadedRates[index]
    }

    private fun setDate() {
        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        date = formatter.format(today)
    }

    fun loadRates() = viewModelScope.launch {
        safeApiCall()
    }

    private suspend fun safeApiCall() {
        _rates.postValue(Resource.Loading())
        try {
            if (isNetworkAvailable()) {
                val response = repository.getExchangeRatesByDate(date)
                _rates.postValue(handleApiResponse(response))
            } else {
                _rates.postValue(Resource.Error(NO_INTERNET))
            }
        } catch (t: Throwable) {
            Log.e("ERROR:", t.toString())
            when (t) {
                is IOException -> _rates.postValue(Resource.Error(NETWORK_FAILURE))
                else -> _rates.postValue(Resource.Error(CONVERSION_ERROR))
            }
        }
    }

    private fun handleApiResponse(response: Response<ExchangeRatesResponse>): Resource<List<ExchangeRate>> {
        if (response.isSuccessful) {
            response.body().let { resultResponse ->
                addDateToLoadedRates()
                setDateToDayBefore()
                return Resource.Success(resultResponse!!.toDomain())
            }
        }
        return Resource.Error(response.message())
    }

    private fun setDateToDayBefore() {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        calendar.time = formatter.parse(date)!!
        calendar.add(Calendar.DATE, -1)
        date = formatter.format(calendar.time)
    }

    private fun addDateToLoadedRates () {
        loadedRates.add(ExchangeRate(date))
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getApplication<FixerAppApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)     -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

}