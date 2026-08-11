import React from 'react'
import VideoCard from './VideoCard'

export default function VideoGrid({videos}){
  return (
    <div className="px-4 pb-8">
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 auto-rows-max">
        {videos.length > 0 ? (
          videos.map(v => <VideoCard key={v._id} video={v} />)
        ) : (
          <div className="col-span-full text-center py-12 text-gray-500 dark:text-gray-400">
            <p className="text-lg">No videos found</p>
          </div>
        )}
      </div>
    </div>
  )
}
