// Import Express router and controller functions
const express = require('express');
const router = express.Router();
const { getDrama, getCharacterByDrama, getTextByDramaWithSpeaker, getFullDrama } = require('../controllers/textsController');

// Define API routes and map them to controller functions
router.get('/api/getDrama', getDrama);                           // Search dramas by title, author, or publication date
router.get('/api/getCharacterByDrama', getCharacterByDrama);     // Get character list for a specific drama
router.get('/api/getTextByDramaWithSpeaker', getTextByDramaWithSpeaker); // Get text content, optionally filtered by speaker
router.get('/api/getFullDrama', getFullDrama);                   // Get complete drama document

// Export the router for use in the main application
module.exports = router;
