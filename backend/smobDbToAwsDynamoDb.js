const fs = require('fs');

// import required AWS SDK clients and commands for Node.js
const {
  DynamoDBClient,
  BatchWriteItemCommand
} = require("@aws-sdk/client-dynamodb");

// turn regular "JSON" to "DynamoDB JSON"
const attr = require('dynamodb-data-types').AttributeValue;

// set the AWS Region
const REGION = "eu-central-1";
const dbclient = new DynamoDBClient({ region: REGION });


// JSON - Insert to Dynamo Table
const insertToDynamoTable = async function (json, dbTableName) {
    try {

        let dynamoDBRecords = getDynamoDBRecords(json, dbTableName)
        var batches = [];

        while (dynamoDBRecords.length) {
            batches.push(dynamoDBRecords.splice(0, 25));
        }

        await callDynamoDBInsert(batches, dbTableName)

    } catch (error) {
        console.log(`Problem while inserting into table ${dbTableName}: ${error}`);
        return error;
    }
}

const callDynamoDBInsert = async function(batches, dynamoTableName){
    return Promise.all(
        batches.map(async (batch) => {
            requestItems = {}
            requestItems[dynamoTableName] = batch

            var params = {
                RequestItems: requestItems
            };

            await dbclient.send(new BatchWriteItemCommand(params))
        })
    );
}

// get DynamoDB records from json
const getDynamoDBRecords = function (data) {

    let dynamoDBRecords = data.map(entity => {

        // reformat to fit DynamoDB (JSON --> "marshalled DynamoDB JSON"
        // https://www.npmjs.com/package/@aws-sdk/client-dynamodb#wrap
        entity = attr.wrap(
            entity, 
            {
                types: {
                    business: 'L',  /* override automatic typing for "business" (hours) - they are not unique */
                    groups: 'L'     /*  override automatic typing for "groups" (of user) - can be empty */ 
                }
            })

        console.log(entity)
        let dynamoRecord = Object.assign({ PutRequest: { Item: entity } })
        
        return dynamoRecord
    })

    return dynamoDBRecords
}


// Create DynamoDB service object
const seedDynamoDb = function() {

    // fetch DB (as produced by smob.js)
    let rawdata = fs.readFileSync(__dirname + '\\db.json', 'utf8');
    let json_data = JSON.parse(rawdata);

    let tableNames = Object.keys(json_data)

    // loop over all tables in db.json
    tableNames.forEach(tName => {

        // fetch table data
        let jData = json_data[tName];

        // form DynamoDB table name
        let tNameDynDb = 'fw-smobbe-Smob' + tName[0].toUpperCase() + tName.slice(1, tName.length)

        // define async lambda for seeding of next table
        const seedTable = async (jD, tN) => {
            try {
                const msg = await insertToDynamoTable(jD, tN)
                if(msg == undefined) {
                    console.log(`Success, items inserted, table ${tN}`);
                } else {
                    // console.dir(msg);
                }
                    
            } catch (err) {
                console.log("Error", err);
            }
        };

        // seed next DynamoDB table
        seedTable(jData, tNameDynDb);
            
    });
    
};


// run seeding...
seedDynamoDb();
