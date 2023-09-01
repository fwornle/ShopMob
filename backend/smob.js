const { image, random, internet } = require('faker')
const faker = require('faker/locale/de')
const fs = require('fs');


// convert JSON object to string
const data = JSON.stringify(smob());

// write JSON string to a file
fs.writeFile('db.json', data, (err) => {
    if (err) {
        throw err;
    }
    console.log("JSON data is saved.");
});



function rand(max) {
    return Math.floor(Math.random() * max);
}


/*
 * ***************************************************************************************
 * smob
 * ***************************************************************************************
 */
function smob () {

    // always produce the same results
    faker.seed(123)


    // ----------------------------------------------------------------------------------
    // generate some users
    // ----------------------------------------------------------------------------------
    var smobUsers = []
    var userIds = []
    for (let id = 0; id < 20; id++) {

        var uuid = faker.datatype.uuid()
        var listItemStatus = 'OPEN'
        var listItemPosition = id
        var username = faker.internet.userName()
        var name = faker.fake("{{name.lastName}}, {{name.firstName}}")
        var email = faker.internet.email()
        var imageUrl = `https://picsum.photos/200/150?${id}`

        smobUsers.push({
            "id": uuid,
            "status": listItemStatus,
            "position": listItemPosition,
            "username": username,
            "name": name,
            "email": email,
            "imageUrl": imageUrl,
            "groups": [],
        })

        // log all userIds
        userIds.push(uuid)

    }

    // manually add "myself" (own user: BTwLBEmaoWfQKbooBguh0x84bV52)
    smobUsers.push({
        "id": "BTwLBEmaoWfQKbooBguh0x84bV52",
        "status": "OPEN",
        "position": 20,
        "username": "Frank.Douvre",
        "name": "Frank Douvre",
        "email": "fradou7@gmail.com",
        "imageUrl": "https://lh3.googleusercontent.com/a/AATXAJx9nYtfUiFXPg2JABf1OWUMGasy_szkM1PSvfiT=s96-c",
        "groups": [],
    })


    // ----------------------------------------------------------------------------------
    // generate some groups
    // ----------------------------------------------------------------------------------
    var smobGroups = []
    var groupIds = []
    const status = ['OPEN', 'IN_PROGRESS', 'DONE']
    const groupTypes = ['OTHER', 'FAMILY', 'FRIENDS', 'WORK']
    for (let id = 0; id < 8; id++) {

        var uuid = faker.datatype.uuid()
        var listItemStatus = 'OPEN'
        var listItemPosition = id
        var name = faker.lorem.word()
        var description = faker.lorem.words(rand(5))
        var type = faker.helpers.randomize(groupTypes)
        var membersOnList = [...Array(rand(10)).keys()].map(_ => faker.helpers.randomize(userIds)).sort()
        var membersOnList = unique(membersOnList)
        membersOnList.splice(0, 0, "BTwLBEmaoWfQKbooBguh0x84bV52")
        var members = membersOnList.map(
            (userId, idx) => { 

                // adjust groups list of current user (back reference to this group)
                if(userId != "BTwLBEmaoWfQKbooBguh0x84bV52") {
                    // only do this for all dynamically created (dev) users
                    var daUser = smobUsers.find(user => user.id == userId)
                    if(!daUser.groups.includes(uuid)) daUser.groups.push(uuid)
                } 
    
                // return member as tuple of ID and status and list position
                return { 
                    "id": userId, 
                    "status": faker.helpers.randomize(status), 
                    "listPosition": idx,
                } 
            }
        )

        var activityDate = faker.date.past()
        var activityReps = rand(20)

        smobGroups.push({
            "id": uuid,
            "status": listItemStatus,
            "position": listItemPosition,
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

    // manually add (all) groups IDs as groups reference to "myself" (own user: BTwLBEmaoWfQKbooBguh0x84bV52)
    smobUsers.find(user => user.id == "BTwLBEmaoWfQKbooBguh0x84bV52").groups = groupIds


    // ----------------------------------------------------------------------------------
    // define some (consistent) products
    // ----------------------------------------------------------------------------------
    const daProducts = [
        { id: faker.datatype.uuid(), name: 'Milk', description: 'lactose free', catMain: 'FOODS', catSub: 'DAIRY', listPosition: 1, inShopCategory: "SUPERMARKET", inShopName: "Rewe", },
        { id: faker.datatype.uuid(), name: 'Cheese', description: 'Cheddar', catMain: 'FOODS', catSub: 'DAIRY', listPosition: 2, inShopCategory: "SUPERMARKET", inShopName: "Rewe", },
        { id: faker.datatype.uuid(), name: 'Bread', description: 'white sliced', catMain: 'FOODS', catSub: 'BREKKY', listPosition: 3,  inShopCategory: "BAKERY", inShopName: "Wimmer", },
        { id: faker.datatype.uuid(), name: 'Pees', description: 'green', catMain: 'FOODS', catSub: 'CANNED_FOOD', listPosition: 4, inShopCategory: "SUPERMARKET", inShopName: "Rewe", },
        { id: faker.datatype.uuid(), name: 'Coffee', description: 'espresso 9', catMain: 'FOODS', catSub: 'BREKKY', listPosition: 5, inShopCategory: "SUPERMARKET", inShopName: "Rewe", },
        { id: faker.datatype.uuid(), name: 'Musli', description: 'no fruit', catMain: 'FOODS', catSub: 'BREKKY', listPosition: 6, inShopCategory: "SUPERMARKET", inShopName: "Rewe", },
        { id: faker.datatype.uuid(), name: 'Apples', description: 'Granny Smith', catMain: 'FOODS', catSub: 'FRUIT_VEGETABLE', listPosition: 7,  inShopCategory: "SUPERMARKET", inShopName: "Aldi", },
        { id: faker.datatype.uuid(), name: 'Beer', description: 'Tegernseer', catMain: 'FOODS', catSub: 'BEVERAGES', listPosition: 8,  inShopCategory: "SUPERMARKET",  inShopName: "Aldi", },
        { id: faker.datatype.uuid(), name: 'Oranges', description: 'Sunny Vale', catMain: 'FOODS', catSub: 'FRUIT_VEGETABLE', listPosition: 9,  inShopCategory: "SUPERMARKET", inShopName: "Lidl", },
        { id: faker.datatype.uuid(), name: 'Nails', description: '100 x', catMain: 'HARDWARE', catSub: 'DIY', listPosition: 1,  inShopCategory: "HARDWARE", inShopName: "Bauhaus", },
        { id: faker.datatype.uuid(), name: 'Light Bulb', description: '60 W', catMain: 'HARDWARE', catSub: 'DIY', listPosition: 2, inShopCategory: "HARDWARE", inShopName: "Bauhaus", },
        { id: faker.datatype.uuid(), name: 'Hammer', description: 'rubber', catMain: 'HARDWARE', catSub: 'TOOLS', listPosition: 3, inShopCategory: "HARDWARE", inShopName: "Hornbach", },
        { id: faker.datatype.uuid(), name: 'Ladder', description: 'foldable', catMain: 'HARDWARE', catSub: 'TOOLS', listPosition: 4, inShopCategory: "HARDWARE", inShopName: "Obi", },
        { id: faker.datatype.uuid(), name: 'Wood', description: 'Planks', catMain: 'HARDWARE', catSub: 'DIY', listPosition: 5, inShopCategory: "HARDWARE", inShopName: "Obi", },    
        { id: faker.datatype.uuid(), name: 'Paper', description: 'A4', catMain: 'SUPPLIES', catSub: 'OFFICE', listPosition: 1, inShopCategory: "SUPPLIES", inShopName: "Kaufhof", },
        { id: faker.datatype.uuid(), name: 'Stapler', description: 'large', catMain: 'SUPPLIES', catSub: 'OFFICE', listPosition: 2, inShopCategory: "SUPPLIES", inShopName: "Kaufhof", },
        { id: faker.datatype.uuid(), name: 'Pen', description: 'blue', catMain: 'SUPPLIES', catSub: 'OFFICE', listPosition: 3, inShopCategory: "SUPPLIES", inShopName: "Kaufhof", },
        { id: faker.datatype.uuid(), name: 'Stamp', description: '60 p', catMain: 'SUPPLIES', catSub: 'POSTAL', listPosition: 4, inShopCategory: "SUPPLIES", inShopName: "Kaufhof", },
        { id: faker.datatype.uuid(), name: 'Envelope', description: '100 x', catMain: 'SUPPLIES', catSub: 'POSTAL', listPosition: 5, inShopCategory: "SUPPLIES", inShopName: "Kaufhof", },    
        { id: faker.datatype.uuid(), name: 'Suit', description: 'blue', catMain: 'CLOTHING', catSub: 'BUSINESS', listPosition: 1, inShopCategory: "CLOTHING", inShopName: "Zara", },
        { id: faker.datatype.uuid(), name: 'Shirt', description: 'white', catMain: 'CLOTHING', catSub: 'BUSINESS', listPosition: 2, inShopCategory: "CLOTHING", inShopName: "Zara", },
        { id: faker.datatype.uuid(), name: 'T-Shirt', description: 'XXL', catMain: 'CLOTHING', catSub: 'LEISURE', listPosition: 3, inShopCategory: "CLOTHING", inShopName: "Zara", },
        { id: faker.datatype.uuid(), name: 'Shorts', description: 'swimming', catMain: 'CLOTHING', catSub: 'LEISURE', listPosition: 4, inShopCategory: "CLOTHING", inShopName: "Kaufhof", },
        { id: faker.datatype.uuid(), name: 'Boots', description: 'black', catMain: 'CLOTHING', catSub: 'SHOES', listPosition: 5, inShopCategory: "CLOTHING", inShopName: "Salamander", },   
    ]
    

    // generate some products
    var smobProducts = []
    var productIds = []
    for (let id = 0; id < daProducts.length; id++) {

        var uuid = daProducts[id].id
        var listItemStatus = 'OPEN'
        var listItemPosition = id
        var name = daProducts[id].name
        var description = daProducts[id].description
        var imageUrl = `https://picsum.photos/200/150?${id}`
        var catMain = daProducts[id].catMain
        var catSub = daProducts[id].catSub
        var dateLastPurchase = faker.date.recent().toDateString()
        var frequencyPurchase = faker.datatype.number(100)
        var inShopCategory = daProducts[id].inShopCategory
        var inShopName = daProducts[id].inShopName
        var inShopLocation = { "latitude": faker.datatype.float(100), "longitude": faker.datatype.float(100) }

        smobProducts.push({
            "id": uuid,
            "status": listItemStatus,
            "position": listItemPosition,
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
              },
            "inShop": {
                "category": inShopCategory,
                "name": inShopName,
                "location": inShopLocation
            }
        })

        // log all productIds
        productIds.push(uuid)

    }


    // ----------------------------------------------------------------------------------
    // define some local shops
    // ----------------------------------------------------------------------------------
    const daShops = [
        { id: faker.datatype.uuid(), name: 'Lidl', category: 'SUPERMARKET', latitude: 48.19446661077098, longitude: 11.465429663658142, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Aldi', category: 'SUPERMARKET', latitude: 48.19283416587666, longitude: 11.466210186481476, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Rewe', category: 'SUPERMARKET', latitude: 48.19143948391694, longitude: 11.466687619686128, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Edeka', category: 'SUPERMARKET', latitude: 48.181761491349825, longitude: 11.476078033447266, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Obi', category: 'HARDWARE', latitude: 48.18904340320997, longitude: 11.467192545533182, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Bauhaus', category: 'HARDWARE', latitude: 48.17845639240512, longitude: 11.505287289619448, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Hornbach', category: 'HARDWARE', latitude: 48.181761491349825, longitude: 11.476078033447266, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Höffner', category: 'FURNITURE', latitude: 48.19143948391694, longitude: 11.466687619686128, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Segmüller', category: 'FURNITURE', latitude: 48.19283416587666, longitude: 11.466210186481476, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'XXX Lutz', category: 'FURNITURE', latitude: 48.19446661077098, longitude: 11.465429663658142, type: 'CHAIN' },
        { id: faker.datatype.uuid(), name: 'Wimmer', category: 'BAKERY', latitude: 48.17845639240512, longitude: 11.505287289619448, type: 'CHAIN' },
    ]


    // generate some shops
    var smobShops = []
    var shopIds = []
    for (let id = 0; id < daShops.length; id++) {

        var uuid = daShops[id].id
        var listItemStatus = 'OPEN'
        var listItemPosition = id
        var name = daShops[id].name
        var description = faker.lorem.words()
        var imageUrl = `https://picsum.photos/200/150?${id}`
        var latitude = daShops[id].latitude
        var longitude = daShops[id].longitude
        var type = daShops[id].type
        var category = daShops[id].category
        var business = businessHours()

        // {
        //     "id": "UUID-store",
        //     "status": "NEW",
        //     "position": 0,
        //     "name": "store name",
        //     "description": "it's a good-e store",
        //     "imageUrl": "some URL",
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
            "status": listItemStatus,
            "position": listItemPosition,
            "name": name,
            "description": description,
            "imageUrl": imageUrl,
            "location": {
                "latitude": latitude,
                "longitude": longitude
            },
            "type": type,
            "category": category,
            "business": business,
        })

        // log all shopIds
        shopIds.push(uuid)

    }
            

    // ----------------------------------------------------------------------------------
    // define some lists
    // ----------------------------------------------------------------------------------
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
    for (let id = 0; id < daLists.length; id++) {

        var uuid = daLists[id].id
        var listItemStatus = 'OPEN'
        var listItemPosition = id
        var name = daLists[id].name
        var description = faker.lorem.words()
        var itemsOnList = unique([...Array(2 + rand(20)).keys()].map(_ => faker.helpers.randomize(productIds)).sort())
        var items = itemsOnList.map(
            (prodId, idx) => { return { 
                "id": prodId, 
                "status": faker.helpers.randomize(status), 
                "listPosition": idx,
                "mainCategory": daProducts.find(prod => prod.id == prodId).catMain,
            } 
        }
        )

        // groups associated with lists
        var groupsOnList = unique([...Array(1 + rand(groupIds.length)).keys()].map(_ => faker.helpers.randomize(groupIds)).sort())
        var groups = groupsOnList.map(
            (groupId, idx) => { return { 
                "id": groupId, 
                "status": faker.helpers.randomize(status), 
                "listPosition": idx,
            } 
        })

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
        //     "status": "NEW",
        //     "position": 0,
        //     "name": "smob list name",
        //     "description": "our daily groceries",
        //     "items": [
        //         { "id": "productId1", "status": "open|in progress|done" },
        //         { "id": "productId2", "status": "open|in progress|done" },
        //         { "id": "productId3", "status": "open|in progress|done" },
        //         { }
        //     ],
        //     "groups": [
        //         { "id": "groupId1", "status": "NEW|OPEN|IN_PROGRESS|DONE|DELETED", "customPosition": 1 },
        //         { "id": "groupId2", "status": "NEW|OPEN|IN_PROGRESS|DONE|DELETED", "customPosition": 2 },
        //         { "id": "groupId3", "status": "NEW|OPEN|IN_PROGRESS|DONE|DELETED", "customPosition": 3 },
        //         "..."
        //     ],
        //        "lifecycle": {
        //         "status": "open|in progress|done",
        //         "completion": 35
        //     }
        // }    

        smobLists.push({
            "id": uuid,
            "status": listItemStatus,
            "position": listItemPosition,
            "name": name,
            "description": description,
            "items": items,
            "groups": groups,
            "lifecycle": lifecycle
        })

        // log all listIds
        listIds.push(uuid)

    }


    // ----------------------------------------------------------------------------------
    return { 
        "users": smobUsers,
        "groups": smobGroups,
        "shops": smobShops,
        "products": smobProducts,
        "lists": smobLists,
    }

}



// return array with only unique values
function unique(arr) {
    return [...new Set(arr)];
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

module.exports = smob