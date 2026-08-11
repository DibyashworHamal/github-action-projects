const express = require('express');
const router = express.Router();
const { getVideos, getVideoById, createVideo, likeVideo } = require('../controllers/videoController');
const auth = require('../controllers/authMiddleware');

router.get('/', getVideos);
router.get('/:id', getVideoById);
router.post('/', auth, createVideo);
router.post('/:id/like', auth, likeVideo);

module.exports = router;
