import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { api } from '../services/api'

function VideoPlayer({url}){
  const [error, setError] = useState(false);

  return (
    <div className="bg-black rounded-xl overflow-hidden aspect-video">
      {error ? (
        <div className="w-full h-full flex items-center justify-center text-white text-center p-4">
          <div>
            <div className="text-3xl mb-2">⚠️</div>
            <p>Video cannot be played</p>
            <p className="text-sm text-gray-400 mt-2">The video URL may not be accessible or in an unsupported format.</p>
            <p className="text-xs text-gray-500 mt-4 break-all">{url}</p>
          </div>
        </div>
      ) : (
        <video 
          controls 
          className="w-full h-full"
          onError={() => setError(true)}
          crossOrigin="anonymous"
        >
          <source src={url} type="video/mp4" />
          <source src={url} type="video/webm" />
          <source src={url} type="video/ogg" />
          Your browser does not support the video tag.
        </video>
      )}
    </div>
  )
}

export default function VideoPage(){
  const { id } = useParams();
  const [video, setVideo] = useState(null);
  const [comments, setComments] = useState([]);
  const [text, setText] = useState('');
  const [liked, setLiked] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(()=>{
    setLoading(true);
    // Load video from backend API
    api.getVideoById(id)
      .then(setVideo)
      .catch(err=>{ 
        console.error('Failed to fetch video:', err); 
        setVideo(null); 
      });

    // Load comments from backend API
    api.getComments(id)
      .then(setComments)
      .catch(err=>{ 
        console.error('Failed to fetch comments:', err);
        setComments([]);
      })
      .finally(()=> setLoading(false));
  },[id]);

  const submitComment = async ()=>{
    if (!text || !text.trim()) return;
    const token = localStorage.getItem('token');
    if (!token) return alert('Please log in to comment');
    
    try {
      const newComment = await api.addComment(id, text);
      setComments([newComment, ...comments]);
      setText('');
    } catch (err) {
      console.error('Failed to add comment:', err);
      alert('Failed to add comment');
    }
  }

  if (loading) return <div className="p-8 text-center">Loading...</div>
  if (!video) return <div className="p-8 text-center text-red-500">Video not found</div>

  return (
    <div className="bg-white dark:bg-gray-950">
      <div className="max-w-6xl mx-auto px-4 py-6 grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2">
          {/* Video Player */}
          <VideoPlayer url={video.url} />
          
          {/* Video Info */}
          <div className="mt-6">
            <h1 className="text-2xl font-bold text-gray-900 dark:text-white">{video.title}</h1>
            
            {/* Channel & Actions */}
            <div className="flex items-center justify-between mt-4 pb-4 border-b border-gray-200 dark:border-gray-800">
              <div className="flex items-center gap-3">
                <div className="w-12 h-12 rounded-full bg-gradient-to-br from-blue-400 to-purple-500"></div>
                <div>
                  <div className="font-semibold text-gray-900 dark:text-white">{video.channelName}</div>
                  <div className="text-xs text-gray-600 dark:text-gray-400">1.2M subscribers</div>
                </div>
                <button className="ml-4 px-4 py-2 bg-red-600 text-white rounded-full font-medium text-sm hover:bg-red-700 transition">
                  Subscribe
                </button>
              </div>
              
              {/* Like/Dislike */}
              <div className="flex items-center gap-2 bg-gray-100 dark:bg-gray-900 rounded-full p-1">
                <button 
                  onClick={() => setLiked(!liked)}
                  className={`px-4 py-2 rounded-full flex items-center gap-2 font-medium text-sm transition ${
                    liked 
                      ? 'bg-gray-200 dark:bg-gray-800 text-red-600' 
                      : 'hover:bg-gray-200 dark:hover:bg-gray-800 text-gray-700 dark:text-gray-300'
                  }`}
                >
                  👍 {liked ? video.likes + 1 : video.likes}
                </button>
                <button className="px-4 py-2 rounded-full text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-800 font-medium text-sm transition">
                  👎
                </button>
                <button className="px-4 py-2 rounded-full text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-800 font-medium text-sm transition">
                  📤
                </button>
              </div>
            </div>
            
            {/* Stats & Description */}
            <div className="mt-4 text-sm text-gray-600 dark:text-gray-400 space-y-2">
              <p className="font-semibold text-gray-900 dark:text-white">{video.views.toLocaleString()} views • Uploaded 2 days ago</p>
              <p className="text-gray-700 dark:text-gray-300">{video.description}</p>
            </div>
          </div>
          
          {/* Comments Section */}
          <div className="mt-8">
            <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4">{comments.length} Comments</h2>
            
            {/* Comment Input */}
            <div className="flex gap-4 mb-6">
              <div className="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex-shrink-0"></div>
              <div className="flex-1">
                <textarea 
                  className="w-full bg-transparent border-b border-gray-300 dark:border-gray-700 text-gray-900 dark:text-white placeholder-gray-600 dark:placeholder-gray-400 outline-none py-2 resize-none" 
                  placeholder="Add a comment..."
                  value={text} 
                  onChange={e=>setText(e.target.value)}
                  rows="2"
                />
                <div className="flex justify-end gap-2 mt-4">
                  <button 
                    onClick={() => setText('')}
                    className="px-4 py-2 text-gray-700 dark:text-gray-300 hover:bg-gray-100 dark:hover:bg-gray-800 rounded font-medium text-sm transition"
                  >
                    Cancel
                  </button>
                  <button 
                    onClick={submitComment}
                    className="px-4 py-2 bg-blue-600 text-white rounded font-medium text-sm hover:bg-blue-700 transition"
                  >
                    Comment
                  </button>
                </div>
              </div>
            </div>
            
            {/* Comments List */}
            <div className="space-y-6">
              {comments.map((c, idx) => (
                <div key={c._id} className="flex gap-4">
                  <div className="w-10 h-10 rounded-full bg-gray-300 dark:bg-gray-700 flex-shrink-0"></div>
                  <div className="flex-1">
                    <div className="flex items-baseline gap-2">
                      <span className="font-semibold text-sm text-gray-900 dark:text-white">{c.user?.username || 'User'}</span>
                      <span className="text-xs text-gray-600 dark:text-gray-400">{new Date(c.createdAt).toLocaleString()}</span>
                    </div>
                    <p className="mt-1 text-gray-700 dark:text-gray-300 text-sm">{c.text}</p>
                    <div className="flex gap-4 mt-2 text-xs">
                      <button className="text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white">👍</button>
                      <button className="text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white">👎</button>
                      <button className="text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white">Reply</button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
        
        {/* Recommendations Sidebar */}
        <aside className="hidden lg:block">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Recommended</h3>
          <div className="space-y-2">
            {[1, 2, 3].map(i => (
              <div key={i} className="flex gap-2 hover:bg-gray-100 dark:hover:bg-gray-800 p-2 rounded cursor-pointer group">
                <img src="https://placehold.co/120x68?text=Video+" alt="rec" className="w-32 h-20 rounded object-cover group-hover:opacity-80" />
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 dark:text-white line-clamp-2">Recommended Video Title</p>
                  <p className="text-xs text-gray-600 dark:text-gray-400 mt-1">Channel Name</p>
                  <p className="text-xs text-gray-600 dark:text-gray-400">1.2M views</p>
                </div>
              </div>
            ))}
          </div>
        </aside>
      </div>
    </div>
  )
}
