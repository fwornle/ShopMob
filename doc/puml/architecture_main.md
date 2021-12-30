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