const getClient = require('../db/mongoClient');
const DB_NAME = "dramaDatabase";
const COLLECTION_NAME = "texts";

async function getDrama(req, res) {
  const { title, author, publication_date } = req.query;
  const query = {};

  if (title) query.title = { $regex: title, $options: 'i' };
  if (author) query.author = { $regex: author, $options: 'i' };
  if (publication_date) query["source.printedSource.publication_date"] = { $regex: publication_date, $options: 'i' };

  try {
    const client = await getClient();
    const collection = client.db(DB_NAME).collection(COLLECTION_NAME);

    const result = await collection.find(query, {
      projection: {
        title: 1,
        author: 1,
        "source.printedSource.publication_date": 1,
        id: 1,
        _id: 0
      }
    }).toArray();

    const simplified = result.map(item => ({
      title: item.title || '',
      author: item.author || '',
      publication_date: item.source?.printedSource?.publication_date || '',
      id: item.id || ''
    }));

    res.json(simplified);
  } catch (err) {
    console.error("query fail: ", err);
    res.status(500).json({ error: "internal error" });
  }
}

async function getCharacterByDrama(req, res) {
  const { dramaId } = req.query;
  if (!dramaId) return res.status(400).json({ error: "missing dramaId" });

  try {
    const client = await getClient();
    const collection = client.db(DB_NAME).collection(COLLECTION_NAME);

    const result = await collection.findOne({ id: dramaId }, {
      projection: { personList: 1, _id: 0 }
    });

    if (!result) return res.status(404).json({ error: "not found" });
    res.json(result.personList || []);
  } catch (err) {
    console.error("fail: ", err);
    res.status(500).json({ error: "internal error" });
  }
}

async function getTextByDramaWithSpeaker(req, res) {
  const { dramaId, speakerID } = req.query;
  if (!dramaId) return res.status(400).json({ error: "missing dramaId" });

  try {
    const client = await getClient();
    const collection = client.db(DB_NAME).collection(COLLECTION_NAME);

    const result = await collection.findOne({ id: dramaId }, {
      projection: { "text.body": 1, _id: 0 }
    });

    if (!result) return res.status(404).json({ error: "not found" });

    let textList = result.text.body || [];
    if (speakerID) {
      textList = textList.filter(item => item.speakerID === speakerID);
    }

    res.json(textList);
  } catch (err) {
    console.error("fail: ", err);
    res.status(500).json({ error: "internal error" });
  }
}

async function getFullDrama(req, res) {
  const { id } = req.query;
  if (!id) return res.status(400).json({ error: "missing id" });

  try {
    const client = await getClient();
    const collection = client.db(DB_NAME).collection(COLLECTION_NAME);

    const result = await collection.findOne({ id }, { projection: { _id: 0 } });

    if (!result) return res.status(404).json({ error: "not found" });
    res.json(result);
  } catch (err) {
    console.error("fail: ", err);
    res.status(500).json({ error: "internal error" });
  }
}

module.exports = {
  getDrama,
  getCharacterByDrama,
  getTextByDramaWithSpeaker,
  getFullDrama
};
