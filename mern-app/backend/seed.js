require('dotenv').config();
const mongoose = require('mongoose');
const User = require('./models/User');
const Video = require('./models/Video');

const MONGO = process.env.MONGO_URI;

const videos = [
  {
    title: 'Relaxing Music 1',
    description: 'Chill music for work and study',
    url: 'https://www.w3schools.com/html/mov_bbb.mp4',
    thumbnailUrl: 'https://placehold.co/640x360?text=Relaxing+1',
    channelName: 'Chill Beats',
    views: 12345
  },
  {
    title: 'Relaxing Music 2',
    description: 'Ambient sounds',
    url: 'https://www.w3schools.com/html/movie.mp4',
    thumbnailUrl: 'https://placehold.co/640x360?text=Relaxing+2',
    channelName: 'Ambient World',
    views: 9876
  },
  {
    title: 'JavaScript Tutorial for Beginners',
    description: 'Learn JavaScript from scratch. Perfect for beginners!',
    url: 'https://www.w3schools.com/html/mov_bbb.mp4',
    thumbnailUrl: 'https://placehold.co/640x360?text=JavaScript+Tutorial',
    channelName: 'Code Academy',
    views: 125000
  },
  {
    title: 'Web Development in 2024',
    description: 'Complete guide to modern web development',
    url: 'https://www.w3schools.com/html/movie.mp4',
    thumbnailUrl: 'https://placehold.co/640x360?text=Web+Dev+2024',
    channelName: 'Tech Channel',
    views: 89432
  },
  {
    title: 'React Best Practices',
    description: 'Learn best practices for React development',
    url: 'https://www.w3schools.com/html/mov_bbb.mp4',
    thumbnailUrl: 'https://placehold.co/640x360?text=React+Practices',
    channelName: 'React Guru',
    views: 54321
  },
  {
    title: 'CSS Grid Layout Mastery',
    description: 'Master CSS Grid with real-world examples',
    url: 'https://www.w3schools.com/html/movie.mp4',
    thumbnailUrl: 'https://placehold.co/640x360?text=CSS+Grid',
    channelName: 'Design Master',
    views: 43210
  },
  {
    title: 'Node.js Backend Development',
    description: 'Build scalable backends with Node.js and Express',
    url: 'https://www.w3schools.com/html/mov_bbb.mp4',
    thumbnailUrl: 'https://placehold.co/640x360?text=Node.js',
    channelName: 'Backend Pro',
    views: 76543
  },
  {
    title: 'MongoDB Deep Dive',
    description: 'Understanding MongoDB: From basics to advanced',
    url: 'https://www.w3schools.com/html/movie.mp4',
    thumbnailUrl: 'https://placehold.co/640x360?text=MongoDB',
    channelName: 'Database Expert',
    views: 65432
  }
];

async function seed(){
  if (!MONGO) return console.error('Set MONGO_URI in .env');
  await mongoose.connect(MONGO);
  console.log('Connected');
  await User.deleteMany({});
  await Video.deleteMany({});
  const u = await User.create({ username: 'demo', email: 'demo@example.com', password: await require('bcryptjs').hash('password', 10), avatar: 'https://placehold.co/80x80' });
  for (const v of videos){
    await Video.create({ ...v, channelName: v.channelName });
  }
  console.log('Seeded');
  process.exit(0);
}

seed().catch(err=>{console.error(err); process.exit(1)});
