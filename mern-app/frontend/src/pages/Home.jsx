import React, { useEffect, useState } from 'react'
import Sidebar from '../components/Sidebar'
import VideoGrid from '../components/VideoGrid'
import { api } from '../services/api'

export default function Home(){
  const [videos, setVideos] = useState([]);
  const [q, setQ] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(()=>{
    setLoading(true);
    api.getVideos(q)
      .then(setVideos)
      .catch(err=>{
        console.error('Failed to fetch videos:', err);
        setVideos([]);
      })
      .finally(()=> setLoading(false));
  }, [q]);

  return (
    <div className="flex">
      <Sidebar />
      <div className="flex-1 bg-white dark:bg-gray-950">
        <div className="p-6">
          {q && (
            <div className="mb-6">
              <h2 className="text-xl font-semibold text-gray-900 dark:text-white">Search results for "<span className="text-blue-600">{q}</span>"</h2>
            </div>
          )}
        </div>
        {loading ? (
          <div className="text-center py-12 text-gray-500">Loading videos...</div>
        ) : (
          <VideoGrid videos={videos} />
        )}
      </div>
    </div>
  )
}
