import React from 'react'
import { Link } from 'react-router-dom'

export default function Sidebar(){
  const items = [
    { icon: '🏠', label: 'Home' },
    { icon: '🔥', label: 'Trending' },
    { icon: '📺', label: 'Subscriptions' },
    { icon: '📚', label: 'Library' },
    { icon: '⏱️', label: 'History' },
  ];

  return (
    <aside className="w-64 p-4 hidden md:block bg-white dark:bg-gray-950 border-r border-gray-200 dark:border-gray-800 overflow-y-auto h-screen sticky top-0">
      <Link 
        to="/upload"
        className="w-full mb-4 px-3 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 transition flex items-center gap-3 text-sm font-medium justify-center"
      >
        📤 Upload
      </Link>
      <hr className="my-4 border-gray-200 dark:border-gray-800" />
      <ul className="space-y-1">
        {items.map((item) => (
          <li key={item.label}>
            <button className="w-full text-left px-3 py-3 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-800 transition flex items-center gap-3 text-sm font-medium">
              <span className="text-xl">{item.icon}</span>
              <span className="text-gray-700 dark:text-gray-300">{item.label}</span>
            </button>
          </li>
        ))}
      </ul>
      <hr className="my-4 border-gray-200 dark:border-gray-800" />
      <div className="text-xs text-gray-500 px-3 space-y-2">
        <p className="font-semibold text-gray-700 dark:text-gray-400">PLAYLISTS</p>
        <p className="hover:text-gray-700 dark:hover:text-gray-300 cursor-pointer">Watch Later</p>
        <p className="hover:text-gray-700 dark:hover:text-gray-300 cursor-pointer">Liked Videos</p>
      </div>
    </aside>
  )
}
