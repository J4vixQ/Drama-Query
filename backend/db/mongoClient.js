// Import MongoDB client and server API version
const { MongoClient, ServerApiVersion } = require('mongodb');

// MongoDB connection URI with cluster credentials
const uri = "mongodb+srv://gujianyang0808:2025@dramatext.pzpbe8l.mongodb.net/?retryWrites=true&w=majority&appName=dramaText";

// Create MongoDB client instance with server API configuration
const client = new MongoClient(uri, {
    serverApi: {
        version: ServerApiVersion.v1,
        strict: true,
        deprecationErrors: true,
    }
});

// Track connection state to avoid multiple connections
let isConnected = false;

/**
 * Get MongoDB client instance, establishing connection if not already connected
 * @returns {Promise<MongoClient>} Connected MongoDB client
 */
async function getClient() {
  if (!isConnected) {
    await client.connect();
    isConnected = true;
  }
  return client;
}

module.exports = getClient;
