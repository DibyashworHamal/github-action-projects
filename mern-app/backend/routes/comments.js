const express = require('express');
const router = express.Router();
const { addComment, getComments } = require('../controllers/commentController');
const auth = require('../controllers/authMiddleware');

router.post('/', auth, addComment);
router.get('/:videoId', getComments);

module.exports = router;
