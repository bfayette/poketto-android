package com.poketto.poketto.controllers

import android.os.Handler
import android.os.Looper
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.util.concurrent.Executors

private val executor = Executors.newCachedThreadPool()
private val mainHandler = Handler(Looper.getMainLooper())

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun doAsync(task: () -> Unit) {
    executor.execute {
        task()
    }
}

fun uiThread(task: () -> Unit) {
    mainHandler.post {
        task()
    }
}
