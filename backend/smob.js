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


    // define some (consistent) products
    const daProducts = [
        { id: faker.datatype.uuid(), name: 'Milk', description: 'lactose free', catMain: 'foods', catSub: 'dairy',  },
        { id: faker.datatype.uuid(), name: 'Cheese', description: 'Cheddar', catMain: 'foods', catSub: 'dairy',  },
        { id: faker.datatype.uuid(), name: 'Bread', description: 'white sliced', catMain: 'foods', catSub: 'brekky',  },
        { id: faker.datatype.uuid(), name: 'Pees', description: 'green', catMain: 'foods', catSub: 'cans',  },
        { id: faker.datatype.uuid(), name: 'Coffee', description: 'espresso 9', catMain: 'foods', catSub: 'brekky',  },
        { id: faker.datatype.uuid(), name: 'Musli', description: 'no fruit', catMain: 'foods', catSub: 'brekky',  },
        { id: faker.datatype.uuid(), name: 'Apples', description: 'Granny Smith', catMain: 'foods', catSub: 'fruit-n-veg',  },
        { id: faker.datatype.uuid(), name: 'Beer', description: 'Tegernseer', catMain: 'foods', catSub: 'beverages',  },
        { id: faker.datatype.uuid(), name: 'Oranges', description: 'Sunny Vale', catMain: 'foods', catSub: 'fruit-n-veg',  },
        { id: faker.datatype.uuid(), name: 'Nails', description: '100 x', catMain: 'hardware', catSub: 'DIY',  },
        { id: faker.datatype.uuid(), name: 'Light Bulb', description: '60 W', catMain: 'hardware', catSub: 'DIY',  },
        { id: faker.datatype.uuid(), name: 'Hammer', description: 'rubber', catMain: 'hardware', catSub: 'tools',  },
        { id: faker.datatype.uuid(), name: 'Ladder', description: 'foldable', catMain: 'hardware', catSub: 'tools',  },
        { id: faker.datatype.uuid(), name: 'Wood', description: 'Planks', catMain: 'hardware', catSub: 'DIY',  },    
        { id: faker.datatype.uuid(), name: 'Paper', description: 'A4', catMain: 'supplies', catSub: 'office',  },
        { id: faker.datatype.uuid(), name: 'Stapler', description: 'large', catMain: 'supplies', catSub: 'office',  },
        { id: faker.datatype.uuid(), name: 'Pen', description: 'blue', catMain: 'supplies', catSub: 'office',  },
        { id: faker.datatype.uuid(), name: 'Stamp', description: '60 p', catMain: 'supplies', catSub: 'postal',  },
        { id: faker.datatype.uuid(), name: 'Envelope', description: '100 x', catMain: 'supplies', catSub: 'postal',  },    
        { id: faker.datatype.uuid(), name: 'Suit', description: 'blue', catMain: 'clothing', catSub: 'business',  },
        { id: faker.datatype.uuid(), name: 'Shirt', description: 'white', catMain: 'clothing', catSub: 'business',  },
        { id: faker.datatype.uuid(), name: 'T-Shirt', description: 'XXL', catMain: 'clothing', catSub: 'leisure',  },
        { id: faker.datatype.uuid(), name: 'Shorts', description: 'swimming', catMain: 'clothing', catSub: 'leisure',  },
        { id: faker.datatype.uuid(), name: 'Boots', description: 'black', catMain: 'clothing', catSub: 'shoes',  },   
    ]
    

    // generate some products
    var smobProducts = []
    var productIds = []
    for (let id = 0; id < daProducts.length; id++) {

        var uuid = daProducts[id].id
        var name = daProducts[id].name
        var description = daProducts[id].description
        var imageUrl = faker.image.image()
        var catMain = daProducts[id].catMain
        var catSub = daProducts[id].catSub
        var dateLastPurchase = faker.date.recent().toDateString()
        var frequencyPurchase = faker.datatype.number(100)

        smobProducts.push({
            "id": uuid,
            "name": name,
            "description": description,
            "imageUrl": imageUrl,
            "category": {
                "main": catMain,
                "sub": catSub,
            },
            "activity": {
                "date": dateLastPurchase,
                "repetitions": frequencyPurchase,
              }
        })

        // log all productIds
        productIds.push(uuid)

    }


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
        { id: faker.datatype.uuid(), name: 'Wimmer', category: 'bakery', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'chain' },
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
            
    // define some lists
    const daLists = [
        { id: faker.datatype.uuid(), name: 'Groceries', },
        { id: faker.datatype.uuid(), name: 'Xmas party', },
        { id: faker.datatype.uuid(), name: 'After work event', },
        { id: faker.datatype.uuid(), name: 'Road trip', },
        { id: faker.datatype.uuid(), name: 'Redecoration', },
    ]

    // generate some shopping lists
    var smobLists = []
    var listIds = []
    const itemStatus = ['open', 'in progress', 'done']
    for (let id = 0; id < daLists.length; id++) {

        var uuid = daLists[id].id
        var name = daLists[id].name
        var description = faker.lorem.words()
        var itemsOnList = [...Array(rand(20)).keys()].map(_ => faker.helpers.randomize(productIds)).sort()
        var items = itemsOnList.map(
            prodId => { return { "id": prodId, "status": faker.helpers.randomize(itemStatus) } }
        )

        // aggregate status
        var listStatus = 'done'
        var allListStatus = items.map(item => item.status)
        var allListStatusFiltered = allListStatus.filter(status => status != 'done')
        var listCompletion = completion(allListStatus)
        if(items.length > 0) {
            if(allListStatusFiltered.length > 0) {
                if(allListStatusFiltered.includes('in progress')) {
                    listStatus = 'in progress'
                } else {
                    listStatus = 'open'
                }
            }    
        } else {
            listCompletion = 100
        }
        var lifecycle = { status: listStatus, completion: listCompletion }

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
        //         "status": "open|in progress|done",
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

// determine the degree of completion of a list
function completion(statusList) {

    const listLen = statusList.length
    const numDone = statusList.filter(status => status == 'done').length

    return Math.round(numDone/listLen * 100)

}

// write to file
// fs.writeFile('db.json', smob(), 'utf8', callback);


module.exports = smob