<link href="design.css" rel="stylesheet" />
# ShopMob Overview

Shopping list management for collaborative shopping trips

---

This document describes the [User Interface] (#user-interface) as well as the [Project Structure] (#project-structure)
of ShopMob.

See [Design] (../Design.md) for more details about the design aspects of the app.

---

## Synopsis

_**ShopMob**_ is an app for collaborative shopping with a group of people - the "shopping mob". 
The app allows "Shop Mobbers" (short: _Mobbers_) to act on a shared shopping list. If one Mobber 
ticks off an item of the list, all other Mobbers in the corresponding group (the _Shop Mob_) get
notified and see their lists updated accordingly.

_ShopMob_ thus facilitates group shopping and helps to reduce the time wasted in day-to-day shopping.

---

### Overview

#### Authentication

![authentication_1](https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/images/sm_login_0.PNG#thumbnail)


#### Planning

#### Detail Screens

#### Shopping





The following five activities make up the principal screens of the app. Within each activity, fragments are used to 
provide sub-views.

1. [Authentication] (#authentication)
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


### Activity Level Architecture

At _activity_ level, ShopMob has the following fundamental architecture:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/architecture_main.md)

### Navigation

At _activity_ and _fragment_ level, the following navigation transitions are possible:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/navigation.md)

### Flow of information

![workflow](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/master/doc/puml/workflow.md)


### Architecture of the SmobUserRepository

The SmobUserRepository has the following architecture:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/smobUserRepository.md)


### Architecture of the Local Database

The Local DB is instantiated as follows:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/localDB.md)

### Architecture of the Koin Service Locator Modules 

The Koin service locator modules have the following architecture:

