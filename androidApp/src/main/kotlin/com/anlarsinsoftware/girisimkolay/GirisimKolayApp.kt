package com.anlarsinsoftware.girisimkolay

import android.app.Application
import android.util.Log
import com.anlarsinsoftware.girisimkolay.di.initKoin
import com.anlarsinsoftware.girisimkolay.di.appModule
import com.anlarsinsoftware.girisimkolay.firebase.FirebaseRuntimeConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class GirisimKolayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Log.i("GirisimKolayApp", "Firebase emulators enabled: ${BuildConfig.USE_FIREBASE_EMULATORS}, environment: ${BuildConfig.APP_ENVIRONMENT}")
        if (BuildConfig.USE_FIREBASE_EMULATORS) {
            FirebaseAuth.getInstance().useEmulator(FirebaseRuntimeConfig.EMULATOR_HOST, FirebaseRuntimeConfig.AUTH_PORT)
            FirebaseFirestore.getInstance().useEmulator(FirebaseRuntimeConfig.EMULATOR_HOST, FirebaseRuntimeConfig.FIRESTORE_PORT)
            FirebaseFunctions.getInstance(FirebaseRuntimeConfig.REGION)
                .useEmulator(FirebaseRuntimeConfig.EMULATOR_HOST, FirebaseRuntimeConfig.FUNCTIONS_PORT)
            FirebaseStorage.getInstance()
                .useEmulator(FirebaseRuntimeConfig.EMULATOR_HOST, FirebaseRuntimeConfig.STORAGE_PORT)
            Log.w("GirisimKolayApp", "Firebase emulator endpoints are active on ${FirebaseRuntimeConfig.EMULATOR_HOST}.")
        }
        initKoin {
            androidLogger()
            androidContext(this@GirisimKolayApp)
            modules(appModule)
        }
    }
}
