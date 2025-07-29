// Import database client and define database constants
const getClient = require('../db/mongoClient');
const DB_NAME = "dramaDatabase";
const COLLECTION_NAME = "texts";

/**
 * Get drama documents based on search criteria
 * @param {Object} req - Express request object containing query parameters
 * @param {Object} res - Express response object
 */
async function getDrama(req, res) {
  // Extract search parameters from query string
  const { title, author, publication_date } = req.query;
  const query = {};

  // Build MongoDB query with case-insensitive regex matching
  if (title) query.title = { $regex: title, $options: 'i' };
  if (author) query.author = { $regex: author, $options: 'i' };
  if (publication_date) query["source.printedSource.publication_date"] = { $regex: publication_date, $options: 'i' };

  try {
    // Get database client and collection
    const client = await getClient();
    const collection = client.db(DB_NAME).collection(COLLECTION_NAME);

    // Execute query with specific field projection
    const result = await collection.find(query, {
      projection: {
        title: 1,
        author: 1,
        "source.printedSource.publication_date": 1,
        id: 1,
        _id: 0
      }
    }).toArray();

    // Transform results to simplified format with default values
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

/**
 * Get character list for a specific drama
 * @param {Object} req - Express request object containing dramaId query parameter
 * @param {Object} res - Express response object
 */
async function getCharacterByDrama(req, res) {
  const { dramaId } = req.query;
  // Validate required parameter
  if (!dramaId) return res.status(400).json({ error: "missing dramaId" });

  try {
    // Get database client and collection
    const client = await getClient();
    const collection = client.db(DB_NAME).collection(COLLECTION_NAME);

    // Find drama by ID and return only the person list
    const result = await collection.findOne({ id: dramaId }, {
      projection: { personList: 1, _id: 0 }
    });

    // Return 404 if drama not found, otherwise return character list
    if (!result) return res.status(404).json({ error: "not found" });
    res.json(result.personList || []);
  } catch (err) {
    console.error("fail: ", err);
    res.status(500).json({ error: "internal error" });
  }
}

/**
 * Get text content from a drama, optionally filtered by speaker
 * @param {Object} req - Express request object containing dramaId and optional speakerID
 * @param {Object} res - Express response object
 */
async function getTextByDramaWithSpeaker(req, res) {
  const { dramaId, speakerID } = req.query;
  // Validate required parameter
  if (!dramaId) return res.status(400).json({ error: "missing dramaId" });

  try {
    // Get database client and collection
    const client = await getClient();
    const collection = client.db(DB_NAME).collection(COLLECTION_NAME);

    // Find drama by ID and return only the text body
    const result = await collection.findOne({ id: dramaId }, {
      projection: { "text.body": 1, _id: 0 }
    });

    if (!result) return res.status(404).json({ error: "not found" });

    // Get text list and optionally filter by speaker
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

/**
 * Get complete drama document by ID
 * @param {Object} req - Express request object containing id query parameter
 * @param {Object} res - Express response object
 */
async function getFullDrama(req, res) {
  const { id } = req.query;
  // Validate required parameter
  if (!id) return res.status(400).json({ error: "missing id" });

  try {
    // Get database client and collection
    const client = await getClient();
    const collection = client.db(DB_NAME).collection(COLLECTION_NAME);

    // Find drama by ID and return complete document (excluding MongoDB _id)
    const result = await collection.findOne({ id }, { projection: { _id: 0 } });

    // Return 404 if drama not found, otherwise return complete drama data
    if (!result) return res.status(404).json({ error: "not found" });
    res.json(result);
  } catch (err) {
    console.error("fail: ", err);
    res.status(500).json({ error: "internal error" });
  }
}

// Export all controller functions
module.exports = {
  getDrama,
  getCharacterByDrama,
  getTextByDramaWithSpeaker,
  getFullDrama
};
