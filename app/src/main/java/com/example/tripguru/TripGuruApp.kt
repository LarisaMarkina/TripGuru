package com.example.tripguru

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The main application class for the TripGuru app.
 * This class is annotated with `@HiltAndroidApp`, indicating that Hilt
 * should perform dependency injection setup for this application.
 *
 * This class extends [Application] and serves as the entry point for
 * application-level configuration and initialization, although in this
 * simple case, it primarily acts as a placeholder for Hilt setup.
 */
@HiltAndroidApp
class TripGuruApp : Application()