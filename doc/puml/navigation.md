```plantuml
@startuml component
component activity_auth
component activity_mobs
component activity_mobList
component activity_shop
actor mobber
node app
database data

mobber -> app
data <-> app
app -> activity_auth
activity_auth <-> activity_mobs
activity_mobs -> activity_mobList
activity_mobList -> activity_shop
@enduml
```