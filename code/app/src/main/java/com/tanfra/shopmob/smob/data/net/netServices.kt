package com.tanfra.shopmob.smob.data.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.smob.data.net.api.SmobUserApi
import com.udacity.asteroidradar.api.AuthInterceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// Koin module for network services
val netServices = module {

    // singleton objects - only initialize Retrofit once (converter factory: Moshi - for plain JSON)
    // ... this is an instance of a Retrofit object, implementing i/f SmobUsersApi (see below)
    single { provideRetrofitMoshi(get()) }

    // consistent handling of network responses/errors
    single { ResponseHandler() }

    // objects recreated at each 'injection'
    factory { AuthInterceptor() }
    factory { provideOkHttpClient(get()) }

    // our APIs
    factory { provideApiForSmobUsers(get()) }

}

private fun provideRetrofitMoshi(okHttpClient: OkHttpClient): Retrofit {

    // Moshi builder
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi)).build()

}

private fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
    return OkHttpClient().newBuilder().addInterceptor(authInterceptor).build()
}


// all APIs to be provided to the app --------------------------------------------------

// ApiSmobUsers
private fun provideApiForSmobUsers(retrofit: Retrofit): SmobUserApi = retrofit.create(SmobUserApi::class.java)


