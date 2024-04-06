package com.example.mzenly

// https://sagar0-0.medium.com/use-googlemaps-and-get-marker-address-details-in-jetpack-compose-29e0876f4d1a

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

sealed class ResponseState<out T> {
    data object Idle : ResponseState<Nothing>()
    data object Loading : ResponseState<Nothing>()
    data class Error(val error: Throwable) : ResponseState<Nothing>()
    data class Success<R>(val data: R) : ResponseState<R>()
}


class MapsViewModel : ViewModel() {
    private val _addressDetail = MutableStateFlow<ResponseState<Address>>(ResponseState.Idle)
    val addressDetail = _addressDetail.asStateFlow()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getMarkerAddressDetails(lat: Double, long: Double, context: Context) {
        _addressDetail.value = ResponseState.Loading
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            geocoder.getFromLocation(
                lat,
                long,
                1,
            ) { p0 ->
                _addressDetail.value = ResponseState.Success(p0[0])
            }
        } catch (e: Exception) {
            _addressDetail.value = ResponseState.Error(e)
        }
    }
}