![main architecture](http://www.plantuml.com/plantuml/proxy?cache=no&src=https://raw.githubusercontent.com/fwornle/ShopMob/main/doc/puml/modulesKoin.md)


---

## Project Planning

This app is the capstone project of a udacity Kotlin/Android nano degree. The
project needs to be submitted by the end of the course. Deliverables are a

- production ready app
- design document (this document)
- evidence of coverage of required design criteria ([rubric](https://review.udacity.com/#!/rubrics/2848/view)))

### Milestone Plan

As such, the following milestones need to have been reached by the submission date of the project.
At the writing of this document (21.12.21), this submission is expected for 31.01.2022.

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
  - [x] DSNG-3.5: Architecture: Data flow modelling (optional)
    - [x] UML diagram of Repository Architecture
    - [x] UML diagram of Koin Service Locator mmodules
    - [x] UML diagram of local DB
    - [x] Sample sequence diagram integrated in design document (puml)
    - [x] Real sequence diagrams
  - [x] DSNG-3.7: Project Milestone planning (this section)
    - [x] Major milestones defined and filled with content
    - [x] Project planning - milestones vs. time
  - [x] [Project rubic](https://review.udacity.com/#!/rubrics/2848/view) coverage evidence
    - [x] Rubic mapping to project milestones (to be sure, all nano degree requirements are covered)

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
        - [x] Refactoring (app): Replace deprecated _JobIntentService_ by recommended _WorkManager_ approach (notification when triggering a geoFence)
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
        - [ ] ~~Refactoring (test): Add unit test and instrumented tests for the network layer~~
    - [x] IMPL-2.5.7: Set-up UI - view models, fragments, RecyclerView handling, LifeData observer, bi-directional data binding
        - [x] Integrate major building blocks for UI layer (from C4)
        - [x] Refactoring (app): Adapt UI to use ShopMob (smob) nomenclature, instead of "reminders" - content still as in C4
        - [x] Refactoring (test): Adjust all integrated UI unit test as well as the instrumented UI tests (from C4)
        - [x] Introduce all activities and fragments as needed by the app - content: dummy placeholders
        - [x] Adjust Activity navigation - all intent based transitions work w/h bundled data
        - [x] Adjust Fragment navigation - all fragment based transitions work w/h bundled data
    - [ ] ~~IMPL-2.5.8: Develop floor plan UI elements (canvas)~~
      - [ ] ~~Design layout view for shop floor plans~~ 
      - [ ] ~~Design data model for storing parameterized shop floor plans (JSON)~~
      - [ ] ~~Integrate shop floor plans in the local DB and the backend DB~~
      - [ ] ~~Integrate major building blocks for advanced UI elements (Canvas based elements, MotionLayout) (from C3, Advanced Android App)~~
      - [ ] ~~Refactoring (app): Adapt advanced UI elements to use ShopMob (smob) nomenclature - content still as in C3~~
      - [ ] ~~Refactoring (test): Add instrumented tests for the advanced UI elements~~
      - [ ] ~~Implement displaying of floor plans including _zones_ with product stats (zone level "overview" display)~~
      - [ ] ~~Implement displaying of "best route through shop", e.g.: zone level --> zone-1 (+ product stats) --> zone-4 (+ stats) --> checkout~~ 
      - [ ] ~~Implement displaying of zone plans including more detailed _sections_ (aisles/shelves) and products per zone~~
      - [ ] ~~Implement displaying of _shelf_ view (image from DB)~~ 
    - [ ] ~~IMPL-2.5.9: Develop floor plan UI elements (canvas)~~
        - [ ] ~~Design layout view for shop floor plans~~
        - [ ] ~~Design data model for storing parameterized shop floor plans (JSON)~~
        - [ ] ~~Integrate shop floor plans in the local DB and the backend DB~~
        - [ ] ~~Implement displaying of floor plans including _zones_ with product stats (zone level "overview" display)~~
        - [ ] ~~Implement displaying of "best route through shop", e.g.: zone level --> zone-1 (+ product stats) --> zone-4 (+ stats) --> checkout~~
        - [ ] ~~Implement displaying of zone plans including more detailed _sections_ (aisles/shelves) and products per zone~~
        - [ ] ~~Implement displaying of _shelf_ view (image from DB)~~
  - [x] IMPL-2.6: App development against local DB
    - [x] Complete activities and fragments (incl. navigation) as required by the app architecture
    - [x] Seed local DB with test data for UI design
    - [x] Complete UI layout of each activity / fragment
    - [x] Extend UI elements to animate them using MotionLayout
    - [x] Complete all view models with required business logic
- [x] IMPL-3: App development against backend DB
  - [x] Set-up development backend DB (json-server)
  - [x] Seed backend DB with test data (same as in IMPL-2.6)
  - [x] Implement CRUD functionality for the backend DB
      - [x] In-principle implementation (one table)
      - [x] Full implementation as needed by the app
  - [x] Implement synchronization between backend and app (WorkManager)
    - [x] In-principle implementation (one table)
    - [x] Full implementation as needed by the app
  - [ ] ~~Push notifications when data has changed at backend level~~
  - [x] Set-up actual backend DB (firebase document DB / AWS Document DB)
- [ ] ~~IMPL-4: Add camera access to take pictures of products and their shelf location~~
  - [ ] ~~Permissions~~
  - [ ] ~~Access and storage in the repository~~
  - [ ] ~~Storage in the local DB~~
  - [ ] ~~Upload to the backend (and reduction in size)~~

### Project Plan

This overview attempts to map the above milestones onto the timeline until submission. Deadline is 31.01.2022 (extended
by one month).

| CW-21/51 | CW-21/52 | CW-22/01 | CW-22/02 | CW-22/03 | CW-22/04 |
|:---------|:---------|:---------|:---------|:---------|:---------|
| DSGN-1 .. DSGN-3, IMPL-1 .. IMPL-2.5.7 | IMPL-2.8 | IMPL-2.5.8 | IMPL-2.5.9 | IMPL-2.6 | IMPL-3 |
| done | done | done | done | done | partially done |

### Project Rubrics

The following specification items need to be included in the app: 

#### Android UI/UX

| Category | Specification Item | Milestone Mapping | Code Link / Evidence |
| ---------------|----------------|----------------------|-----------------|
| Multi-screen UI | At least three screens with distinct features | IMPL-2.6 | RecyclerView lists (several), Edit screens (data entry), Map (location selection), "coming soon" screen (motionLayout) |
| Navigable UI | Navigation via NavController (fragments) or Intents (activities) | IMPL-2.6 | NavController: navigation across all Planning screens, incl. navDrawer; Intent-based navigation between Activities, incl. parameters (bundle), e.g. to detail screens, Administration activity & "coming soon" Shopping activity |
| Navigable UI | Passing of data during navigation via bundles | IMPL-2.6 | Introduced 'NavigationCommand.ToWithBundle' for inter-fragment navigation w/h parameters; Intent-based navigation with bundles, e.g. to the detail screens, etc. |
| UI design/display | UI adheres to Android standards (material theme) | IMPL-2.6 | Themes inherit from standard Material themes (Dark.ActionBar), see Manifest & values/styles.xml |
| UI design/display | UI displays data in an easily consumable way | IMPL-2.6 | Simple, intuitive UI (no frills, just essential functionality) |
| UI design/display | UI displays data using string values, drawables, colors, dimensions | IMPL-2.6 | all over the place - all Strings are pulled out into values/strings.xml; same for dimensions & colors - reconfiguring the look of the app is straight forward |
| UI design/display | UI uses _Constraint Layouts_ with flat UI structure | IMPL-2.6 | Constraint Layout used extensively (all fragments, most activities) |
| UI design/display | Constraint Layouts have IDs and use (at least one) vertical constraint(s) | IMPL-2.6 | Constraints are established in a lean and logical way using the IDs of adjacent views |
| UI design/display | UI display appropriately on screens of different size and resolution| IMPL-2.6 | Tested on Pixel 3 phone, incl. screen rotations, Pixel C tablet |
| UI design/display | UI display uses ViewHolder pattern to load data into the visual areas | IMPL-2.6 | all RecyclerView views use adapters with ViewHolders to load the data - all adapters share a common base adapter with shared list handling functionality. The data sources are Kotlin (State)Flows which are collected in the Binding Adapter. This makes the lists responsive. |
| UI animation/transitions | UI uses MotionLayout to adapt UI elements to a given function | IMPL-2.6 | MotionLayout used on the Shopping "coming soon" screen to advertise the shop floor experience |
| UI animation/transitions | UI defines MotionLayout in a _MotionScene_ using one or more _Transition nodes_ and a _ConstraintSet block_| IMPL-2.6 | MotionLayout using MotionScene, and Transition nodes & ConstraintSet |
| UI animation/transitions | UI uses (custom) parameters to configure the animations | IMPL-2.6 | Custom animated button on ShopMob shop detail screen (leading to shop floor plan) |

#### Local and Network Data

| Category | Specification Item | Milestone Mapping | Code Link / Evidence |
| ---------------|----------------|----------------------|-----------------|
| RESTful API to connect/consume network data | Read access to at least one external resource using retrofit | IMPL-3| full CRUD functionality implemented in (both local DB and) network modules; access to backend to retrieve and write ShopMob (shared) data: shopping lists, products, shops, ... |
| RESTful API to connect/consume network data | Local models & data types, with conversions via Moshi & similar libraries | IMPL-3 | domain specific data types (local DB: DTO, net: NTO, application domain: ATO); Moshi used to serialize/deserialize data |
| RESTful API to connect/consume network data | Network requests are handled off the UI thread to avoid stalling the UI/app | IMPL-3 | Retrofit used, with Coroutines to offload slow network access to the IO thread and keep the UI thread unblocked |
| Load network resources, such as Bitmap Images, dynamically and on-demand | Loads remote resources asynchronously using Glide or similar (Coil) | IMPL-3 | Coil used to load images on detail screens from URLs |
| Load network resources, such as Bitmap Images, dynamically and on-demand | Placeholder images while for loading and failed state | IMPL-3 | Placeholder images used while the image is not yet fully loaded (or unavailable) |
| Load network resources, such as Bitmap Images, dynamically and on-demand | All requests are performed asynchronously and handled on the appropriate threads (Coil) | IMPL-3 | Coil offloads the loading of images to the IO thread (off the UI thread) |
| Store data locally on the device for use between application sessions and/or offline use | Utilizes storage mechanisms that best fit the data stored to store data locally on the device (Room) | IMPL-3 | Room DB used to store local data as well as data retrieved from the backend - SQL table structure possibly not the best choice, as backend uses noSQL, but with data type translations, it can be made work. |
| Store data locally on the device for use between application sessions and/or offline use | Data stored is accessible across user sessions | IMPL-3 | Persistent storage, both on the device (Room DB) as well as sync'ed to the backend |
| Store data locally on the device for use between application sessions and/or offline use | Data is structured with appropriate data types and scope as required by application functionality | IMPL-3 | DTO, NTO and ATO used as domain specific data types, with extension functions for conversion (asDomain, asDatabase, as...) |

#### Android System and Hardware Integration

| Category | Specification Item | Milestone Mapping | Code Link / Evidence |
| ---------|--------------------|-------------------|----------------------|
| MVVM architecture | Separation of responsibilities amongst classes and structures using the MVVM Pattern | IMPL-2 | Views created in Fragments/Activities, ViewModel (VM) includes the business logic - some fragments share a VM to exchange data |
| MVVM architecture | Observer pattern, Activity Contexts, and efficiently utilization of system resources | IMPL-2 | LiveData with 2-way data binding as well as Flow & StateFlow used to observe data changes in the UI layer as well as at local DB level (Room). Lifecycle awareness: ViewModels preserve data in the light of lifecycle changes (eg. navigation to another activity/fragment). Background jobs started/stopped in lifecycle callbacks to stop/reduce network syncs when the app is in the background. |
| Handle and respond to hardware and system events | Use of at least one HW component | IMPL-2 | Location used (map) to set GeoFences, allowing the user to be notified (Notifications) when they are near a shop that sells goods on any of their shopping lists |
| Lifecycle events | Storage/retrieval of data upon LC events | IMPL-2 | Data stored in (and retrieved from) shared ViewModels, to make the data "survive" LC events | 
| Lifecycle events | Handling interactions from/to the app with Intents | IMPL-2 | Notification uses PendingIntend to open the shop details screen (using an intend) from where the user can get to the shop floor as well as launch the app. Intents are also used in the handling of permissions. Finally, bundles/data objects are used to communicate information to activities (intent), fragments (navigate) as well as background jobs (parameters) - wherever there is no shared VM or the LC of the VM is not aligned with the action. |
| Access to system hardware to provide advanced functionality and features | Location (GPS, fused location) | IMPL-2 | Map & GeoFencing are used is used to facilitate the definition of shop entries as well as remind the user about pending shopping activities |
| Access to system hardware to provide advanced functionality and features | Permission handling | IMPL-2 | Permissions are granted in a staged way, honoring the least required permission principle. Permissions are always checked prior to accessing a system resource (stateless). |

---

