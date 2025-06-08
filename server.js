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

app.get('/searchAuthor', async (req, res) => {
  const keyword = req.query.author?.toLowerCase();
  if (!keyword) return res.status(400).send("Missing author keyword");

  try {
    await client.connect();
    const db = client.db('TextTech');
    const collection = db.collection('drama');

    const query = {
      $or: [
        { "TEI.teiHeader.fileDesc.titleStmt.author.persName.forename": { $regex: keyword, $options: 'i' } },
        { "TEI.teiHeader.fileDesc.titleStmt.author.persName.surname":  { $regex: keyword, $options: 'i' } }
      ]
    };

    const results = await collection.find(query).limit(20).toArray();
    res.json(results);
  } catch (err) {
    console.error('Author search failed:', err);
    res.status(500).send(err.stack);
  }
});

app.get('/searchTitle', async (req, res) => {
  const keyword = req.query.title?.toLowerCase();
  if (!keyword) return res.status(400).send("Missing title keyword");

  try {
    await client.connect();
    const db = client.db('TextTech');
    const collection = db.collection('drama');

    const query = {
      "TEI.teiHeader.fileDesc.titleStmt.title": { $regex: keyword, $options: 'i' }
    };

    const results = await collection.find(query).limit(20).toArray();
    res.json(results);
  } catch (err) {
    console.error('Title search failed:', err);
    res.status(500).send(err.stack);
  }
});

app.listen(3000, () => {
  console.log('Server running at http://localhost:3000');
});