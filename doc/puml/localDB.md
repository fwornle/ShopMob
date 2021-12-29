```plantuml
@startuml

class RoomDatabase #lightgray ##gray

abstract SmobDatabase #palegreen ##[dashed]green implements RoomDatabase {
  {abstract} mobItemDao(): SmobItemDao
  {abstract} **smobUserDao(): SmobUserDao**
  {abstract} smobGroupDao(): SmobGroupDao
  {abstract} smobShopDao(): SmobShopDao
  {abstract} smobProductDao(): SmobProductDao
  {abstract} smobListDao(): SmobListDao
}

annotation Database #pink;line:red;line.dotted;text:red {
    **Room Database** annotation:
    entities = { see classes below }
    version = 1
    exportSchema = false
    ---
  +SmobItemDTO::class
  +**SmobUserDTO**::class
  +SmobGroupDTO::class
  +SmobShopDTO::class
  +SmobProductDTO::class
  +SmobListDTO::class
}

annotation TypeConverters #pink;line:red;line.dotted;text:red {
    **Room TypeConverters ** annotation:
    ---
  +**LocalDbConverters**::class
}

together {
    SmobDatabase o-right- Database
    SmobDatabase o-right- TypeConverters
}

interface SmobItemDao #aliceblue;line:blue;line.dotted;text:blue {
  DAO for the **smobItems** table
  [async]
  +...()
}

interface SmobUserDao #aliceblue;line:blue;line.dotted;text:blue {
  DAO for the **smobUsers** table
  [async]
  +getSmobUsers()
  +getSmobUser()
  +saveSmobUser()
  +deleteAllSmobUsers()
}

interface SmobXxxxDao #aliceblue;line:blue;line.dotted;text:blue {
  DAO for the **smobXxxx** table
  [async]
  +...()
}

together {
    SmobDatabase o-down- SmobItemDao
    SmobDatabase o-down- SmobUserDao
    SmobDatabase o-down- SmobXxxxDao
}


class LocalDB << (S,#FF7700) Singleton >> {
  Singleton class, providing **factory functions** for the
  **SmobDatabase** [smob.db] as well as all **Smob<XXX>Dao**

  +**createSmobDatabase**(): SmobDatabase
  +createSmobItemDao(db: SmobDatabase): SmobItemDao
  +**createSmobUserDao**(db: SmobDatabase): SmobUserDao
  +createSmobGroupDao(db: SmobDatabase): SmobGroupDao
  +createSmob<...>Dao(): Smob<...>Dao
}

class Room #lightgray ##gray {
 {field} (imported class)
 + **databaseBuilder**()
 }

LocalDB -left-o SmobDatabase : factory for >
LocalDB o.down. Room : uses >


class SmobUserDTO {
    Data Transfer Object
    for **SmobUser** items
    ---
    +username
    +name
    +email
    +imageUrl
}
    
SmobUserDao -up-|> SmobUserDTO : uses >

annotation Entity #pink;line:red;line.dotted;text:red {
   **Room** annotations
   {method} @Entity ("**smobUsers**")
   {method} @PrimaryKey
   {method} @ColumnInfo (...)
}

SmobUserDTO o-left. Entity

annotation Dao #pink;line:red;line.dotted;text:red {
   **Room** annotations
   {method} @Dao
   {method} @Query (...)
   {method} @Insert (...)
}

SmobUserDao o-down. Dao

frame "dbServices" #Lightblue {
    class dbObject << (S,#FF7700) SmobUserDao>> implements SmobUserDao, SmobItemDao, SmobXxxxDao {
        **Singleton**
        from **Koin** Service Locator
        ---
        (dbServices)
    }
}

LocalDB o-up-- dbObject : uses <

@enduml
```
