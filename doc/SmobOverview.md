# ShopMob Overview

Shopping list management for collaborative shopping trips.

---

This document describes the [User Interface](#user-interface-ui) as well as the [Project Workspace](#project-workspace)
of ShopMob.

See [Design](./Design.md) for more details about the design aspects of the app.

---

### User Interface (UI)

#### Authentication

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="ShopMob Login Screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_login_0.PNG" title="Login Screen"/>
  <img alt="ShopMob Login Screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_login.PNG" title="Login Screen"/>
  <img alt="ShopMob Login Screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_login_2fa.PNG" title="Login Screen"/>
</div>

The app launches with an authentication screen. Using the _firebaseUI_ authentication flow, login can be achieved 
by entering a user email and password, or via federated identity authentication using a google account. The flow
has been set up to use 2-factor authentication.

#### Planning

##### SmobList - the ShopMob shopping lists

Once logged on, the user arrives at the planning screen of the app. The first time the app is used, the local 
database is still empty. When there is no connection to the backend, the app has no data. This is indicated to the user
as shown below.

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobLists screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_lists.PNG" title="SmobLists"/>
  <img alt="SmobLists screen, no data" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_backend_missing_1.PNG" title="SmobLists, no data"/>
</div>

When the connection to the backend is established, the app downloads all shopping lists the user has subscribed to. Note that, 
at the writing of this document (Jan 2022), user groups have yet to be established - at present, all shopping lists are 
presented to all users on all devices.

##### SmobShop - the stores defined in ShopMob 

Clicking on the home menu button opens a drawer menu with two options:

- Smob Shops
- Administration

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobShops screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_drawer.PNG" title="Drawer Menu"/>
  <img alt="SmobShops screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shops.PNG" title="Administration"/>
  <img alt="SmobShops screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shops_details_2.PNG" title="SmobShops"/>
  <img alt="SmobShops screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_geofence_check_2.PNG" title="SmobShops"/>
</div>

While the administration screen is presently still empty - this it will be possible to define users, user groups, etc. -
the SmobShop list shows all shops currently defined in ShopMob. Clicking on a SmobShop item takes the user to a
details screen on which they can see the details of a particular shop, eg. where to find it, opening hours, etc.

Clicking on the highlighted coordinates of the shop opens google maps in streetview (if available) at the selected location. 
The shop image is loaded from a URL, should one be provided. If not, a ShopMob placeholder image is shown instead.

The Floating Action Button ("+") can be used to define a new SmobShop. An edit screen is shown

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobShops screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_edit.PNG" title="ShopMop definition"/>
  <img alt="SmobShops screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_edit_type.PNG" title="ShopMop definition"/>
  <img alt="SmobShops screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_edit_type_done.PNG" title="ShopMop definition"/>
</div>

A shop name has to be specified, as well as the general shop type (eg. Supermarket, Drugstore, ...). In addition, the location
of the shop has to be defined. To home in on the current location of the device, the user is asked for permission to the 
user location information on their phone:

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="Location screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_map_perm.PNG" title="Location selection"/>
  <img alt="Location screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_map_select.PNG" title="Location selection"/>
  <img alt="Location screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_map_select_2.PNG" title="Location selection"/>
</div>

The style of the map can be altered via the overlay menu:

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="Location screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_map_menu.PNG" title="Location selection"/>
  <img alt="Location screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_map_hybrid.PNG" title="Location selection"/>
</div>

Once the shop location has been selected (user clicks on OK), the newly defined SmobShop can be stored in the local DB on the
device and, if a network connection to the backend has been established, synchronized to the backend. During the storing of the 
SmobShop, a GeoFence is registered - provided the user chooses to grant the necessary permissions:

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="GeoFencing" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_geofence_perm_1.PNG" title="GeoFencing, Permissions"/>
  <img alt="GeoFencing" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_geofence_perm_2.PNG" title="GeoFencing"/>
  <img alt="GeoFencing" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_geofence_done.PNG" title="GeoFencing"/>
</div>

The GeoFence will trigger as soon as the device enters a perimeter of 100 m of the SmobShop store location. The app does not
have to be open or in the background for this. A _BroadCast Receiver_ has been installed to listen for GeoFence events.
Having receive such an event, the local DB is queried for all the shopping lists (of the user). If an item on any of these lists
matches the SmobShop main category (eg. Drugstore), then a notificaton with the details of the SmobShop is sent to the user.

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="GeoFencing" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_geofence_check.PNG" title="GeoFencing, notification"/>
</div>

This notification is of a low priority - a short bell is sounded and a small icon appears at the top of the screen, indicating the
receipt of a new notification. The app does not have to be active to receive this notification. Opening the notification, launches the
app on the shops details screen. Note that, this time, the LOGOUT menu is replaced by a LAUNCH menu. Clicking on this menu item allows
the user to open the app on the SmobLists screen, to see what items they might want to buy in this shop. In addition, the user can get
to the shopping screen via the animated Shop Floor button.

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="GeoFencing" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_geofence_check_2.PNG" title="GeoFencing, notification"/>
  <img alt="GeoFencing" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_geofence_check_3.PNG" title="GeoFencing, notification"/>
  <img alt="GeoFencing" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_geofence_check_5.PNG" title="GeoFencing, notification"/>
  <img alt="GeoFencing" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_lists.PNG" title="GeoFencing, notification"/>
</div>


##### SmobShop - the shop floor area

Clicking on the animated "Shop Floor" button takes the user to the Shopping area of the app. At present, this area still not available.
Forthcoming updates of the app will extend the functionality by a floor plan of the shop, guiding the user to the desired
products inside the shop (zone, aisle, shelf). An animated "coming soon" screen is shown instead (MotionLayout).

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobShopping screen" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_1.PNG" title="ShopMop shopping"/>
  <img alt="SmobShopping screen, no data" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_2.PNG" title="ShopMop shopping"/>
  <img alt="SmobShopping screen, no data" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_shop_3.PNG" title="ShopMop shopping"/>
</div>

##### SmobShop - the product list area

Clicking on any of the SmobList (shopping) lists takes the user to the list of products currently on this shopping list.
Items on a shopping list are colored in shades of green:

- very light green: the item is OPEN, i.e. none of the Smobbers has indicated yet, that they would go and buy this item.
- light green: a Smobber has selected the item for shopping and communicates this to their fellow Smobbers. The item is IN_PROGRESS.
- dark green: a Smobber has put the item in their shopping basket (or purchased the item). Others now know that this item is no longer needed

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobProduct list" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_product_list.PNG" title="SmobProduct list"/>
  <img alt="SmobProduct list, no data" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_lists.PNG" title="SmobProduct list"/>
</div>

Note that the state of the associated shopping list (OPEN, IN_PROGRESS, DONE) as well as the degree of completion of a shopping list adjust
automatically, as the Smobbers select or buy items. It is also possible to select all items on a SmobList by swiping the list to the right.
A SmobList can be deleted by swiping left. Before it is deleted, an UNDO button is show for a few seconds. Letting this
cadence time elapse confirms to the app that the user wants to delete the list. The list is marked as DELETED - it is not
removed from the local DB or the backend DB. In future releases of the app, it will be possible to retrieve an accidentally deleted list via
the administration screen.

The same swiping logic applies to the items on the actual shopping list (SmobProducts). In addition, a click on an item takes
the user to a details screen. Similarly to the shop details screen, the product details are shown. Where available, a product image is 
loaded from a URL - if no URL has been provided, a placeholder image is shown instead. Future editions of the app will allow 
the user to take product images with the camera of their device and upload them to the backend storage. This will make it easy
to collect a list of products and product details which are relevant to this particular user. At present, random images are 
provided to demonstrate the mechanism (same as all other product details - lore ipsum...).

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobProduct list" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_product_details.PNG" title="SmobProduct list"/>
  <img alt="SmobProduct list" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_product_swipe_right.PNG" title="SmobProduct list"/>
  <img alt="SmobProduct list" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_product_swipe_left.PNG" title="SmobProduct list"/>
  <img alt="SmobProduct list" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_product_swipe_undo.PNG" title="SmobProduct list"/>
</div>

Products can be added to a SmobList using the "+" Floating Action Button. This is similar to what has been described for the SmobList 
edit screen. Products need to be classified in a main category (FOODS, HARDWARE, CLOTHING, etc.) and a sub category
(FRUIT_N_VEGETABLES, DAIRY, BEVERAGES, BAKERY, ...). This allows a shopping list to be sorted in a meaningful way. In its present form,  
the product edit interface does not yet offer a hierarchical category selection, where the sub-category changes dynamically, depending
on the choices made for main-category. This will be added in forthcoming versions of the app. In addition, the mapping of main product
categories to shop categories will also be configurable. At present, this mapping is provided by a fix class - just to demonstrate the 
fundamental idea - i.e. in a SUPERMARKET one can buy FOOD, CLOTHING, HARDWARE, SUPPLIES, BAKERY, ...

Also note, the idea for this app arose when realizing that our "Alexa - put Milk on the shopping list" UI and the associated
Alexa App (/Skill) does not provide shared access to the shopping list, nor multiple shopping lists or sorting by product categories.
A future extension of the app will provide an Alexa Skill to be able to "speak to Alexa" in order to control what items go 
on which list (in the backend). Editing a shopping list via the app is therefore thought as a secondary use case, as it is much 
more tedious to do so via the app as it will be using Alexa's ability to make sense of the user's utterances.

##### Synchronization with the Backend

Throughout the development of this app a simple local backend has been used. The _express_ framework (JavaScript) based 
[Json-Server](https://www.npmjs.com/package/json-server) running on **node.js** provides a simple REST API from a JSON file
based "database". The database file "db.json" is generated using a small JavaScript function. Content is created from custom
tables (where it mattered) and [faker](https://www.npmjs.com/package/faker), a flexible JavaScript package for node.js which 
supports creation of user data, URLs, UUIDs, dates, random text, etc. Using this approach, the backend was developed in parallel
to the actual app development. Once mature (= two days ago... ;) ), the local backend has been transferred to an AWS cloud
instance using SST, the [Serverless Stack](https://serverless-stack.com/chapters/serverless-nodejs-starter.html) framework for
infrastructure as code. The resulting backend runs as AWS lambda functions, i.e. they are spun up on demand, when the app
establishes a connection via the Application Server exposed REST API. The (still file based) backend DB is in an S3 bucket 
and can easily be replaced (drag'n'drop) should this be necessary. In the future, a proper backend DB will be used and a
bridge will be provided to Alexa's Skills system.

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobProduct background sync" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_backend_local.PNG" title="SmobShop Background Sync"/>
</div>

**Note:**

The app currently uses rather short timeouts (6 seconds to establish a connection and 3 seconds for read / write access). This
proved a useful compromise between long periods of the "progress bar spinner" and stable operations of the app. However, as the backend
first needs to spin up a container for the lambda function of our backend, this timeout may have to be increased in the future, should
longer routes from remote locations need more time. Once a timeout condition has been detected, the app ceases to sync with the backend
to provide a smooth user experience. In this condition, data is merely stored in the local DB and not synchronized with the server.
The app regularly attempts to re-connect with the server. For example, pulling down on a list screen triggers a backend sync
refresh which - if successful - re-activates the periodic background sync.

In addition, a slowly running background job attempts to reactivate this "polling" mechanism every 30 minutes. Fast polling is only activated when the app is in the foreground.
Altogether, this synchronization will have to be extended in the future, as a re-connecting app will currently simply overwrite some of the
local changes with the status values found in the backend DB. Here, a proper synchronization strategy is needed, similarly
to what git offers. Maybe Room can be replaced by a git repository altogether...
Using this local backend, the development of the synchronizing mechanisms was greatly accelerated. The script provides immediate
feedback on the CRUD commands exchanged with the express server. Running the app on two emulators, the exchange of data can be 
observed and verified / debugged: 

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobProduct background sync" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_backend_local2.PNG" title="SmobShop Background Sync"/>
</div>

The local backend can be found in folder /backend. It can be launched by running node script "backend" (>> node backend) and it
responds with a list of the principal routes which are available. To experiment with the local backend, two small changes 
need to be made. First, the AWS server needs to be replaced by _localhost:3000_ which, on the qEMU emulator is mirrored at 10.0.2.2:3000.

This is done in the project gradle build file (app), where simply commenting out the current BASE_URL definition commenting in the 
one of the local backend does the trick: 

```groovy
//    buildConfigField "String", "BASE_URL", "\"http://10.0.2.2:3000\""
    buildConfigField "String", "BASE_URL", "\"https://jp8rx9tjtg.execute-api.eu-central-1.amazonaws.com/dev/\""
```

In addition, the local backend uses a slightly different API_URL (a version specifier "1" is used - couldn't get this to 
work on AWS yet... this difference will disappear in the future): 

```kotlin
//    const val SMOB_API_URL = "api/1"  // simulated (local) backend
    const val SMOB_API_URL = "api/"     // actual backend (AWS)
```

Simply use whichever line is appropriate for whichever use case, i.e. local backend vs. real backend on 
[AWS](https://eu-central-1.console.aws.amazon.com/cloudformation/home?region=eu-central-1#/stacks/stackinfo?filteringStatus=active&filteringText=&viewNested=true&hideStacks=false&stackId=arn%3Aaws%3Acloudformation%3Aeu-central-1%3A930500114053%3Astack%2Fshopmob%2F759c6370-7dc7-11ec-b7e9-060181288dce)

Once running, go to localhost:3000 or the above (cryptic) AWS URL to find the endpoints where the backend exposes
the REST API routes needed by ShopMob.

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="SmobProduct REST API" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_backend_aws.PNG" title="SmobShop REST API"/>
</div>

The cryptic domain name will be replaced by something more human-readable in forthcoming extensions of the app as well.

---

### Project Workspace

#### General Overview

At top level, the app's code base includes three distinct areas:

1. backend
2. code
3. doc

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="Workspace" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_proj_0.PNG" title="ShopMob Top Level"/>
</div>

Folder **backend** includes a local [backend](#synchronization-with-the-backend) which has been created for and used during development of this app.
Folder **code** includes the main code base of the app. Folder **doc** includes the documentation of the app as well as of
the udacity nano-degree capstone project which lead to its creation. This document is part of folder doc. 

The main main code base of the app can be found in workspace **code**. It is organized in the following principal sections:

1. data
2. geofence
3. ui
4. work
5. utils

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="Workspace" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_proj_1.PNG" title="ShopMob Workspace"/>
</div>

The **data** layer includes the repository through which access is provided to both the local DB and the backend DB (via a RESTful API).
Section **geofence**  includes the handling of geofence events via a _BroadCast Receiver_ as well as _Geofence Service Handler_ which is
triggered by a WorkManager background job. Section **ui** includes all activities, fragments as well as the viewModels which are used to 
hold the date vor the UI layer. Section **work** includes the WorkManager background services used for periodic synchronization of the 
device with the backend. Package **utils** includes a number of helper classes and extension functions which are shared by the other
sections.

In addition to the app's code base, two test projects have been defined, providing regression testing of several aspects of the app. Initially, 
both unit tests and instrumented test had been defined. However, given the lack of time, the test projects have not been maintained
for some time and, as such, need to be altered to reflect the many substantial changes in the app's main code base. Prior to extending
this app any further, both test projects will be fixed and completed - this is necessary, as the number of features of the app will grow 
over time, and it will become essential to perform continuous testing to avoid breaking changes from being merged to the main branch of the app.

#### Data Layer

The **data layer** of the app follows the recommended repository architecture, thereby abstracting the individual data sources
(local DB, Network) from the app's core logic. The data layer is structured as follows:

1. local
2. net
3. repo

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="Workspace - Data Layer" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_proj_data.PNG" title="ShopMob Data Layer"/>
  <img alt="Workspace - Data Layer" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_proj_data_local.PNG" title="ShopMob Data Layer - Local"/>
  <img alt="Workspace - Data Layer" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_proj_data_net.PNG" title="ShopMob Data Layer - Network"/>
  <img alt="Workspace - Data Layer" height="300" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_proj_data_repo.PNG" title="ShopMob Data Layer - Repository"/>
</div>

##### Persistent storage in a Local DB

Persistent on-device storage is provided using a Room mySQL database. Access to this DB is through the data layer abstacting 
repository (see below). The repository implements the Data Access Object interface (DAO). During the SW build, gradle plugins
generate the needed access code to the Room DB data tables. The format of the latter is governed by annotated data classes
defining "Data Transfer Objects" (DTO). The (Room) annotations provides the code generator with the necessary information to 
derive matching SQL tables (with column names as per annotation in the data class). In addition, these 
annotations also link each data class to a matching type conversion class which provides the necessary serialization /
deserialization methods for complex data types. The _Kotlinx Serializer_ is used for this.

As each domain (local DB, network, application) defines their own data types (DTO - local DB, NTO - network, ATO - application),
extension classes have been defined to convert between these distinct data types: package _dto2ato_ includes the extension 
classes for the conversion from DTO data (local DB) to ATO data (application). In many cases, a 1:1 property equality is used, 
but some nested structures are resolved when turning application data types (ATO) into local DB types (DTO) to better match the
table structure of an SQL database.

Kotlin file _utils/dbTypes.kt_ includes a variety of data type definitions which are fundamental to the application: Enums for 
ShopItemStatus, ProductCategory (main, sub), etc.

##### Network access to the Backend

The RESTful API exposed by the AWS cloud based backend is used by the network layer of the app. Package 
_api_ plays the role of the _dao_ in accessing the local DB: the interface defined in package api is implemented
by the generated Retrofit2 code, similarly to what is being done with Room. Annotations are used to define 
all CRUD routes using the HTTP standard commands GET, POST, PUT, DELETE. The network data type NTO can be 
converted from/to DTO. There is no direct conversion between NTO and ATO, as the application layer will only 
access the local DB, with the above described underlying synchronization mechanism to the backend (via the REST API).

All network services are collected in a Koin service locator module (Kotlin file _netServices.kt_) from where
they get made available for dependency "injection". The network services include an OkHttp3 client which are 
integrated in the Retrofit2 builder. This service modules include some form of middleware in the form of interceptors 
(AuthInterceptor, LoggingInterceptor, NetworkConnectionInterceptor). These modules are used to provide supporting 
middleware functionality along all routes serviced by retrofit. Network logging can be activated by simply commenting 
out the masking "false" condition in the following code section of _netServices.kt_:

```kotlin
    // helper function to provide a configured OkHttpClient
    fun provideOkHttpClient(app: Application, authInterceptor: AuthInterceptor): OkHttpClient {

        // add connection first, then auth
        val client = OkHttpClient().newBuilder()
            .addInterceptor(NetworkConnectionInterceptor( app.applicationContext ))
            .addInterceptor(authInterceptor)
            .readTimeout(3, TimeUnit.SECONDS)
            .connectTimeout(6, TimeUnit.SECONDS)


        // add eventually logging (in debug mode only)
        // ... even during debug mode: disable when working (by adding hardcoded 'false &&')
        if (
            false &&
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
```




##### Abstraction through Repositories


#### User Interface

#### GeoFencing

#### Background Work

#### Utilities






