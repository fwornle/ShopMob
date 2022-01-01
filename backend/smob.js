const { image, random, internet } = require('faker')
const faker = require('faker/locale/de')

// test me...
smob()


function rand(max) {
    return Math.floor(Math.random() * max);
}
  
function smob () {

    // always produce the same results
    faker.seed(123)

    // define some local shops
    const daShops = [
        { id: faker.datatype.uuid(), name: 'Lidl', category: 'supermarket', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Aldi', category: 'supermarket', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Rewe', category: 'supermarket', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Edeka', category: 'supermarket', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Obi', category: 'hardware', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Bauhaus', category: 'hardware', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Hornbach', category: 'hardware', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Höffner', category: 'furniture', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Segmüller', category: 'furniture', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'XXX Lutz', category: 'furniture', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
        { id: faker.datatype.uuid(), name: 'Wimmer', category: 'bakery', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'individual' },
    ]


    // generate some shops
    var smobShops = []
    var shopIds = []
    for (let id = 0; id < daShops.length; id++) {

        var uuid = daShops[id].id
        var name = daShops[id].name
        var description = faker.lorem.words()
        var latitude = daShops[id].latitude
        var longitude = daShops[id].longitude
        var type = daShops[id].type
        var category = daShops[id].category
        var business = faker.lorem.words()

        // {
        //     "id": "UUID-store",
        //     "name": "store name",
        //     "description": "it's a good-e store",
        //     "latitude": "where the shop is",
        //     "longitude": "where the shop is",
        //     "type": "chain|individual",
        //     "category": "(default)other|supermarket|drugstore|hardware|clothing|accessories|supplies",
        //     "business": [
        //         "09:00 - 12:00, 14:00 - 22:00",
        //         "09:00 - 12:00, 14:00 - 22:00",
        //         "09:00 - 12:00",
        //         "09:00 - 12:00, 14:00 - 22:00",
        //         "09:00 - 12:00, 14:00 - 22:00",
        //         "09:00 - 12:00, 14:00 - 18:00",
        //         "closed"
        //     ]
        // }

        smobShops.push({
            "id": uuid,
            "name": name,
            "description": description,
            "latitude": latitude,
            "longitude": longitude,
            "type": type,
            "category": category,
            "business": business,
        })

        // log all userIds
        shopIds.push(uuid)

    }
    
    
    // generate some users
    var smobUsers = []
    var userIds = []
    for (let id = 0; id < 20; id++) {

        var uuid = faker.datatype.uuid()
        var username = faker.internet.userName()
        var name = faker.fake("{{name.lastName}}, {{name.firstName}}")
        var email = faker.internet.email()
        var imageUrl = faker.image.people()

        smobUsers.push({
            "id": uuid,
            "username": username,
            "name": name,
            "email": email,
            "imageUrl": imageUrl,
        })

        // log all userIds
        userIds.push(uuid)

    }


    // generate some groups
    var smobGroups = []
    var groupIds = []
    const groupTypes = ['other', 'family', 'friends', 'work']
    for (let id = 0; id < 5; id++) {

        var uuid = faker.datatype.uuid()
        var name = faker.lorem.word()
        var description = faker.lorem.words(rand(5))
        var type = faker.helpers.randomize(groupTypes)
        var members = [...Array(rand(10)).keys()].map(_ => faker.helpers.randomize(userIds)).sort()
        var activity = faker.date.past()

        smobGroups.push({
            "id": uuid,
            "name": name,
            "description": description,
            "type": type,
            "members": members,
            "activity": activity,
        })

        // log all groupIds
        groupIds.push(uuid)

    }


    // generate some products
    var smobProducts = []
    var productIds = []
    for (let id = 0; id < 50; id++) {

        var uuid = faker.datatype.uuid()
        var name = faker.commerce.product()
        var description = faker.commerce.productDescription()
        var image = faker.image.image()
        var category = { main: faker.commerce.department(), sub: faker.commerce.productName() }
        var dateLastPurchase = faker.date.recent().toDateString()
        var frequencyPurchase = faker.datatype.number()

        smobProducts.push({
            "id": uuid,
            "name": name,
            "description": description,
            "image": image,
            "category": category,
            "activity": {
                "date": dateLastPurchase,
                "repetitions": frequencyPurchase,
              }
        })

        // log all productIds
        productIds.push(uuid)

    }


    // generate some shopping lists
    var smobLists = []
    var listIds = []
    const itemStatus = ['open', 'in progress', 'done']
    for (let id = 0; id < 20; id++) {

        var uuid = faker.id
        var name = faker.commerce.product()
        var description = faker.commerce.productDescription()
        var itemsOnList = [...Array(rand(20)).keys()].map(_ => faker.helpers.randomize(productIds)).sort()
        var items = itemsOnList.map(
            prodId => { return { "id": prodId, "status": faker.helpers.randomize(itemStatus) } }
        )
        var members = [...Array(1 + rand(5)).keys()].map(_ => faker.helpers.randomize(userIds)).sort()
        var lifecycle = { state: faker.date.recent().toDateString(), completion: rand(100) }

        // {
        //     "id": "UUID-list",
        //     "name": "smob list name",
        //     "description": "our daily groceries",
        //     "items": [
        //         { "id": "productId1", "status": "open|in progress|done" },
        //         { "id": "productId2", "status": "open|in progress|done" },
        //         { "id": "productId3", "status": "open|in progress|done" },
        //         { }
        //     ],
        //     "members": [
        //         "userId1",
        //         "userId2",
        //         "userId3",
        //         "..."
        //     ],
        //     "lifecycle": {
        //         "state": "open|in progress|done",
        //         "completion": 35
        //     }
        // }

        smobLists.push({
            "id": uuid,
            "name": name,
            "description": description,
            "items": items,
            "members": members,
            "lifecycle": lifecycle
        })

        // log all listIds
        listIds.push(uuid)

    }


    return { 
        "users": smobUsers,
        "groups": smobGroups,
        "shops": smobShops,
        "products": smobProducts,
        "lists": smobLists,
    }

}


// write to file
// fs.writeFile('db.json', smob(), 'utf8', callback);


module.exports = smob