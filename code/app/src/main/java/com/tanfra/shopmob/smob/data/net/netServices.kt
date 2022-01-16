package com.tanfra.shopmob.smob.data.net

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.smob.data.net.api.*
import com.tanfra.shopmob.smob.data.net.nto.*
import com.tanfra.shopmob.smob.data.net.utils.ArrayListAdapter
import com.tanfra.shopmob.smob.data.net.utils.AuthInterceptor
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

import okhttp3.logging.HttpLoggingInterceptor


// Koin module for network services
val netServices = module {

    // helper function to provide a configured OkHttpClient
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {

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

    // helper function to provide a configured retrofit instance
    fun provideRetrofitMoshi(okHttpClient: OkHttpClient): Retrofit {

        // Moshi builder
        val moshi = Moshi.Builder()
            .add(ArrayListAdapter.Factory<SmobUserNTO>())
            .add(ArrayListAdapter.Factory<SmobGroupNTO>())
            .add(ArrayListAdapter.Factory<SmobProductNTO>())
            .add(ArrayListAdapter.Factory<SmobShopNTO>())
            .add(ArrayListAdapter.Factory<SmobListNTO>())
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    }

    // helper function to provide APIs
    fun provideSmobUserApi(retrofit: Retrofit): SmobUserApi = retrofit.create(SmobUserApi::class.java)
    fun provideSmobGroupApi(retrofit: Retrofit): SmobGroupApi = retrofit.create(SmobGroupApi::class.java)
    fun provideSmobProductApi(retrofit: Retrofit): SmobProductApi = retrofit.create(SmobProductApi::class.java)
    fun provideSmobShopApi(retrofit: Retrofit): SmobShopApi = retrofit.create(SmobShopApi::class.java)
    fun provideSmobListApi(retrofit: Retrofit): SmobListApi = retrofit.create(SmobListApi::class.java)


    // define instances to be offered as services via the Koin service locator
    // define instances to be offered as services via the Koin service locator
    // define instances to be offered as services via the Koin service locator

    // consistent handling of network responses/errors
    single { ResponseHandler() }

    // authentication middleware
    single { AuthInterceptor() }

    // HTTP client - allows injection of logger (for debugging... see there)
    single { provideOkHttpClient(authInterceptor = get()) }

    // retrofit object
    // ... incl. Moshi JSON adapters for all our data sources (generalized)
    single { provideRetrofitMoshi(okHttpClient = get()) }


    // individual APIs for access to network data (per category) ----------------------
    // individual APIs for access to network data (per category) ----------------------
    // individual APIs for access to network data (per category) ----------------------

    // APIs to access data from the backend
    single { provideSmobUserApi(retrofit = get()) }
    single { provideSmobGroupApi(retrofit = get()) }
    single { provideSmobProductApi(retrofit = get()) }
    single { provideSmobShopApi(retrofit = get()) }
    single { provideSmobListApi(retrofit = get()) }

}


