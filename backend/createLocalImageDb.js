/* eslint-disable no-plusplus */
const faker = require('faker');
const axios = require('axios');
const path = require('path');
const fs = require('fs');

const url = [];
// 100 fake avatar url from fakter
for (let i = 0; i < 100; i++) {
  url.push(faker.image.people());
}


// download 100 user photoes to local image folder
for (let i = 0; i < url.length; i++) {
  // path for image storage
  const imagePath = path.join(__dirname, './images/people', `${i}.jpg`);
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
// 100 fake avatar url from fakter
for (let i = 0; i < 100; i++) {
  url2.push(faker.image.imageUrl(640, 480, 'animals', true));
}


// download 100 user photoes to local image folder
for (let i = 0; i < url2.length; i++) {
  // path for image storage
  const imagePath = path.join(__dirname, './images/products', `${i}.jpg`);
  axios({
    method: 'get',
    url: url2[i],
    responseType: 'stream',
  })
    .then((response) => {
      response.data.pipe(fs.createWriteStream(imagePath));
    });
}