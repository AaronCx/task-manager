import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { AxiosError } from 'axios';
import { tasksApi } from '../api/tasks';
import { Task, TaskStatus } from '../types';
import { Layout } from '../components/Layout';
import { StatusBadge } from '../components/StatusBadge';
import { PriorityBadge } from '../components/PriorityBadge';
import { useAuth } from '../context/AuthContext';
import { format } from 'date-fns';

const STATUS_FILTERS: { label: string; value: TaskStatus | 'ALL' }[] = [
  { label: 'All',         value: 'ALL' },
  { label: 'To Do',       value: 'TODO' },
  { label: 'In Progress', value: 'IN_PROGRESS' },
  { label: 'Done',        value: 'DONE' },
];

/**
 * Dashboard — main view showing the authenticated user's task list.
 * Supports status filtering and delete operations.
 */
export function Dashboard() {
  const { user } = useAuth();

  const [tasks,       setTasks]       = useState<Task[]>([]);
  const [filter,      setFilter]      = useState<TaskStatus | 'ALL'>('ALL');
  const [loading,     setLoading]     = useState(true);
  const [error,       setError]       = useState<string | null>(null);
  const [deletingId,  setDeletingId]  = useState<number | null>(null);

  // Fetch tasks whenever the filter changes
  useEffect(() => {
    setLoading(true);
    setError(null);

    const status = filter === 'ALL' ? undefined : filter;

    tasksApi.getAll(status)
      .then((res) => setTasks(res.data))
      .catch((err: AxiosError) => {
        setError('Failed to load tasks. Please refresh.');
        console.error(err);
      })
      .finally(() => setLoading(false));
  }, [filter]);

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this task?')) return;
    setDeletingId(id);
    try {
      await tasksApi.remove(id);
      setTasks((prev) => prev.filter((t) => t.id !== id));
    } catch {
      alert('Failed to delete task.');
    } finally {
      setDeletingId(null);
    }
  };

  // ── Stats cards ───────────────────────────────────────────────────
  const stats = {
    total:      tasks.length,
    todo:       tasks.filter((t) => t.status === 'TODO').length,
    inProgress: tasks.filter((t) => t.status === 'IN_PROGRESS').length,
    done:       tasks.filter((t) => t.status === 'DONE').length,
  };

  return (
    <Layout>
      {/* ── Page header ────────────────────────────────────────────── */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">
          Good {getGreeting()}, {user?.firstName} 👋
        </h1>
        <p className="mt-1 text-gray-500">Here's what's on your plate today.</p>
      </div>

      {/* ── Stats row ──────────────────────────────────────────────── */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-8">
        {[
          { label: 'Total',       value: stats.total,      color: 'text-gray-700',  bg: 'bg-gray-50'   },
          { label: 'To Do',       value: stats.todo,       color: 'text-gray-600',  bg: 'bg-gray-50'   },
          { label: 'In Progress', value: stats.inProgress, color: 'text-blue-700',  bg: 'bg-blue-50'   },
          { label: 'Done',        value: stats.done,       color: 'text-green-700', bg: 'bg-green-50'  },
        ].map(({ label, value, color, bg }) => (
          <div key={label} className={`${bg} rounded-xl p-4 border border-gray-100`}>
            <p className="text-xs font-medium text-gray-500 uppercase tracking-wide">{label}</p>
            <p className={`text-3xl font-bold mt-1 ${color}`}>{value}</p>
          </div>
        ))}
      </div>

      {/* ── Filter tabs ────────────────────────────────────────────── */}
      <div className="flex gap-1 mb-6 bg-gray-100 p-1 rounded-lg w-fit">
        {STATUS_FILTERS.map(({ label, value }) => (
          <button
            key={value}
            onClick={() => setFilter(value)}
            className={`px-4 py-1.5 rounded-md text-sm font-medium transition-colors ${
              filter === value
                ? 'bg-white text-gray-900 shadow-sm'
                : 'text-gray-500 hover:text-gray-800'
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {/* ── Task list ──────────────────────────────────────────────── */}
      {loading && (
        <div className="flex justify-center py-20">
          <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent
                          rounded-full animate-spin" />
        </div>
      )}

      {error && (
        <div className="p-4 bg-red-50 text-red-700 rounded-lg text-sm">{error}</div>
      )}

      {!loading && !error && tasks.length === 0 && (
        <div className="text-center py-20">
          <span className="text-5xl">📋</span>
          <p className="mt-4 text-lg font-medium text-gray-600">No tasks yet</p>
          <p className="text-sm text-gray-400 mt-1">Create your first task to get started</p>
          <Link
            to="/tasks/new"
            className="mt-4 inline-block bg-blue-600 text-white px-6 py-2 rounded-lg
                       text-sm font-medium hover:bg-blue-700 transition-colors"
          >
            Create task
          </Link>
        </div>
      )}

      {!loading && !error && tasks.length > 0 && (
        <div className="space-y-3">
          {tasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              onDelete={handleDelete}
              isDeleting={deletingId === task.id}
            />
          ))}
        </div>
      )}
    </Layout>
  );
}

// ── Task card sub-component ───────────────────────────────────────────────────

function TaskCard({
  task,
  onDelete,
  isDeleting,
}: {
  task: Task;
  onDelete: (id: number) => void;
  isDeleting: boolean;
}) {
  const isOverdue =
    task.dueDate && task.status !== 'DONE' && new Date(task.dueDate) < new Date();

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-5
                    hover:border-blue-300 hover:shadow-sm transition-all group">
      <div className="flex items-start justify-between gap-4">
        {/* Left: title + meta */}
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-2 flex-wrap">
            <h3 className="text-sm font-semibold text-gray-900 truncate">
              {task.title}
            </h3>
            <StatusBadge   status={task.status}   />
            <PriorityBadge priority={task.priority} />
          </div>

          {task.description && (
            <p className="mt-1 text-sm text-gray-500 line-clamp-2">{task.description}</p>
          )}

          <div className="mt-2 flex items-center gap-4 text-xs text-gray-400">
            {task.dueDate && (
              <span className={isOverdue ? 'text-red-500 font-medium' : ''}>
                📅 {isOverdue ? 'Overdue: ' : 'Due: '}
                {format(new Date(task.dueDate), 'MMM d, yyyy')}
              </span>
            )}
            {task.assignedToName && (
              <span>👤 {task.assignedToName}</span>
            )}
          </div>
        </div>

        {/* Right: actions */}
        <div className="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
          <Link
            to={`/tasks/${task.id}`}
            className="text-xs text-blue-600 hover:text-blue-800 font-medium px-3 py-1.5
                       border border-blue-200 rounded-lg hover:bg-blue-50 transition-colors"
          >
            Edit
          </Link>
          <button
            onClick={() => onDelete(task.id)}
            disabled={isDeleting}
            className="text-xs text-red-600 hover:text-red-800 font-medium px-3 py-1.5
                       border border-red-200 rounded-lg hover:bg-red-50 transition-colors
                       disabled:opacity-50"
          >
            {isDeleting ? '…' : 'Delete'}
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Helpers ───────────────────────────────────────────────────────────────────

function getGreeting(): string {
  const hour = new Date().getHours();
  if (hour < 12) return 'morning';
  if (hour < 18) return 'afternoon';
  return 'evening';
}
