// Import required modules
const express = require('express');
const cors = require('cors');

// Initialize Express application
const app = express();
const port = 3000;

// Enable Cross-Origin Resource Sharing for all routes
app.use(cors());

// Mount routes
const textRoutes = require('./routes/texts');
app.use('/', textRoutes);

// Start the server and listen on the specified port
app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});
