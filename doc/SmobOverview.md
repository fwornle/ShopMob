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

---

### Project Workspace

#### General Overview

The workspace of the app's main code base is organized in the following main sections:

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


#### User Interface

#### GeoFencing

#### Background Work

#### Utilities






