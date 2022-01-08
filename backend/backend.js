const jsonServer = require('json-server')
const server = jsonServer.create()
const router = jsonServer.router('db.json')
const middlewares = jsonServer.defaults()
const path = require('path');
const fs = require('fs');

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

server.get('*', function (req, res) {
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
  "/api/1/*": "/$1",
  "/api/1/users/:id": "/api/1/users?id=:id",
  "/api/1/lists/:id": "/api/1/lists?id=:id",
  "/api/1/products/:id": "/api/1/products?id=:id",
  "/api/1/shops/:id": "/api/1/shops?id=:id",
  "/api/1/groups/:id": "/api/1/groups?id=:id"
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