package com.poketto.poketto.api

import com.poketto.poketto.models.Network
import com.poketto.poketto.services.Explorer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInitializer(baseUrl: String = DEFAULT_BASE_URL) {

    companion object {
        var DEFAULT_BASE_URL = Network.GNOSIS.explorerApiUrl

        fun forNetwork(network: Network): RetrofitInitializer {
            return RetrofitInitializer(network.explorerApiUrl)
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun explorer(): Explorer {
        return retrofit.create(Explorer::class.java)
    }
}