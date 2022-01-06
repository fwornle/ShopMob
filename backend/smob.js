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
        var imageUrl = `https://placeimg.com/200/150/people?${id}`

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
    const groupTypes = ['OTHER', 'FAMILY', 'FRIENDS', 'WORK']
    for (let id = 0; id < 5; id++) {

        var uuid = faker.datatype.uuid()
        var name = faker.lorem.word()
        var description = faker.lorem.words(rand(5))
        var type = faker.helpers.randomize(groupTypes)
        var members = [...Array(rand(10)).keys()].map(_ => faker.helpers.randomize(userIds)).sort()
        var activityDate = faker.date.past()
        var activityReps = rand(20)

        smobGroups.push({
            "id": uuid,
            "name": name,
            "description": description,
            "type": type,
            "members": members,
            "activity": {
                "date": activityDate,
                "reps": activityReps,
              }
        })

        // log all groupIds
        groupIds.push(uuid)

    }


    // define some (consistent) products
    const daProducts = [
        { id: faker.datatype.uuid(), name: 'Milk', description: 'lactose free', catMain: 'FOODS', catSub: 'DAIRY',  },
        { id: faker.datatype.uuid(), name: 'Cheese', description: 'Cheddar', catMain: 'FOODS', catSub: 'DAIRY',  },
        { id: faker.datatype.uuid(), name: 'Bread', description: 'white sliced', catMain: 'FOODS', catSub: 'BREKKY',  },
        { id: faker.datatype.uuid(), name: 'Pees', description: 'green', catMain: 'FOODS', catSub: 'CANNED_FOOD',  },
        { id: faker.datatype.uuid(), name: 'Coffee', description: 'espresso 9', catMain: 'FOODS', catSub: 'BREKKY',  },
        { id: faker.datatype.uuid(), name: 'Musli', description: 'no fruit', catMain: 'FOODS', catSub: 'BREKKY',  },
        { id: faker.datatype.uuid(), name: 'Apples', description: 'Granny Smith', catMain: 'FOODS', catSub: 'FRUIT_VEGETABLE',  },
        { id: faker.datatype.uuid(), name: 'Beer', description: 'Tegernseer', catMain: 'FOODS', catSub: 'BEVERAGES',  },
        { id: faker.datatype.uuid(), name: 'Oranges', description: 'Sunny Vale', catMain: 'FOODS', catSub: 'FRUIT_VEGETABLE',  },
        { id: faker.datatype.uuid(), name: 'Nails', description: '100 x', catMain: 'HARDWARE', catSub: 'DIY',  },
        { id: faker.datatype.uuid(), name: 'Light Bulb', description: '60 W', catMain: 'HARDWARE', catSub: 'DIY',  },
        { id: faker.datatype.uuid(), name: 'Hammer', description: 'rubber', catMain: 'HARDWARE', catSub: 'TOOLS',  },
        { id: faker.datatype.uuid(), name: 'Ladder', description: 'foldable', catMain: 'HARDWARE', catSub: 'TOOLS',  },
        { id: faker.datatype.uuid(), name: 'Wood', description: 'Planks', catMain: 'HARDWARE', catSub: 'DIY',  },    
        { id: faker.datatype.uuid(), name: 'Paper', description: 'A4', catMain: 'SUPPLIES', catSub: 'OFFICE',  },
        { id: faker.datatype.uuid(), name: 'Stapler', description: 'large', catMain: 'SUPPLIES', catSub: 'OFFICE',  },
        { id: faker.datatype.uuid(), name: 'Pen', description: 'blue', catMain: 'SUPPLIES', catSub: 'OFFICE',  },
        { id: faker.datatype.uuid(), name: 'Stamp', description: '60 p', catMain: 'SUPPLIES', catSub: 'POSTAL',  },
        { id: faker.datatype.uuid(), name: 'Envelope', description: '100 x', catMain: 'SUPPLIES', catSub: 'POSTAL',  },    
        { id: faker.datatype.uuid(), name: 'Suit', description: 'blue', catMain: 'CLOTHING', catSub: 'BUSINESS',  },
        { id: faker.datatype.uuid(), name: 'Shirt', description: 'white', catMain: 'CLOTHING', catSub: 'BUSINESS',  },
        { id: faker.datatype.uuid(), name: 'T-Shirt', description: 'XXL', catMain: 'CLOTHING', catSub: 'LEISURE',  },
        { id: faker.datatype.uuid(), name: 'Shorts', description: 'swimming', catMain: 'CLOTHING', catSub: 'LEISURE',  },
        { id: faker.datatype.uuid(), name: 'Boots', description: 'black', catMain: 'CLOTHING', catSub: 'SHOES',  },   
    ]
    

    // generate some products
    var smobProducts = []
    var productIds = []
    for (let id = 0; id < daProducts.length; id++) {

        var uuid = daProducts[id].id
        var name = daProducts[id].name
        var description = daProducts[id].description
        var imageUrl = `https://placeimg.com/200/150/tech?${id}`
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
                "reps": frequencyPurchase,
              }
        })

        // log all productIds
        productIds.push(uuid)

    }


    // define some local shops
    const daShops = [
        { id: faker.datatype.uuid(), name: 'Lidl', category: 'SUPERMARKET', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Aldi', category: 'SUPERMARKET', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Rewe', category: 'SUPERMARKET', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Edeka', category: 'SUPERMARKET', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Obi', category: 'HARDWARE', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Bauhaus', category: 'HARDWARE', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Hornbach', category: 'HARDWARE', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Höffner', category: 'FURNITURE', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Segmüller', category: 'FURNITURE', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'XXX Lutz', category: 'FURNITURE', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Wimmer', category: 'BAKERY', latitude: faker.address.latitude(), longitude: faker.address.longitude(), type: 'CHAIN' },
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
        var business = businessHours()

        // {
        //     "id": "UUID-store",
        //     "name": "store name",
        //     "description": "it's a good-e store",
        //     "latitude": "where the shop is",
        //     "longitude": "where the shop is",
        //     "type": "CHAIN|individual",
        //     "category": "(default)other|SUPERMARKET|drugstore|HARDWARE|CLOTHING|accessories|SUPPLIES",
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
            "location": {
                "latitude": latitude,
                "longitude": longitude
            },
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
    const itemStatus = ['OPEN', 'IN_PROGRESS', 'DONE']
    for (let id = 0; id < daLists.length; id++) {

        var uuid = daLists[id].id
        var name = daLists[id].name
        var description = faker.lorem.words()
        var itemsOnList = [...Array(rand(20)).keys()].map(_ => faker.helpers.randomize(productIds)).sort()
        var items = itemsOnList.map(
            prodId => { return { "id": prodId, "status": faker.helpers.randomize(itemStatus) } }
        )

        // aggregate status
        var listStatus = 'DONE'
        var allListStatus = items.map(item => item.status)
        var allListStatusFiltered = allListStatus.filter(status => status != 'DONE')
        var listCompletion = completion(allListStatus)
        if(items.length > 0) {
            if(allListStatusFiltered.length > 0) {
                if(allListStatusFiltered.includes('IN_PROGRESS')) {
                    listStatus = 'IN_PROGRESS'
                } else {
                    listStatus = 'OPEN'
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
    const numDone = statusList.filter(status => status == 'DONE').length

    return Math.round(numDone/listLen * 100)

}

// return some business hours
function businessHours() {
    var weAreOpen = [
        "09:00 - 12:00, 14:00 - 22:00",
        "09:00 - 12:00, 14:00 - 22:00",
        "09:00 - 12:00",
        "09:00 - 12:00, 14:00 - 22:00",
        "09:00 - 12:00, 14:00 - 22:00",
        "09:00 - 12:00, 14:00 - 18:00",
        "closed"
    ]

    return weAreOpen
}


// write to file
// fs.writeFile('db.json', smob(), 'utf8', callback);


module.exports = smob