import React, { useState } from 'react'
import { api } from '../services/api'

export default function Header(){
  const [username, setUsername] = useState(localStorage.getItem('username') || '');
  const [showMenu, setShowMenu] = useState(false);
  const [showAuth, setShowAuth] = useState(false);
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [regUsername, setRegUsername] = useState('');

  const handleAuth = async (e) => {
    e.preventDefault();
    try {
      if (isLogin) {
        await api.login(email, password);
      } else {
        await api.register(regUsername, email, password);
      }
      setUsername(localStorage.getItem('username'));
      setShowAuth(false);
      setEmail('');
      setPassword('');
      setRegUsername('');
    } catch (err) {
      alert(err.message);
    }
  };

  const handleLogout = () => {
    api.logout();
    setUsername('');
    setShowMenu(false);
  };

  return (
    <header className="bg-white dark:bg-gray-950 border-b border-gray-200 dark:border-gray-800 sticky top-0 z-50">
      <div className="max-w-full mx-auto px-4 py-2 flex items-center justify-between">
        {/* Logo */}
        <div className="flex items-center gap-2">
          <div className="text-2xl font-bold text-red-600">▶</div>
          <div className="text-xl font-semibold text-gray-900 dark:text-white">YouTubeClone</div>
        </div>

        {/* Search Bar */}
        <div className="flex-1 max-w-lg mx-8">
          <div className="flex items-center border border-gray-300 dark:border-gray-600 rounded-full bg-gray-50 dark:bg-gray-900">
            <input 
              type="text"
              placeholder="Search videos" 
              className="flex-1 bg-transparent px-4 py-2 outline-none text-sm dark:text-white" 
            />
            <button className="px-4 py-2 text-gray-600 dark:text-gray-400 hover:bg-gray-200 dark:hover:bg-gray-800 rounded-full">
              🔍
            </button>
          </div>
        </div>

        {/* Right Menu */}
        <div className="flex items-center gap-4">
          <button className="p-2 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-full text-xl">🔔</button>
          
          {!username ? (
            <button 
              onClick={() => setShowAuth(true)}
              className="px-4 py-2 bg-red-600 text-white rounded-full font-medium text-sm hover:bg-red-700 transition"
            >
              Sign In
            </button>
          ) : (
            <div className="relative">
              <button 
                onClick={() => setShowMenu(!showMenu)}
                className="w-10 h-10 rounded-full bg-gradient-to-br from-blue-400 to-purple-500 flex items-center justify-center text-white font-bold hover:shadow-lg transition"
              >
                {username[0].toUpperCase()}
              </button>
              {showMenu && (
                <div className="absolute right-0 mt-2 bg-white dark:bg-gray-900 border border-gray-200 dark:border-gray-700 rounded-lg shadow-lg w-48 py-2">
                  <div className="px-4 py-2 border-b border-gray-200 dark:border-gray-700">
                    <div className="text-sm font-semibold">{username}</div>
                  </div>
                  <button 
                    onClick={handleLogout}
                    className="w-full text-left px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-800 text-sm text-red-600"
                  >
                    Logout
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Auth Modal */}
      {showAuth && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white dark:bg-gray-900 rounded-lg p-6 w-96 shadow-lg">
            <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
              {isLogin ? 'Sign In' : 'Sign Up'}
            </h2>
            <form onSubmit={handleAuth} className="space-y-4">
              {!isLogin && (
                <input
                  type="text"
                  placeholder="Username"
                  value={regUsername}
                  onChange={(e) => setRegUsername(e.target.value)}
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-red-500"
                  required
                />
              )}
              <input
                type="email"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-red-500"
                required
              />
              <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded bg-gray-50 dark:bg-gray-800 text-gray-900 dark:text-white outline-none focus:ring-2 focus:ring-red-500"
                required
              />
              <button
                type="submit"
                className="w-full px-4 py-2 bg-red-600 text-white rounded font-medium hover:bg-red-700 transition"
              >
                {isLogin ? 'Sign In' : 'Sign Up'}
              </button>
            </form>
            <button
              onClick={() => setIsLogin(!isLogin)}
              className="w-full mt-4 text-sm text-gray-600 dark:text-gray-400 hover:text-gray-900 dark:hover:text-white"
            >
              {isLogin ? "Don't have an account? Sign Up" : 'Already have an account? Sign In'}
            </button>
            <button
              onClick={() => setShowAuth(false)}
              className="absolute top-4 right-4 text-gray-600 dark:text-gray-400 text-2xl"
            >
              ✕
            </button>
          </div>
        </div>
      )}
    </header>
  )
}
