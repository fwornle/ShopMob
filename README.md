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

Note that the API keys associated with these two dependencies have to be obtained separately and placed in the following
folders of the app:

- google-services.json
  - ShopMob/code/app
- google_maps_api.xml
  - ShopMob/code/app/src/**debug**/res/values
  - ShopMob/code/app/src/**release**/res/values

<div style="display: flex; align-items: center; justify-content: space-around;">
  <img alt="Google Services" width="200" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/google_services.PNG" title="Google Services"/>
  <img alt="Google Maps API Key" width="200" src="https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/gms_api_key.PNG" title="Google Maps API Key"/>
</div>

Retrieval of these files is described in many posts on the "international network".

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
