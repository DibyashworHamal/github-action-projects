const mongoose = require('mongoose');

const VideoSchema = new mongoose.Schema({
  title: {type: String, required: true},
  description: {type: String},
  url: {type: String, required: true},
  thumbnailUrl: {type: String},
  channelName: {type: String, required: true},
  views: {type: Number, default: 0},
  likes: {type: Number, default: 0},
  uploadDate: {type: Date, default: Date.now}
});

module.exports = mongoose.model('Video', VideoSchema);
