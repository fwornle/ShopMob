# ShopMob

A shopping list app for group ("mob") based shopping: share the items you wanna buy online, access 
it from the individual handset of each group member. Only buy each item once... and, as such, avoid 
having to eat spinach for weeks after the panicky last-minute Xmas shopping trip with your family.

## Features:

1. The app updates the list, as a shopping group member puts an item in their basket and ticks it 
   off the list. 
2. Where available, hints will be issued to quickly find an item in a "known" shop - this aims at
   supporting husbands, who otherwise aimlessly wander around a shop, trying to find the lentils 
   their wife told them to get.
3. Multiple lists can be shared with each group (e.g. groceries, DIY, ...)
   a. A list can be associated with one or more shops
   b. Arriving in the vicinity of any such shop, a notification is sent, reminding the user
      that there's mob shopping to be done
   c. This location reminder is cleared, if the list has zero items on it (as other members
      of the shop mob may have already been able to buy everything)
4. The user can be member of multiple groups (e.g. shop-for-Xmas, family-shopping, toms-party, ...)
5. The user authenticates using an email/password or via a federated authentication process (OAUTH)
6. Once near a shop, a reminder is sent with the remaining items on the corresponding shopping list.
7. From the reminder, the list view can be opened, to allow the shopping to commence.

## Design

The design of the app can be found **[here](doc/Design.md)**.

## Getting Started

1. Clone the project to your local machine.
2. Open the project using Android Studio.

### Dependencies

```
1. A created project on Firebase console.
2. A create a project on Google console.
```

Note that the API keys and credential information associated with these two dependencies have to be obtained separately and placed in the following
folders of the app:

- google-services.json
  - ShopMob/code/app
- google_maps_api.xml
  - ShopMob/code/app/src/**main**/res/values
  - _(previously: ShopMob/code/app/src/{**release**|**debug**}/res/values)_

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="Google Services" width="400" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/google_services.PNG" title="Google Services"/>
  <img alt="Google Maps API Key" width="400" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/gms_api_key.PNG" title="Google Maps API Key"/>
</div>

Retrieval of these files is described in many posts on the "international network".

The **google maps** API key is accessed from string resource google_maps_api.xml in the manifest of the app:

        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/google_maps_key" />

The **gms** credentials (google-services.json) are read directly by the gms libraries when instantiating the gms client on the device in question.

The use of firebase's login UI with the federated login credentials from **google** also require the app's _signing key_ (SHA-1, debug or release/apk)
to be registered at the firebase developer console. During development, this SHA-1 can be obtained from the _.android keystore_ on the development 
machine as described here: [https://developers.google.com/android/guides/client-auth](https://developers.google.com/android/guides/client-auth):

`keytool -list -v -alias androiddebugkey -keystore %USERPROFILE%\.android\debug.keystore
`

Make sure to be using the keytool distributed with Java, at _%JAVA_HOME%/bin_. The default password
for this keystore is _android_. You need to obtain the correct signature **on each development machine
you want to declare to google**, thereby causing it to accept google federated ID requests from the instance
of the app running on this machine (eg. in the emulator). Switching from one dev machine to another
(eg. new computer) requires you to repeat the above steps on the new machine, as the new installation of
Android Studio comes with its own .android keystore and, as such, provides a new signature which has to be 
declared to google/firebase.

You need to register the returned SHA-1 value at the firebase 
developer console ([https://console.firebase.google.com/](https://console.firebase.google.com/)) in the
_project settings_ section of your app. Once declared there, download gms configuration file _google-services.json_ and 
store it in the app folder of your app on your development machine (as described above). The gms client uses
this signature in the communication with google during the firebase UI login workflow.

When deploying to production, use the SHA-1 signature from the _apk/android bundle_ obtained during deployment or (equivalently)
from the google playstore console (where this signature can be found as well).

Federated login credentials can be obtained from **facebook** using a _facebook application ID_ (APP-ID) which declares the 
application at facebook.com as well as a _facebook client token_. During app development, both of these values need to be obtained
from the developer console at facebook.com and provided to the app in its manifest, either as
string resource or via an environment variable:

        <meta-data
            android:name="com.facebook.sdk.ApplicationId"
            android:value="@string/facebook_application_id" />
        <meta-data
            android:name="com.facebook.sdk.ClientToken"
            android:value="${SMOB_FB_CLIENT_TOKEN}"/>

Environment variables can be defined on the development machine and read in the _gradle build file_. There are several
ways to access environment variables in build.gradle. To make an environment variable accessible in the manifest, it can 
be declared as follows:

        // Facebook login: read SMOB_FB_CLIENT_TOKEN key from ENV variable
        manifestPlaceholders = [ SMOB_FB_CLIENT_TOKEN:System.getenv("SMOB_FB_CLIENT_TOKEN") ]

Wherever an environment variable is needed in the source code, the following declaration style can be use (via
the _buildConfig_):

        // API key - read from environment variable
        def smobNetApiKey = System.getenv("SMOB_NET_API_KEY")
        buildConfigField("String", "SMOB_NET_API_KEY", "\"${smobNetApiKey}\"")

        // network base URL - hardcoded in the build file
        buildConfigField "String", "BASE_URL", "\"http://10.0.2.2:3000\""


### Installation

Step by step explanation of how to get a dev environment running.

```
1. To enable Firebase Authentication:
        a. Go to the authentication tab at the Firebase console and enable Email/Password and Google Sign-in methods.
        b. download `google-services.json` and add it to the app.
2. To enable Google Maps:
    a. Go to APIs & Services at the Google console.
    b. Select your project and go to APIs & Credentials.
    c. Create a new api key and restrict it for android apps.
    d. Add your package name and SHA-1 signing-certificate fingerprint.
    c. Enable Maps SDK for Android from API restrictions and Save.
    d. Copy the api key to the `google_maps_api.xml`
3. Run the app on your mobile phone or emulator with Google Play Services in it.
```

## Project Instructions

For further details, please refer to the **[Design document](./doc/Design.md)** as well as the **[App Overview](./doc/SmobOverview.md)**
document included in this repository.

## Built With

* [Koin](https://github.com/InsertKoinIO/koin) - A pragmatic lightweight dependency injection framework for Kotlin.
* [FirebaseUI Authentication](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md) - FirebaseUI provides a drop-in auth solution that handles the UI flows for signing
* lots other packages... see the above-mentioned [App Overview](./doc/SmobOverview.md) document.

## License
