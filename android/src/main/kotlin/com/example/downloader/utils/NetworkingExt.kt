package com.example.downloader.utils

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.features.HttpTimeout


fun createHttpClient(enableNetworkLogs: Boolean = false) = buildHttpClient {

    install(HttpTimeout) {
        socketTimeoutMillis = 520_000
        requestTimeoutMillis = 360_000
        connectTimeoutMillis = 360_000
    }
}

fun buildHttpClient(extraConfig: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(
        block = extraConfig,
    );
}