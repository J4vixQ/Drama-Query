const { MongoClient, ServerApiVersion } = require('mongodb');

const uri = "mongodb+srv://gujianyang0808:2025@dramatext.pzpbe8l.mongodb.net/?retryWrites=true&w=majority&appName=dramaText";
const client = new MongoClient(uri, {
    serverApi: {
        version: ServerApiVersion.v1,
        strict: true,
        deprecationErrors: true,
    }
});

let isConnected = false;

async function getClient() {
  if (!isConnected) {
    await client.connect();
    isConnected = true;
  }
  return client;
}

module.exports = getClient;
