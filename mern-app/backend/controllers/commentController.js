const Comment = require('../models/Comment');
const Video = require('../models/Video');

exports.addComment = async (req, res) => {
  try {
    const { videoId, text } = req.body;
    const video = await Video.findById(videoId);
    if (!video) return res.status(404).json({ message: 'Video not found' });
    const comment = await Comment.create({ video: videoId, user: req.userId, text });
    res.json(comment);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.getComments = async (req, res) => {
  try {
    const comments = await Comment.find({ video: req.params.videoId }).populate('user', 'username avatar').sort({ createdAt: -1 });
    res.json(comments);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};
