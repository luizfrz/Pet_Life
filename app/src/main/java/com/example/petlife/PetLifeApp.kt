package com.example.petlife

import android.app.Application
import com.example.petlife.di.AppContainer

class PetLifeApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        container.notifier.ensureChannel()
    }
}
