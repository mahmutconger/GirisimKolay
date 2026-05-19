package com.anlarsinsoftware.girisimkolay

import android.app.Application
import com.google.firebase.FirebaseApp
import com.anlarsinsoftware.girisimkolay.di.initKoin
import com.anlarsinsoftware.girisimkolay.di.appModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class GirisimKolayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (BuildConfig.USE_FIREBASE_EMULATORS) {
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099)
            FirebaseFirestore.getInstance().useEmulator("10.0.2.2", 8080)
        }
        initKoin {
            androidLogger()
            androidContext(this@GirisimKolayApp)
            modules(appModule)
        }
    }
}
