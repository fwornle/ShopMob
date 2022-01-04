```plantuml
@startuml
package "startKoin lambda" #Lightgray {

    package "Koin module: dbServices" #Lightblue {
        [single { LocalDB.createSmobProductDao(get()) }]   -up->      [single { LocalDB.**createSmobDatabase**(get()) }]
        [single { LocalDB.createSmobShopDao(get()) }]      -up->    [single { LocalDB.**createSmobDatabase**(get()) }]
        [single { LocalDB.createSmobListDao(get()) }]          -left->   [single { LocalDB.**createSmobDatabase**(get()) }]
        [single { LocalDB.createSmobGroupDao(get()) }]     -right->   [single { LocalDB.**createSmobDatabase**(get()) }]
        [single { LocalDB.**createSmobUserDao**(get()) }]  -up->      [single { LocalDB.**createSmobDatabase**(get()) }]
    }

    [applicationContext]<-down-[single { LocalDB.**createSmobDatabase**(get()) }]

    package "Koin module: netServices" #Lightblue {
        together {
            [single { AuthInterceptor() }]
            [single { provideOkHttpClient(authInterceptor = get()) }]  -down->  [single { AuthInterceptor() }]
            [single { provideRetrofitMoshi(okHttpClient = get()) }]    -down->  [single { provideOkHttpClient(authInterceptor = get()) }]
            [single { **provideSmobUserApi**(retrofit = get()) }]      -down->  [single { provideRetrofitMoshi(okHttpClient = get()) }]
            [single { provideSmobShopi(retrofit = get()) }]            -down->  [single { provideRetrofitMoshi(okHttpClient = get()) }]
        }
        [single { ResponseHandler() }]
    }

    package "Koin module: repoServices" #Lightblue {
        together {
            [...]
            [single<SmobUserDataSource> { SmobUserRepository(get(), get()) }]  -up->    [single { LocalDB.**createSmobUserDao**(get()) }]
            [single<SmobUserDataSource> { SmobUserRepository(get(), get()) }]  -down->  [single { **provideSmobUserApi**(retrofit = get()) }]
            [single<SmobShopDataSource> { SmobShopRepository(get(), get()) }]  -up->    [single { LocalDB.createSmobShopDao(get()) }]
            [single<SmobShopDataSource> { SmobShopRepository(get(), get()) }]  -down->  [single { provideSmobShopi(retrofit = get()) }]
        }
    }
@enduml
```