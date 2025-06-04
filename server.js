const express = require('express');
const cors = require('cors');
const { MongoClient } = require('mongodb');

const app = express();
app.use(cors());

const uri = 'mongodb://127.0.0.1:27017/'; // use iPv4 MongoDB address
const client = new MongoClient(uri);

const allowedCollections = ['test', 'Catelog'];  // the collection names

app.get('/data/:collectionName', async (req, res) => {
  const { collectionName } = req.params;

  // check the whitelist
  if (!allowedCollections.includes(collectionName)) {
    return res.status(400).json({ error: `Collection '${collectionName}' is not allowed.` });
  }

  try {
    await client.connect();
    const db = client.db('TextTech');
    const collection = db.collection(collectionName);
    const data = await collection.find().toArray();
    res.json(data);
  } catch (err) {
    console.error(`Error accessing collection ${collectionName}:`, err);
    res.status(500).send(err.stack);
  }
});

app.listen(3000, () => {
  console.log('Server running at http://localhost:3000');
});