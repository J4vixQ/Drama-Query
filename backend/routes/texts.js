const express = require('express');
const router = express.Router();
const { getDrama, getCharacterByDrama, getTextByDramaWithSpeaker, getFullDrama } = require('../controllers/textsController');

router.get('/api/getDrama', getDrama);
router.get('/api/getCharacterByDrama', getCharacterByDrama);
router.get('/api/getTextByDramaWithSpeaker', getTextByDramaWithSpeaker);
router.get('/api/getFullDrama', getFullDrama);


module.exports = router;
