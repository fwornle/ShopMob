package com.tanfra.shopmob.smob.data.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.smob.data.net.api.ApiSmobUsers
import com.udacity.asteroidradar.api.AuthInterceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

// Koin module for network services
val netModule = module {

    // singleton objects - only initialize Retrofit once (converter factory: Moshi - for plain JSON)
    single { provideRetrofitMoshi(get()) }

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
private fun provideApiForSmobUsers(retrofit: Retrofit): ApiSmobUsers = retrofit.create(ApiSmobUsers::class.java)


