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

#### Data Handling Milestones

##### [MS-DH-1] Off-line persistence

During app development, this data model will be created as objects in a local DB,
abstracted by a repository class. As such, the app will be able to access and
manage a persistent data store without the additional burden of fist having to set
up a backend DB and networked data access.

##### [MS-DH-2] Backend persistence

In a second phase, the (firestore) backend and network access classes will be
added. All off-device data will be synchronized with the local DB - the UI will
only ever use data from the local DB. This ensures the smooth working of the app
in areas with poor network connectivity.

### Data Model

#### Collections

Several **collections** will be maintained:

1. Users ("Mobbers")
2. Groups ("ShopMobs")
4. Stores
5. Products
6. Shopping Lists ("smob lists")

#### Documents

**Documents** in each of these collections include the following data:

##### Outline of Data Model

1. Documents in _Users_ include...
   1. UUID
   2. Name
   3. Image (optional //Avatar or picture//)
   4. Shops (list of UUIDs of shops the user has visited //+visiting frequency, +last visited//)
   5. Groups (list of UUIDs of groups the user is affiliated with //+group name, +group activity level//)
   6. Lists (list of UUIDs of shopping lists the user has subscribed to //+list name, +group name, +list activity level//)
2. Documents in _Groups_ include...
   1. UUID
   2. Name
   3. Description (optional - purpose of the group, e.g. "tom's party")
   4. Type (optional - family, department, ad-hoc, ...)
   5. Members (list of UUIDs //+name, +Avatar// of users who are part of this group)
   6. Date of last activity
   7. Frequency of activity
3. Documents in _Stores_ include...
   1. UUID
   2. Name
   3. Description (optional)
   4. Type (chain //shop exists several times//, individual //unique//)
   5. Shop Category (other //= default//, supermarket, drugstore, DIY, clothing, accessories, office supplies,...)
   6. Opening hours (optional - //week days, times//)
4. Documents in _Products_ include...
   1. UUID
   2. Name
   3. Description (optional)
   4. Image (optional)
   5. Product Category (other //= default//, dairy, hardware, supplies, clothes...)
   6. Type (e.g. recurring //groceries, toiletries//, event ["tom's party"], ...
   7. Date of last purchase (by any Mobber)
   8. Frequency of purchase (by any Mobber)
5. Documents in _Smob Lists_ include...
   1. UUID
   2. Name
   3. Description (optional)
   4. Products (list of UUIDs //+name, +p-category//). Per item:
      1. Item: Status (//open <-> in progress <-> done//)
      2. Item: Date and time of last state transition 
   5. Status (list: open <-> in progress <-> done
      1. Level of completion (0% ... 100%)

##### JSON Schema of Database Documents

###### User

```json
{
  "id": "UUID-user",
  "name": {
    "first": "Ace",
    "last": "Ventura"
  },
  "image": "URL-to-profile-picture-or-avatar",
  "shops": [],
  "groups": [],
  "lists": []
}
```

###### Group

```json
{
  "id": "UUID-group",
  "name": "group name",
  "description": "daily groceries",
  "type": "family",
  "members": [
    { "id": "UUID #1", "firstname": "fina #1", "lastname": "lana #1", "image": "URL #1" },
    { "id": "UUID #2", "firstname": "fina #2", "lastname": "lana #2", "image": "URL #2" },
    { }
  ],
  "activity": {
    "date": "date-of-last-change",
    "frequency": "frequency-of-changes"
  }
}
```

###### Store

```json
{
  "id": "UUID-store",
  "name": "store name",
  "description": "",
  "type": "chain|individual",
  "category": "(default)other|supermarket|drugstore|hardware|clothing|accessories|supplies",
  "business": {
    "monday": "09:00 - 12:00, 14:00 - 22:00",
    "tuesday": "09:00 - 12:00, 14:00 - 22:00",
    "wednesday": "09:00 - 12:00",
    "thursday": "09:00 - 12:00, 14:00 - 22:00",
    "friday": "09:00 - 12:00, 14:00 - 22:00",
    "saturday": "09:00 - 12:00, 14:00 - 18:00",
    "sunday": "closed"
  }
}
```

###### Product

```json
{
  "id": "UUID-product",
  "name": "product name",
  "description": "lactose free",
  "image": "URL",
  "category": {
    "main": "(default)other|foods|hardware|supplies|clothing",
    "sub": "{(default)none|fruit-n-vegetables|bread|dairy|frozen|cans|beverages}|{...}"
  },
  "type": "recurring|event",
  "activity": {
    "date": "date-of-last-purchase",
    "frequency": "frequency-of-purchase"
  }
}
```

###### Smob List

```json
{
  "id": "UUID-list",
  "name": "smob list name",
  "description": "our daily groceries",
  "products": {
    "(cat)fruit-n-veg": [
      { "id": "UUID #1", "name": "item #1", "image (opt)": "URL #1" },
      { "id": "UUID #2", "name": "item #2", "image (opt)": "URL #2" },
      { "id": "UUID #3", "name": "item #3", "image (opt)": "URL #3" },
      { }
    ],
    "(cat)dairy": [
      { "id": "UUID #1", "name": "item #1", "image (opt)": "URL #1" },
      { "id": "UUID #2", "name": "item #2", "image (opt)": "URL #2" },
      { "id": "UUID #3", "name": "item #3", "image (opt)": "URL #3" },
      { }
    ],
    "etc.": []
  },
  "lifecycle": {
    "state": "open",
    "completion": "0"
  }
}
```

---

## Architecture

The app includes several _activities_ to separate principal collections of use cases
from one another. Each of these principal activities featues a number of _fragments_
to further separate distinct aspects of each phase of the flow. Navigation between activities is done by Intents. Navigation between fragments is done
using a NavController instance.

### Overview

1. Authentication
   1. Login & Sign-up with email/password
   2. Federated login provider (google)
2. Data Management
   1. User management
   2. Shop management
   3. Product management
   4. Shopping list management
3. Shopping
   1. Smob List item view (RecyclerView)
   2. Store details (floor plan with route and zones + stats of shopping items in each zone)
   3. Zone details (location of items in zone)
   4. Section details (image of shelf with location of selected shopping item on shelf)

### Activity Level Architecture

At _activity_ level, ShopMob has the following fundamental architecture:

```plantuml
@startuml component
component activity_auth
component activity_list
component activity_shop
actor mobber
node app_main
node service_provider
node repo
database DB
database net

mobber -> app_main
DB <-> repo
net <-> repo
repo <-> service_provider
service_provider <-> app_main
app_main -> activity_auth
activity_auth -> activity_list
activity_auth <- activity_list
activity_list -> activity_shop
activity_list <- activity_shop
activity_shop -> activity_auth
@enduml
```

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/architecture_main.puml)

### Activity Level Architecture

At _activity_ level, ShopMob has the following fundamental architecture:

```plantuml
@startuml component
component activity_auth
component activity_list
component activity_shop
actor mobber
node app_main
node service_provider
node repo
database DB
database net

mobber -> app_main
DB <-> repo
net <-> repo
repo <-> service_provider
service_provider <-> app_main
app_main -> activity_auth
activity_auth -> activity_list
activity_auth <- activity_list
activity_list -> activity_shop
activity_list <- activity_shop
activity_shop -> activity_auth
@enduml
```

### Detailed Flow

![workflow test](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/master/doc/puml/workflow.puml)

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
    - [ ] Fragments and fragment navigation
  - [x] DSNG-3.5: Architecture: Internal app
    - [ ] Data source abstraction via a repository class
    - [ ] Service provider pattern
    - [ ] Model View / View Model (MVVM) 
  - [ ] DSNG-3.5: Data flow modelling (optional)
    - [x] Sample sequence diagram integrated in design document (puml)
  - [ ] DSNG-3.7: Project Milestone planning (this section)
    - [x] Major milestones defined and filled with content
    - [ ] Project planning - milestones vs. time
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
        - [ ] Refactoring (app): Make notification support an extension function of the Context (as proposed by the reviewers)
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
        - [ ] Integrate major building blocks of the network layer (from C2, Asteroids), e.g. retrofit, moshi converters
        - [ ] Refactoring (app): Adapt network modules to use ShopMob (smob) nomenclature, instead of "asteroids" - content still as in C2
        - [ ] Refactoring (test): Add unit test and instrumented tests for the network layer
    - [x] IMPL-2.5.7: Set-up UI - view models, fragments, RecyclerView handling, LifeData observer, bi-directional data binding
        - [x] Integrate major building blocks for UI layer (from C4)
        - [x] Refactoring (app): Adapt UI to use ShopMob (smob) nomenclature, instead of "reminders" - content still as in C4
        - [x] Refactoring (test): Adjust all integrated UI unit test as well as the instrumented UI tests (from C4)
        - [ ] Integrate major building blocks for advanced UI elements (Canvas based elements, MotionLayout) (from C3, Advanced Android App)
        - [ ] Refactoring (app): Adapt advanced UI elements to use ShopMob (smob) nomenclature - content still as in C3
        - [ ] Refactoring (test): Add instrumented tests for the advanced UI elements
    - [ ] IMPL-2.5.8: Develop floor plan UI elements (canvas)
      - [ ] Design layout view for shop floor plans 
      - [ ] Design data model for storing parameterized shop floor plans (JSON)
      - [ ] Integrate shop floor plans in the local DB and the backend DB
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
    - [ ] Seed local DB with test data for UI design
    - [ ] Complete UI layout of each activity / fragment
    - [ ] Extend UI elements to animate them using MotionLayout
    - [ ] Complete all view models with required business logic
- [ ] IMPL-3: App development against backend DB
  - [ ] Set-up backend DB (firebase document DB)
  - [ ] Seed backend DB with test data (same as in IMPL-2.6)
  - [ ] Implement CRUD functionality for the backend DB
  - [ ] Implement synchronization between backend and app (WorkManager)
  - [ ] Push notifications when data has changed at backend level
- [ ] IMPL-4: Add camera access to take pictures of products and their shelf location
  - [ ] Permissions
  - [ ] Access and storage in the repository
  - [ ] Storage in the local DB
  - [ ] Upload to the backend (and reduction in size)

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
| RESTful API to connect/consume network data | ... | IMPL-3|
| RESTful API to connect/consume network data | ... | IMPL-3 |

#### Android System and Hardware Integration

| Category | Specification Item | Milestone Mapping |
| ---------------|----------------|----------------------|
| MVVM architecture | ... | IMPL-2|
| MVVM architecture | ... | IMPL-2 |

---

