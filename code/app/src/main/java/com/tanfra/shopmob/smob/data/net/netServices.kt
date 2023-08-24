package com.tanfra.shopmob.smob.data.net

import com.tanfra.shopmob.BuildConfig
import com.tanfra.shopmob.BuildConfig.BASE_URL
import com.tanfra.shopmob.smob.data.net.api.*
import com.tanfra.shopmob.smob.data.net.nto.Nto
import com.tanfra.shopmob.smob.data.net.nto.SmobGroupNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobListNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobProductNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobShopNTO
import com.tanfra.shopmob.smob.data.net.nto.SmobUserNTO
import com.tanfra.shopmob.smob.data.net.utils.*
import com.tanfra.shopmob.smob.data.repo.utils.ResponseHandler
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import java.util.concurrent.TimeUnit
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.*


// Koin module for network services
val netServices = module {

    // define polymorphic serializer for class "NTO"
    // ... used below when configuring the kotlinx serializer
    val serMod = SerializersModule {
        polymorphic(Nto::class) {
            subclass(SmobGroupNTO::class)
            subclass(SmobListNTO::class)
            subclass(SmobProductNTO::class)
            subclass(SmobShopNTO::class)
            subclass(SmobUserNTO::class)
        }
    }


    // helper function to provide coroutine context
    fun provideCoroutineScope() =
        CoroutineScope(Dispatchers.Default + SupervisorJob())


    // creates Ktor client with OkHttp engine
    fun provideOkHttpClient4Ktor(
        networkConnectionInterceptor: NetworkConnectionInterceptor,
        authInterceptor: AuthInterceptor
    ): HttpClient = HttpClient(OkHttp) {

        // activate default validation to throw exceptions for non-2xx responses:
        expectSuccess = true

        // install custom handler for specific non-2xx responses:
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->

                // ensure it's one of the three exception types (should always be the case)
                val exceptionResponse = when(exception) {
                    is RedirectResponseException -> exception.response  // 3xx
                    is ClientRequestException -> exception.response     // 4xx
                    is ServerResponseException -> exception.response    // 5xx
                    else -> return@handleResponseExceptionWithRequest
                }

                // handle 404 -- note (fw-230819: also handled in ResponseHandler, see repo classes)
                if (exceptionResponse.status == HttpStatusCode.NotFound) {
                    val exceptionResponseText = exceptionResponse.bodyAsText()  // cache text
                    throw MissingPageException(exceptionResponse, exceptionResponseText)
                }

            }
        }

        engine {

            addInterceptor(networkConnectionInterceptor)
            addInterceptor(authInterceptor)

           // add logging (in debug mode only -- can be hardcoded to NONE by adding 'false')
            addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(
                        if (true && BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                        else HttpLoggingInterceptor.Level.NONE
                    )
                }
            )

            // general OkHttpConfig properties
            config {
                readTimeout(3, TimeUnit.SECONDS)
                connectTimeout(6, TimeUnit.SECONDS)
            }

        }  // engine

        // set default request parameters
        defaultRequest {
            // add base url for all request
            url(BASE_URL)
        }

        // use json (= kotlinx.serialization) content negotiation for serialize or deserialize
        install(ContentNegotiation) {
            // configure kotlinx serializer for JSON & polymorphic classes - defined above (serMod)
            json(
                Json {
                    encodeDefaults = true
                    classDiscriminator = "source"
                    serializersModule = serMod
                }
            )
        }

    }  // provideOkHttpClient4Ktor


    // helper function to provide APIs
    fun provideSmobUserApi(ktorClient: HttpClient): SmobUserApi  =
        SmobUserApiImpl(ktorClient, "users")

    fun provideSmobGroupApi(ktorClient: HttpClient): SmobGroupApi  =
        SmobGroupApiImpl(ktorClient, "groups")

    fun provideSmobProductApi(ktorClient: HttpClient): SmobProductApi  =
        SmobProductApiImpl(ktorClient, "products")

    fun provideSmobShopApi(ktorClient: HttpClient): SmobShopApi  =
        SmobShopApiImpl(ktorClient, "shops")

    fun provideSmobListApi(ktorClient: HttpClient): SmobListApi  =
        SmobListApiImpl(ktorClient, "lists")


    // define instances to be offered as services via the Koin service locator
    // define instances to be offered as services via the Koin service locator
    // define instances to be offered as services via the Koin service locator

    // instantiate our custom NetworkConnectionManager class (as singleton)
    single<NetworkConnectionManager> {
        NetworkConnectionManagerImpl(context = get(), coroutineScope = provideCoroutineScope())
    }

    // consistent handling of database access responses/errors (at repository level)
    // note: fw-230819: redundant, use HttpResponseValidator instead (KTOR HttpClient engine config)
    single { ResponseHandler() }

    // authentication middleware
    single { AuthInterceptor() }

    // network connection middleware
    single { NetworkConnectionInterceptor(networkConnectionManager = get()) }

    // (KTOR) HTTP client
    single { provideOkHttpClient4Ktor(
        networkConnectionInterceptor = get(),
        authInterceptor = get()
    ) }


    // individual APIs for access to network data (per category) ----------------------
    // individual APIs for access to network data (per category) ----------------------
    // individual APIs for access to network data (per category) ----------------------

    // APIs to access data from the backend
    single { provideSmobUserApi(ktorClient = get()) }
    single { provideSmobGroupApi(ktorClient = get()) }
    single { provideSmobProductApi(ktorClient = get()) }
    single { provideSmobListApi(ktorClient = get()) }
    single { provideSmobShopApi(ktorClient = get()) }

}


