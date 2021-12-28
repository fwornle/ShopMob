package com.tanfra.shopmob.smob.data.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.smob.data.net.api.SmobUserApi
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO
import com.tanfra.shopmob.smob.data.net.utils.ArrayListAdapter
import com.udacity.asteroidradar.api.AuthInterceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import okhttp3.logging.HttpLoggingInterceptor


// Koin module for network services
val netServices = module {

    // consistent handling of network responses/errors
    single { ResponseHandler() }

    // authentication middleware
    single { AuthInterceptor() }

    // HTTP client - allows injection of logger (for debugging... see there)
    single { provideOkHttpClient(get()) }

    // retrofit object
    // ... incl. Moshi JSON adapters for all our data sources (generalized)
    single { provideRetrofitMoshi(get()) }

    // individual APIs for access to network data (per category)
    single { provideApiForSmobUsers(get()) }

}

private fun provideRetrofitMoshi(okHttpClient: OkHttpClient): Retrofit {

    // Moshi builder
    val moshi = Moshi.Builder()
        .add(ArrayListAdapter.Factory<SmobUserNTO>())
        // NEXT: .add(ArrayListAdapter.Factory<SmobGroupNTO>())
        .add(KotlinJsonAdapterFactory())
        .build()

    return Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

}

private fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {

    // add auth first
    val client = OkHttpClient().newBuilder().addInterceptor(authInterceptor)

    // add logging (in debug mode only)
    // ... even during debug mode: disable when working (by adding hardcoded 'false &&')
    if (
        //false &&
        BuildConfig.DEBUG
    ) {

        // create and configure logging interceptor
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        // add to the HTTP client - this should always go last
        client.addInterceptor(interceptor)

    }

    // done - build client and return it
    return client.build()

}


// all APIs to be provided to the app --------------------------------------------------

// ApiSmobUsers
private fun provideApiForSmobUsers(retrofit: Retrofit): SmobUserApi = retrofit.create(SmobUserApi::class.java)
//private fun provideApiForSmobGroups(retrofit: Retrofit): SmobGroupApi = retrofit.create(SmobGroupApi::class.java)


