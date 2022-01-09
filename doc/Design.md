# ShopMob

Shopping list management for collaborative shopping trips

---

This document describes the [Use Cases](#use-cases), [Features](#features)
and [Architecture](#architecture) of the app to be developed.

See [README](../README.md) for an outline of the app.

---

## Synopsis

_**ShopMob**_ is an app for collaborative shopping with a group of people - the "shopping mob". 
The app allows "Shop Mobbers" (short: _Mobbers_) to act on a shared shopping list. If one Mobber 
ticks off an item of the list, all other Mobbers in the corresponding group (the _Shop Mob_) get
notified and see their lists updated accordingly.

_ShopMob_ thus facilitates group shopping and helps to reduce the time wasted in day-to-day shopping.

---

## Use Cases

ShopMob is designed to assist with the following use cases:

1. As a user of the app, I would like to join one or more shopping groups - the "Shop Mobs"
2. As a Shop Mop member - a Mobber - I would like to see who else is in my Shop Mob
3. As a Mobber, I would like to see what shopping lists have been defined for each of my Shop Mobs
4. As a Mobber, I would like to see what is on each of the shopping lists of each of my Shop Mobs
5. As a Mobber, I would like to add a shopping list to any of my Shop Mobs
6. As a Mobber, I would like to delete a shopping list I own (created by myself)
7. As a Mobber, I would like to add (remove) items to (from) any of the shopping lists of my Shop Mobs
8. As a Mobber, I would like to classify items on the list by product categories (e.g. "dairy", "appliances", ...)
9. As a Mobber, I would like to attribute list items to classes of shops (e.g. "supermarket", "hardware store", ...)
10. As a Mobber, I would like to attribute list items to specific shops from a list of known shops
11. As a Mobber, I would like to add shops to the list of known shops by clicking on their POI on the map
12. As a Mobber, I would like to remove shops from the list of know shops using the app
13. As a Mobber, I would like to receive a notification when a fellow Mobber has added (removed/ticked off) an item to (
    from) the list
14. As a Mobber, I would like to receive a notification when I am near a shop where an item of one of the lists I
    subscribed to can be purchased
15. As a Mobber, I would like to see a floor plan of the shop I am about to enter (/have entered)
16. As a Mobber, I would like to see a route through the shop, taking me to all items on my list which can be found in
    this shop
17. As a Mobber, I would like to see where in the shop zones for specific product categories are located (relating to
    the items on my shopping list)
18. As a Mobber, I would like to see an image of each 'zone of relevance', i.e. a zone in which any of my shopping list
    items can be found (rough localization)
19. As a Mobber, I would like to see an image of the section inside a zone where my shopping list item can be found (
    fine localization)
20. As a Mobber, I would like to get live updates when items on my list get modified by fellow Mobbers (
    added/removed/ticked off)

---

## Features

The app shall have a number of features:

1. Android UI/UX
   1. Authentication using the firebaseUI library
       1. Login / Sign-up using email & password
       2. Federated authentication using Google
   2. A UI with several screens (Android activities & fragments)
       1. Authentication screen (activity)
       2. Shopping list screen (activity)
           1. Fragment for administration of all Shop Mobs (subscribe, unsubscribe, sort by city)
           2. Fragment for administration of all shopping lists of an individual Shop Mob (create list, delete list)
           3. Fragments for administration of shops in my area
              1. Adding a shop shall be done by clicking on POIs on a map
              2. Removal of a shop from list of know shops
           4. Fragment for administration of an individual shopping list (add/remove items, categorize, attribute to shops)
           5. Fragment for the displaying of and interaction with a shopping list (viewing, sorting, filtering, ticking off
              items)
              1. A RecyclerView (RV) list shall be used to display shopping list items
              2. MotionLayout animations shall be used to support the user experience
                  1. Swiping left/right on a shopping list item shall mark it as 'ticked off' / 'active'
                  2. Swiping far left on a shopping list item shall delete the item
           6. Fragment with product details
              1. Access to camera to take picture of items
              2. Access to camera to take picture of shelf location
       3. Shop screen (activity)
          1. Fragment with zones of relevance and the fastest route through the shop
          2. Fragment with zone details (sections) and where the items can be found

2. Local and Network data
   1. Centralized data store on a firebase network storage
       1. This feature is essential to the sharing of the list with fellow Mobbers
       2. A WorkManager job shall be used to manage the synchronization with the firebase backend
   2. Repository: Data source abstraction - all online data shall be stored in a local database (DB) prior to being used in
      the app
       1. This allows the app to be used when inside a shop with poor/no network connectivity

3. Android System and HW Integration
   1. Interaction with Android and/or device HW
      1. Access to Google Maps as UI element for POI selection (shops)
      2. Access to the camera to take a picture of a product (enhance shopping list)
      3. Sending and receiving of notification
   2. Responding to and handling of HW and system events
      1. Life cycle events due to change of orientation and putting app into background 
      2. Permission handling
   

---

## Data storage

The app will be using a cloud based backend database to store the 
information about users, groups, ShopMob (smob) shopping lists, 
store information (floor plan, zone map and product map) and 
product information (images, description, categories). 

### Data caching

A firestore document DB will be used to provide this functionality. A local
DB on the device will provide a cache, mirroring the backend DB. Synchronization
between these two DBs will be done by worker jobs. 

#### Data Handling - Repositories

During app development, this data model will be created as objects in a local DB,
abstracted by a repository class. As such, the app will be able to access and
manage a persistent data store without the additional burden of fist having to set
up a backend DB and networked data access.


In a second phase, a cloud-based backend and network access classes will be
added. All off-device data will be synchronized with the local DB - the UI will
only ever use data from the local DB. This ensures the smooth working of the app
in areas with poor network connectivity.

### Data Model

#### Collections

Several **collections** will be maintained:

1. Users ("Mobbers")
2. Groups ("ShopMobs")
3. Stores
4. Products 
5. Lists ("smob community shopping lists")

#### Documents

**Documents** in each of these collections include the following data:

##### Outline of Data Model

Each table includes nothing but the data which truly belongs to it. Optional properties are _nullable_. This may not be
the optimal structure for a scalable noSQL database within a backend (as it increases the number of queries needed to 
collect all data a specific UI screen / piece of business logic might need), but it simplifies the maintenance of 
consistent data records (as there is no need for unnecessary synchronization between different tables with partially 
redundant information). This may be optimized for scalability at a later stage.

1. Documents in _Users_ include...
   1. UUID
   2. Username
   3. Name (firstname, lastname)
   4. Email
   5. Image (optional //Avatar or picture//)
2. Documents in _Groups_ include...
   1. UUID
   2. Name
   3. Description (optional - purpose of the group, e.g. "tom's party")
   4. Type (optional - family, department, ad-hoc, ...)
   5. Members (list of UUIDs //+name, +Avatar// of users who are part of this group)
   6. Date of last activity
3. Documents in _Stores_ include...
   1. UUID
   2. Name
   3. Description (optional)
   4. Longitude
   5. Latitude
   6. Type (chain //shop exists several times//, individual //unique//)
   7. Shop Category (other //= default//, supermarket, drugstore, DIY, clothing, accessories, office supplies,...)
   8. Opening hours (optional - //week days, times//)
4. Documents in _Products_ include...
   1. UUID
   2. Name
   3. Description (optional)
   4. Image (optional)
   5. Product Category
      1. Main (other //= default//, foods, hardware, supplies, clothes, ...)
      2. Sub (other //= default//, dairy, bread, fruit_vegetable, canned_food, beverages, ...)
   6. Activity
      1. Date of last purchase (by any Mobber)
      2. Repetitions of purchase (by any Mobber, since creation of this product)
5. Documents in _Smob Lists_ include...
   1. UUID
   2. Name
   3. Description (optional)
   4. Products (list of UUIDs //+name, +p-category//). Per item:
      1. Item: Status (//open <-> in progress <-> done//)
      2. Item: Date and time of last state transition
   5. Mobbers (list of user UUIDs - the mobbers who are sharing this list)
   6. List lifecycle
      1. Aggregated state of the list (open <-> in progress <-> done)
      2. Level of completion of the list (0% ... 100%)


##### Data Schemata of Backend stored Data Items

The backend offers persistent data storage using noSQL databases. Used correctly, this promises fast queries even at 
scale (incl. distributed storage). The following independent data sets have been defined as backend storage items:

###### User

The _smobUser_ entries of the _smobUsers_ table adheres to the following schema:

```json
{
  "id": "UUID-user",
  "username": "username",
  "name": "name",
  "email": "max@mustermann.com",
  "imageUrl": "URL-to-profile-picture-or-avatar"
}
```

###### Group

The _smobGroup_ entries of the _smobGroups_ table adheres to the following schema:

```json
{
  "id": "UUID-group",
  "name": "group name",
  "description": "daily groceries",
  "type": "(default)OTHER|FAMILY|FRIENDS|WORK",
  "members": [
    "userId1",
    "userId2",
    "userId3",
    "..."
  ],
  "activity": {
    "date": "date-of-last-change",
    "reps": 0
  }
}
```

###### Product

The _smobProduct_ entries of the _smobProducts_ table adheres to the following schema:

```json
{
  "id": "UUID-product",
  "name": "product name",
  "description": "lactose free",
  "imageUrl": "URL",
  "category": {
    "main": "(default)OTHER|FOODS|HARDWARE|SUPPLIES|CLOTHING",
    "sub": "{(default)NONE|DAIRY|BREAD|BREKKY|FRUIT_VEGETABLE|CANNED_FOOD|BEVERAGES|IY|TOOLS|...}"
  },
  "activity": {
    "date": "date-of-last-purchase",
    "repetitions": 17
  }
}
```

###### Store

The _smobStore_ entries of the _smobStores_ table adheres to the following schema:

```json
{
  "id": "UUID-store",
  "name": "store name",
  "description": "it's a good-e store",
  "imageUrl": "a pretty store",
  "location": {
    "latitude": "where the shop is",
    "longitude": "where the shop is"
  },
  "type": "CHAIN|INDIVIDUAL",
  "category": "(default)OTHER|SUPERMARKET|DRUGSTORE|HARDWARE|CLOTHING|ACCESSORIES|SUPPLIES",
  "business": [
    "09:00 - 12:00, 14:00 - 22:00",
    "09:00 - 12:00, 14:00 - 22:00",
    "09:00 - 12:00",
    "09:00 - 12:00, 14:00 - 22:00",
    "09:00 - 12:00, 14:00 - 22:00",
    "09:00 - 12:00, 14:00 - 18:00",
    "closed"
  ]
}
```

###### Smob List

The _smobList_ entries of the _smobLists_ table adheres to the following schema:

```json
{
  "id": "UUID-list",
  "name": "smob list name",
  "description": "our daily groceries",
  "items": [
    { "id": "productId1", "status": "OPEN|IN_PROGRESS|DONE|DELETED" },
    { "id": "productId2", "status": "OPEN|IN_PROGRESS|DONE|DELETED" },
    { "id": "productId3", "status": "OPEN|IN_PROGRESS|DONE|DELETED" },
    { }
  ],
  "members": [
    "userId1",
    "userId2",
    "userId3",
    "..."
  ],
  "lifecycle": {
    "status": "OPEN|IN_PROGRESS|DONE|DELETED",
    "completion": 35
  }
}
```

##### Local DB Schemata of Database Tables

The local DB is a mySQL database (DB). As such, all potential query items should be modelled as separate fields (as
opposed to nested structures, etc.)


#### Data Type Transformations

For each persistently stored item, the app defines **three sets of data representations**:

- <<item>>NTO
    - Network Transfer Object (the datatype of an item as stored in the backend)
    - the moshi JSON converters work with NTOs
- <<item>>DTO
    - Database Transfer Object (the datatype of an item as stored in the local DB)
    - the room JSON converters work with DTOs
    - this datatype is also used as repository level datatype.
- <<item>>ATO
    - Application Transfer Object (domain datatype, "above" the repository, the app works with this storage independent datatype)

Both scalar and array type variants of these data objects can be converted from/to one another:


##### Datatype Converter asRepoModel()

... turns an NTO into a DTO.

##### Datatype Converter asNetworkModel()

... turns a DTO into an NTO.

##### Datatype Converter asDomainModel()

... turns a DTO into ATO.

##### Datatype Converter asDatabaseModel()

... turns an ATO into a DTO.


---

## Architecture

The app includes several _activities_ to separate principal collections of use cases
from one another. Each of these principal activities featues a number of _fragments_
to further separate distinct aspects of each phase of the flow. Navigation between activities is done by Intents. Navigation between fragments is done
using a NavController instance.

### Overview

The following five activities make up the principal screens of the app. Within each activity, fragments are used to 
provide sub-views.

1. Authentication
   1. Login & Sign-up with email/password
   2. Federated login provider (google)
2. Administration
   1. User management
   2. Shop management
   3. Product management
   4. Shopping list management
3. Planning Lists
   1. Smob lists view (RecyclerView, RV) - all smob lists which are available to the user
   2. Smob product list view (RV) - an individual smob list with product details (= "shopping items")
   3. Smob shop list view (RV) - all shops which offer at least one product on any of the lists available to the user
4. Details
   1. Product details
   2. Shop details
5. Shopping
   1. Store details (floor plan with route and zones + stats of shopping items in each zone)
   2. Zone details (location of items in zone)
   3. Section details (image of shelf with location of selected shopping item on shelf)


### Architecture of the SmobUserRepository

The SmobUserRepository has the following architecture:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/smobUserRepository.md)


### Architecture of the Local Database

The Local DB is instantiated as follows:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/localDB.md)

### Architecture of the Koin Service Locator Modules 

The Koin service locator modules have the following architecture:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/modulesKoin.md)


### Activity Level Architecture

At _activity_ level, ShopMob has the following fundamental architecture:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/architecture_main.md)

### Navigation

At _activity_ and _fragment_ level, the following navigation transitions are possible:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/navigation.md)


### Detailed Flow

![workflow test](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/master/doc/puml/workflow.md)

---

## Project Planning

This app is the capstone project of a udacity Kotlin/Android nano degree. The
project needs to be submitted by the end of the course. Deliverables are a

- production ready app
- design document (this document)
- evidence of coverage of required design criteria ([rubric](https://review.udacity.com/#!/rubrics/2848/view)))

### Milestone Plan

As such, the following milestones need to have been reached by the submission date of the project.
At the writing of this document (21.12.21), this submission is expected for 
31.01.2022.

#### Design

- [x] DSGN-1: Brainstorm app idea and write down idea and principal use cases
  - [x] Purpose: why this app?
  - [x] Name of the app (ShopMob)
  - [x] Naming of essential building blocks to allow for consistent naming and clean code 
- [x] DSGN-2: Set-up project at GitHub
  - [x] DSGN-2.1: Create new repository
  - [x] DSGN-2.2: Initial commit: README & Design document
  - [x] DSGN-2.3: PlantUML diagram support - commit example and link to design document ("fit for GH --> no-cache indirection via puml server)
- [x] DSNG-3: Write design document (this document)
  - [x] DSNG-3.1: Synopsis - what this app is all about
  - [x] DSNG-3.2: Describe Use Cases
  - [x] DSNG-3.3: Design Data Model, taking into consideration the noSQL structure of the backend data storage (& potential scalability concerns)
    - [x] Pseudo-code modelling
    - [x] JSON schema modelling
  - [x] DSNG-3.4: Architecture: Customer visible
    - [x] Activities and activity navigation
    - [x] Fragments and fragment navigation
  - [x] DSNG-3.5: Architecture: Internal app
    - [x] Data source abstraction via a repository class(es)
      - [x] Multi-table DB: One DTO/DAO per table
      - [x] One repository class per table (/DTO/DAO) - to maintain modularity / improve SW quality & testability
    - [x] Service Locator pattern (using _Koin_)
      - [x] Modularize Koins: DB, NET, REPO, VM
    - [x] Model View / View Model (MVVM)
      - [x] move all LiveData to VM (from Repo - replace loading status by Resource wrappers)
      - [x] Migrate from _LiveData_ to _Flow_
  - [ ] DSNG-3.5: Architecture: Data flow modelling (optional)
    - [x] UML diagram of Repository Architecture
    - [x] UML diagram of Koin Service Locator mmodules
    - [x] UML diagram of local DB
    - [x] Sample sequence diagram integrated in design document (puml)
    - [ ] Real sequence diagrams
  - [x] DSNG-3.7: Project Milestone planning (this section)
    - [x] Major milestones defined and filled with content
    - [x] Project planning - milestones vs. time
  - [ ] [Project rubic](https://review.udacity.com/#!/rubrics/2848/view) coverage evidence
    - [ ] Rubic mapping to project milestones (to be sure, all nano degree requirements are covered)

#### Implementation

- [x] IMPL-1: Create initial 'Android' project with 'working set' of expected (up-to-date) dependencies
  - [x] Structure gradle.build (app) and gradle.build (project) to include  
- [x] IMPL-2: Integrate and adjust base set of required modules from last course exercise (location reminder) to provide a fundamental structure of the app:
  - [x] IMPL-2.1: Manifest with compatibility information, activities and permissions
    - Compatibility information (API levels)
    - activities
      - Authentication
      - Main activity (with fragments)
      - Details activity (triggered "from outside", e.g. by a notification event)
    - Permissions for location access (foreground / background)
    - Permissions for internet access
  - [x] IMPL-2.2: Application module with Koin service provider for...
    - Application context
    - Repository service (using the location reminders example as basis)
    - View models (using the location reminders example as basis - incl. generalized functionality within a base view model)
  - [x] IMPL-2.3: Test projects (migration from last course work - location reminder)
    - [x] Unit tests
    - [x] Instrumented tests
  - [x] IMPL-2.4: Activities and Fragments to have some fundamental navigation elements set up
    - [x] Configure initial fragment navigation via a Androidx navController
  - [x] IMPL-2.5: Integrate major building blocks from the course exercises (where needed for this app):
    - [x] IMPL-2.5.1: Set-up firebase project for this app
      - [x] Retrieve configuration file (w/h API keys - not to be committed to the GH repo --> .gitignore)
      - [x] Configure firebase authentication for email & google
    - [x] IMPL-2.5.2: Generate new Google Maps API key (debug) 
      - [x] Install API key in the corresponding configuration files (not to be committed to the GH repo --> .gitignore)
      - [x] Integrate map-based location selection from C4 (location reminder)
      - [x] Refactoring (app): Adapt map support to use ShopMob (smob) nomenclature, instead of "reminders" - logic still as in C4
      - [x] Refactoring (app): Adjust permission handling to use ShopMob (smob) nomenclature, instead of "reminders" - logic still as in C4
      - [x] Refactoring (test): Adjust all integrated instrumented map handling tests (from C4)
    - [x] IMPL-2.5.3: GeoFencing and notification support / geoFencing notifications
        - [x] Integrate geoFencing modules from C4 (location reminder)
        - [x] Refactoring (app): Adapt geoFencing support to use ShopMob (smob) nomenclature, instead of "reminders" - logic still as in C4
        - [x] Refactoring (app): Adjust permission handling to use ShopMob (smob) nomenclature, instead of "reminders" - logic still as in C4
        - [x] Refactoring (test): Adjust all integrated instrumented geoFencing tests (from C4)
        - [x] Refactoring (app): Make notification support an extension function of the Context (as proposed by the reviewers)
        - [ ] Refactoring (app): Replace deprecated _JobIntentService_ by recommended _WorkManager_ approach (notification when triggering a geoFence)
    - [x] IMPL-2.5.4: Integrate authentication support (based on building block from location reminders)
      - [x] Integrate user authentication from C4 (location reminder)
      - [x] Refactoring (app): Adapt authentication support to use ShopMob (smob) nomenclature, instead of "reminders" - logic still as in C4
      - [x] Adjust instrumented "end-to-end" (e2e) test with UI Automator to test login functionality
    - [x] IMPL-2.5.5: Set-up local database (loc-DB) and abstraction through repository class
      - [x] Integrate building blocks from course work modules (C4, location reminder)
      - [x] Refactoring (app): Adapt loc-DB to use ShopMob (smob) nomenclature, instead of "reminders" - data records still as in C4
      - [x] Refactoring (test): Adjust all integrated unit test as well as the instrumented DAO and repository tests (from C4)
    - [ ] IMPL-2.5.6: Set-up network access modules and integrate them into the repository
        - [x] Integrate major building blocks of the network layer (from C2, Asteroids), e.g. retrofit, moshi converters
        - [x] Refactoring (app): Adapt network modules to use ShopMob (smob) nomenclature, instead of "asteroids" - content still as in C2
        - [ ] Refactoring (test): Add unit test and instrumented tests for the network layer
    - [x] IMPL-2.5.7: Set-up UI - view models, fragments, RecyclerView handling, LifeData observer, bi-directional data binding
        - [x] Integrate major building blocks for UI layer (from C4)
        - [x] Refactoring (app): Adapt UI to use ShopMob (smob) nomenclature, instead of "reminders" - content still as in C4
        - [x] Refactoring (test): Adjust all integrated UI unit test as well as the instrumented UI tests (from C4)
        - [ ] Introduce all activities and fragments as needed by the app - content: dummy placeholders
        - [x] Adjust Activity navigation - all intent based transitions work w/h bundled data
        - [x] Adjust Fragment navigation - all fragment based transitions work w/h bundled data
    - [ ] IMPL-2.5.8: Develop floor plan UI elements (canvas)
      - [ ] Design layout view for shop floor plans 
      - [ ] Design data model for storing parameterized shop floor plans (JSON)
      - [ ] Integrate shop floor plans in the local DB and the backend DB
      - [ ] Integrate major building blocks for advanced UI elements (Canvas based elements, MotionLayout) (from C3, Advanced Android App)
      - [ ] Refactoring (app): Adapt advanced UI elements to use ShopMob (smob) nomenclature - content still as in C3
      - [ ] Refactoring (test): Add instrumented tests for the advanced UI elements
      - [ ] Implement displaying of floor plans including _zones_ with product stats (zone level "overview" display)
      - [ ] Implement displaying of "best route through shop", e.g.: zone level --> zone-1 (+ product stats) --> zone-4 (+ stats) --> checkout 
      - [ ] Implement displaying of zone plans including more detailed _sections_ (aisles/shelves) and products per zone
      - [ ] Implement displaying of _shelf_ view (image from DB) 
    - [ ] IMPL-2.5.9: Develop floor plan UI elements (canvas)
        - [ ] Design layout view for shop floor plans
        - [ ] Design data model for storing parameterized shop floor plans (JSON)
        - [ ] Integrate shop floor plans in the local DB and the backend DB
        - [ ] Implement displaying of floor plans including _zones_ with product stats (zone level "overview" display)
        - [ ] Implement displaying of "best route through shop", e.g.: zone level --> zone-1 (+ product stats) --> zone-4 (+ stats) --> checkout
        - [ ] Implement displaying of zone plans including more detailed _sections_ (aisles/shelves) and products per zone
        - [ ] Implement displaying of _shelf_ view (image from DB)
  - [ ] IMPL-2.6: App development against local DB
    - [ ] Complete activities and fragments (incl. navigation) as required by the app architecture
    - [x] Seed local DB with test data for UI design
    - [ ] Complete UI layout of each activity / fragment
    - [ ] Extend UI elements to animate them using MotionLayout
    - [ ] Complete all view models with required business logic
- [ ] IMPL-3: App development against backend DB
  - [x] Set-up development backend DB (json-server)
  - [x] Seed backend DB with test data (same as in IMPL-2.6)
  - [x] Implement CRUD functionality for the backend DB
      - [x] In-principle implementation (one table)
      - [x] Full implementation as needed by the app
  - [x] Implement synchronization between backend and app (WorkManager)
    - [x] In-principle implementation (one table)
    - [x] Full implementation as needed by the app
  - [ ] Push notifications when data has changed at backend level
  - [ ] Set-up actual backend DB (firebase document DB / AWS Document DB)
- [ ] IMPL-4: Add camera access to take pictures of products and their shelf location
  - [ ] Permissions
  - [ ] Access and storage in the repository
  - [ ] Storage in the local DB
  - [ ] Upload to the backend (and reduction in size)

### Project Plan

This overview attempts to map the above milestones onto the timeline until submission. Deadline is 31.01.2022 (extended
by one month).

| CW-21/51 | CW-21/52 | CW-22/01 | CW-22/02 | CW-22/03 | CW-22/04 |
|:---------|:---------|:---------|:---------|:---------|:---------|
| DSGN-1 .. DSGN-3, IMPL-1 .. IMPL-2.5.7 | IMPL-2.8 | IMPL-2.5.8 | IMPL-2.5.9 | IMPL-2.6 | IMPL-3 |
| done | done | in progress | open | open | open |

### Project Rubrics

The following specification items need to be included in the app: 

#### Android UI/UX

| Category | Specification Item | Milestone Mapping |
| ---------------|----------------|----------------------|
| Multi-screen UI | At least three screens with distinct features | IMPL-2.6 |
| Navigable UI | Navigation via NavController (fragments) or Intents (activities) | IMPL-2.6 |
| Navigable UI | Passing of data during navigation via bundles | IMPL-2.6 |
| UI design/display | UI adheres to Android standards (material theme) | IMPL-2.6 |
| UI design/display | UI displays data in an easily consumable way | IMPL-2.6 |
| UI design/display | UI displays data using string values, drawables, colors, dimensions | IMPL-2.6 |
| UI design/display | UI uses _Constraint Layouts_ with flat UI structure | IMPL-2.6 |
| UI design/display | Constraint Layouts have IDs and use (at least one) vertical constraint(s) | IMPL-2.6 |
| UI design/display | UI display appropriately on screens of different size and resolution| IMPL-2.6 |
| UI design/display | UI display uses ViewHolder pattern to load data into the visual areas | IMPL-2.6 |
| UI animation/transitions | UI uses MotionLayout to adapt UI elements to a given function | IMPL-2.6 |
| UI animation/transitions | UI defines MotionLayout in a _MotionScene_ using one or more _Transition nodes_ and a _ConstraintSet block_| IMPL-2.6 |
| UI animation/transitions | UI uses (custom) parameters to configure the animations | IMPL-2.6 |

#### Local and Network Data

| Category | Specification Item | Milestone Mapping |
| ---------------|----------------|----------------------|
| RESTful API to connect/consume network data | Read access to at least one external resource using retrofit | IMPL-3|
| RESTful API to connect/consume network data | Local models & data types, with conversions via Moshi & similar libraries | IMPL-3 |
| RESTful API to connect/consume network data | Network requests are handled off the UI thread to avoid stalling the UI/app | IMPL-3 |
| Load network resources, such as Bitmap Images, dynamically and on-demand | Loads remote resources asynchronously using Glide or similar (Coil) | IMPL-3 |
| Load network resources, such as Bitmap Images, dynamically and on-demand | Placeholder images while for loading and failed state | IMPL-3 |
| Load network resources, such as Bitmap Images, dynamically and on-demand | All requests are performed asynchronously and handled on the appropriate threads (Coil) | IMPL-3 |
| Store data locally on the device for use between application sessions and/or offline use | Utilizes storage mechanisms that best fit the data stored to store data locally on the device (Room) | IMPL-3: Room, SQL DB |
| Store data locally on the device for use between application sessions and/or offline use | Data stored is accessible across user sessions | IMPL-3: Room, persistent storage |
| Store data locally on the device for use between application sessions and/or offline use | Data is structured with appropriate data types and scope as required by application functionality | IMPL-3: DTO, NTO, ATO - plus conversions |



#### Android System and Hardware Integration

| Category | Specification Item | Milestone Mapping |
| ---------------|----------------|----------------------|
| MVVM architecture | Separation of responsibilities amongst classes and structures using the MVVM Pattern | Views via Fragments/Activities, ViewModel: business logic | IMPL-2 |
| MVVM architecture | Observer pattern, Activity Contexts, and efficiently utilization of system resources | LifeData, StateFlow, LifeCycle awareness, IMPL-2 |
| Handle and respond to hardware and system events | Use of at least one HW component | IMPL-2: Location (map, geofence), Notifications |
| Lifecycle events | Storage/retrieval of data upon LC events | IMPL-2: (Bundles) **TBD**|
| Lifecycle events | Handling interactions from/to the app with Intents | IMPL-2: Activities & return values |
| Access to system hardware to provide advanced functionality and features | Location (GPS, fused location) | IMPL-2: Map & GeoFencing |
| Access to system hardware to provide advanced functionality and features | Permission handling | IMPL-2: Staged permissions, least required principle |

---

