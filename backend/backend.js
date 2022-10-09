const jsonServer = require('json-server')
const server = jsonServer.create()
const router = jsonServer.router('db.json')
const middlewares = jsonServer.defaults()
const path = require('path');
const fs = require('fs');

// firebase admin SDK
var admin = require("firebase-admin");
var serviceAccount = require("./shopmob-335809-firebase-adminsdk-u9glb-e694b132f2.json");

// initialize firebase SDK
const app = admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// fetch messaging service (of our app)
const messaging = admin.messaging(app)


// serve local images
var dir = path.join(__dirname, 'public');

var mime = {
    html: 'text/html',
    txt: 'text/plain',
    css: 'text/css',
    gif: 'image/gif',
    jpg: 'image/jpeg',
    png: 'image/png',
    svg: 'image/svg+xml',
    js: 'application/javascript'
};

server.get('/images*', function (req, res) {
    var file = path.join(dir, req.path.replace(/\/$/, '/index.html'));
    if (file.indexOf(dir + path.sep) !== 0) {
        return res.status(403).end('Forbidden');
    }
    var type = mime[path.extname(file).slice(1)] || 'text/plain';
    var s = fs.createReadStream(file);
    s.on('open', function () {
        res.set('Content-Type', type);
        s.pipe(res);
    });
    s.on('error', function () {
        res.set('Content-Type', 'text/plain');
        res.status(404).end('Not found');
    });
});


// Set default middlewares (logger, static, cors and no-cache)
server.use(middlewares)

const customRoutes = {
  "/api/*": "/$1",
  "/api/users/:id": "/api/users?id=:id",
  "/api/lists/:id": "/api/lists?id=:id",
  "/api/products/:id": "/api/products?id=:id",
  "/api/shops/:id": "/api/shops?id=:id",
  "/api/groups/:id": "/api/groups?id=:id"
}
server.use(jsonServer.rewriter(customRoutes))


// Add custom routes before JSON Server router
// server.get('/echo', (req, res) => {
//   res.jsonp(req.query)
// })

// To handle POST, PUT and PATCH you need to use a body-parser
// You can use the one used by JSON Server
server.use(jsonServer.bodyParser)
// server.use((req, res, next) => {
//   if (req.method === 'POST') {
//     req.body.createdAt = Date.now()
//   }
//   // Continue to JSON Server router
//   next()
// })

// intercept returned resources before it goes out --> send FCM update message to topic
router.render = (req, res) => {
    switch(req.method) {
        case 'GET':
            // render content (unmodified)
            res.jsonp(res.locals.data)
            break

        case 'POST':
        case 'PUT':
            res.jsonp(res.locals.data)

            const incoming = req.originalUrl.split('/').filter(n => n)

            // The topic name can be optionally prefixed with "/topics/".
            const topic = 'shopmob';

            const message = {
            data: {
                method: req.method,
                table: incoming[0],
                element: incoming[1],
            },
            topic: topic
            };

            // Send a message to devices subscribed to the provided topic.
            messaging.send(message)
            .then((response) => {
                // Response is a message ID string.
                console.log(`Successfully sent message to FCM (${message.data.method}/${message.data.table}):`, response);
            })
            .catch((error) => {
                console.log(`Error sending message to FCM (${message.data.method}/${message.data.table}):`, error);
            });

            break

    }
}

// Use default router
server.use(router)
server.listen(3000, () => {
  console.log('ShopMob Server is running')
  console.log('Listening on http://localhost:3000/')
  console.log('')
  console.log('Serving locally stored test images on...')
  console.log('/images/smobUsers/<num>.jpg')
  console.log('/images/smobProducts/<num>.jpg')
  console.log('/images/smobShops/<num>.jpg')
  console.log('')
  console.log('Custom routes:')
  console.dir(Object.keys(customRoutes))
})