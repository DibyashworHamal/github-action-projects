import React from 'react'
import { Link } from 'react-router-dom'

export default function VideoCard({video}){
  const formatViews = (views) => {
    if (views >= 1000000) return (views / 1000000).toFixed(1) + 'M';
    if (views >= 1000) return (views / 1000).toFixed(1) + 'K';
    return views;
  };

  const formatDate = (date) => {
    const d = new Date(date);
    const now = new Date();
    const diff = Math.floor((now - d) / 1000);
    if (diff < 60) return 'just now';
    if (diff < 3600) return Math.floor(diff / 60) + 'm ago';
    if (diff < 86400) return Math.floor(diff / 3600) + 'h ago';
    if (diff < 604800) return Math.floor(diff / 86400) + 'd ago';
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: d.getFullYear() !== now.getFullYear() ? 'numeric' : undefined });
  };

  return (
    <div className="w-full group cursor-pointer">
      <Link to={`/video/${video._id}`} className="block">
        <div className="relative overflow-hidden rounded-xl bg-gray-200 dark:bg-gray-800">
          <img 
            src={video.thumbnailUrl} 
            alt={video.title} 
            className="w-full h-44 object-cover group-hover:scale-105 transition duration-200" 
          />
          <div className="absolute bottom-2 right-2 bg-black text-white text-xs px-2 py-1 rounded">3:45</div>
        </div>
        <div className="mt-3 flex gap-2">
          <div className="flex-shrink-0">
            <div className="w-9 h-9 rounded-full bg-gradient-to-br from-blue-400 to-purple-500"></div>
          </div>
          <div className="flex-1 min-w-0">
            <h3 className="font-medium text-sm text-gray-900 dark:text-white line-clamp-2 group-hover:text-gray-600 dark:group-hover:text-gray-300">
              {video.title}
            </h3>
            <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">{video.channelName}</p>
            <p className="text-xs text-gray-600 dark:text-gray-400">
              {formatViews(video.views)} views • {formatDate(video.uploadDate)}
            </p>
          </div>
        </div>
      </Link>
    </div>
  )
}
