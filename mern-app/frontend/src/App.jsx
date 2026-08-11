import React from 'react'
import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import VideoPage from './pages/VideoPage'
import Upload from './pages/Upload'
import Header from './components/Header'

export default function App(){
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 text-gray-900 dark:text-gray-100">
      <Header />
      <Routes>
        <Route path="/" element={<Home/>} />
        <Route path="/video/:id" element={<VideoPage/>} />
        <Route path="/upload" element={<Upload/>} />
      </Routes>
    </div>
  )
}
