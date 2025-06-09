const express = require('express');
const cors = require('cors');
const { MongoClient } = require('mongodb');

const app = express();
app.use(cors());

const uri = 'mongodb://127.0.0.1:27017/'; // use iPv4 MongoDB address
const client = new MongoClient(uri);

app.get('/searchCombined', async (req, res) => {
  const title = req.query.title?.toLowerCase();
  const author = req.query.author?.toLowerCase();

  if (!title && !author) {
    return res.status(400).send("At least one of title or author is required.");
  }

  try {
    await client.connect();
    const db = client.db('TextTech');
    const collection = db.collection('drama');

    const query = { $and: [] };

    if (title) {
      query.$and.push({
        "TEI.teiHeader.fileDesc.titleStmt.titleText": { $regex: title, $options: 'i' }
      });
    }

    if (author) {
      query.$and.push({
        $or: [
          { "TEI.teiHeader.fileDesc.titleStmt.author.persName.forename": { $regex: author, $options: 'i' } },
          { "TEI.teiHeader.fileDesc.titleStmt.author.persName.surname": { $regex: author, $options: 'i' } }
        ]
      });
    }

    const results = await collection.find(query.$and.length > 0 ? query : {}).limit(10).toArray();
    res.json(results);
  } catch (err) {
    console.error('Combined search failed:', err);
    res.status(500).send(err.stack);
  }
});

app.listen(3000, () => {
  console.log('Server running at http://localhost:3000');
});