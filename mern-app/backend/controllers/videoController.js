const Video = require('../models/Video');

exports.getVideos = async (req, res) => {
  try {
    const q = req.query.q;
    let filter = {};
    if (q) {
      const re = new RegExp(q, 'i');
      filter = { $or: [{ title: re }, { channelName: re }] };
    }
    const videos = await Video.find(filter).sort({ uploadDate: -1 });
    res.json(videos);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.getVideoById = async (req, res) => {
  try {
    const video = await Video.findById(req.params.id);
    if (!video) return res.status(404).json({ message: 'Not found' });
    res.json(video);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.createVideo = async (req, res) => {
  try {
    const { title, description, url, thumbnailUrl, channelName } = req.body;
    const video = await Video.create({ title, description, url, thumbnailUrl, channelName });
    res.json(video);
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};

exports.likeVideo = async (req, res) => {
  try {
    const video = await Video.findById(req.params.id);
    if (!video) return res.status(404).json({ message: 'Not found' });
    video.likes = (video.likes || 0) + 1;
    await video.save();
    res.json({ likes: video.likes });
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: 'Server error' });
  }
};
