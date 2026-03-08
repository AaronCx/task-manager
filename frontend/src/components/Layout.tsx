import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { NotificationsDropdown } from './NotificationsDropdown';

/**
 * App shell — top nav bar shown on authenticated pages.
 */
export function Layout({ children }: { children: React.ReactNode }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { isDark, toggle } = useTheme();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 transition-colors">
      {/* ── Navigation bar ───────────────────────────────────────────── */}
      <nav className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            {/* Logo / brand */}
            <Link to="/dashboard" className="flex items-center gap-2">
              <span className="text-2xl">✅</span>
              <span className="text-xl font-bold text-blue-600">TaskFlow</span>
            </Link>

            {/* Right side */}
            <div className="flex items-center gap-3">
              {user && (
                <Link to="/profile" className="text-sm text-gray-600 hidden sm:block hover:text-blue-600 transition-colors">
                  {user.firstName} {user.lastName}
                </Link>
              )}
              {/* Dark mode toggle */}
              <button
                onClick={toggle}
                aria-label="Toggle dark mode"
                className="p-2 rounded-lg text-gray-500 hover:text-gray-800 dark:text-gray-400
                           dark:hover:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
              >
                {isDark ? (
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round"
                          d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
                  </svg>
                ) : (
                  <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round"
                          d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
                  </svg>
                )}
              </button>
              {/* Notification bell */}
              <NotificationsDropdown />
              <Link
                to="/tasks/new"
                className="bg-blue-600 text-white px-4 py-2 rounded-lg text-sm font-medium
                           hover:bg-blue-700 transition-colors"
              >
                + New Task
              </Link>
              <button
                onClick={handleLogout}
                className="text-sm text-gray-500 hover:text-gray-800 transition-colors"
              >
                Log out
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* ── Page content ─────────────────────────────────────────────── */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>
    </div>
  );
}
