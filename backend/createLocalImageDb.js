/* eslint-disable no-plusplus */
const faker = require('faker');
const axios = require('axios');
const path = require('path');
const fs = require('fs');

const url = [];
// 100 fake avatar url from faker
for (let i = 0; i < 15; i++) {
  url.push(faker.image.people());
}


// download 100 user photoes to local image folder
for (let i = 0; i < url.length; i++) {
  // path for image storage
  const imagePath = path.join(__dirname, './public/images/smobUsers', `${i}.jpg`);
  axios({
    method: 'get',
    url: url[i],
    responseType: 'stream',
  })
    .then((response) => {
      response.data.pipe(fs.createWriteStream(imagePath));
    });
}

const url2 = [];
// 100 fake avatar url from faker
for (let i = 0; i < 30; i++) {
  url2.push(faker.image.imageUrl(640, 480, 'food', true));
}


// download 100 user photoes to local image folder
for (let i = 0; i < url2.length; i++) {
  // path for image storage
  const imagePath = path.join(__dirname, './public/images/smobProducts', `${i}.jpg`);
  axios({
    method: 'get',
    url: url2[i],
    responseType: 'stream',
  })
    .then((response) => {
      response.data.pipe(fs.createWriteStream(imagePath));
    });
}

const url3 = [];
// 100 fake avatar url from faker
for (let i = 0; i < 10; i++) {
  url3.push(faker.image.imageUrl(640, 480, 'city', true));
}


// download 100 user photoes to local image folder
for (let i = 0; i < url3.length; i++) {
  // path for image storage
  const imagePath = path.join(__dirname, './public/images/smobShops', `${i}.jpg`);
  axios({
    method: 'get',
    url: url3[i],
    responseType: 'stream',
  })
    .then((response) => {
      response.data.pipe(fs.createWriteStream(imagePath));
    });
}