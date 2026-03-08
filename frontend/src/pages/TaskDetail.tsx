import { useEffect, useState, FormEvent } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { AxiosError } from 'axios';
import { tasksApi } from '../api/tasks';
import { usersApi, UserSummary } from '../api/users';
import { Task, TaskPriority, TaskRequest, TaskStatus } from '../types';
import { Layout } from '../components/Layout';
import { ApiError } from '../types';

/**
 * Task detail / editor page.
 *
 * Route: /tasks/new    → create mode
 * Route: /tasks/:id    → edit mode (loads existing task)
 */
export function TaskDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isNew = id === 'new';

  const [task,    setTask]    = useState<Task | null>(null);
  const [users,   setUsers]   = useState<UserSummary[]>([]);
  const [loading, setLoading] = useState(!isNew);
  const [saving,  setSaving]  = useState(false);
  const [error,   setError]   = useState<string | null>(null);

  const [form, setForm] = useState<TaskRequest>({
    title:        '',
    description:  '',
    status:       'TODO',
    priority:     'MEDIUM',
    dueDate:      '',
    assignedToId: null,
  });

  // Load users for assignment dropdown
  useEffect(() => {
    usersApi.getAll()
      .then((res) => setUsers(res.data))
      .catch(() => { /* non-critical */ });
  }, []);

  // Load existing task in edit mode
  useEffect(() => {
    if (isNew) return;

    tasksApi.getById(Number(id))
      .then((res) => {
        const t = res.data;
        setTask(t);
        setForm({
          title:        t.title,
          description:  t.description ?? '',
          status:       t.status,
          priority:     t.priority,
          dueDate:      t.dueDate ?? '',
          assignedToId: t.assignedToId,
        });
      })
      .catch(() => setError('Task not found or you don\'t have permission to view it.'))
      .finally(() => setLoading(false));
  }, [id, isNew]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    if (name === 'assignedToId') {
      setForm((prev) => ({ ...prev, assignedToId: value ? Number(value) : null }));
    } else {
      setForm((prev) => ({ ...prev, [name]: value || undefined }));
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSaving(true);

    const payload: TaskRequest = {
      ...form,
      dueDate: form.dueDate || null,
    };

    try {
      if (isNew) {
        await tasksApi.create(payload);
      } else {
        await tasksApi.update(Number(id), payload);
      }
      navigate('/dashboard');
    } catch (err) {
      const axiosErr = err as AxiosError<ApiError>;
      const fieldErrors = axiosErr.response?.data?.fieldErrors;
      if (fieldErrors) {
        setError(Object.values(fieldErrors).join(' · '));
      } else {
        setError(axiosErr.response?.data?.message ?? 'Save failed. Please try again.');
      }
    } finally {
      setSaving(false);
    }
  };

  // ── Render loading / error states ─────────────────────────────────
  if (loading) {
    return (
      <Layout>
        <div className="flex justify-center py-20">
          <div className="w-8 h-8 border-4 border-blue-500 border-t-transparent
                          rounded-full animate-spin" />
        </div>
      </Layout>
    );
  }

  if (error && !isNew && !task) {
    return (
      <Layout>
        <div className="p-4 bg-red-50 text-red-700 rounded-lg text-sm">{error}</div>
      </Layout>
    );
  }

  return (
    <Layout>
      <div className="max-w-2xl mx-auto">
        {/* Back link */}
        <button
          onClick={() => navigate(-1)}
          className="text-sm text-blue-600 hover:underline mb-6 flex items-center gap-1"
        >
          ← Back to dashboard
        </button>

        <div className="bg-white rounded-2xl border border-gray-200 shadow-sm p-8">
          <h1 className="text-2xl font-bold text-gray-900 mb-6">
            {isNew ? 'New Task' : 'Edit Task'}
          </h1>

          {error && (
            <div className="mb-5 p-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-700">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            {/* Title */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Title <span className="text-red-500">*</span>
              </label>
              <input
                name="title"
                type="text"
                required
                value={form.title}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm
                           focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="What needs to be done?"
              />
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
              <textarea
                name="description"
                rows={4}
                value={form.description}
                onChange={handleChange}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm
                           focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                           resize-none"
                placeholder="Add more details (optional)..."
              />
            </div>

            {/* Status + Priority */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Status</label>
                <select
                  name="status"
                  value={form.status}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm
                             focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                             bg-white"
                >
                  {(['TODO', 'IN_PROGRESS', 'DONE'] as TaskStatus[]).map((s) => (
                    <option key={s} value={s}>
                      {s === 'TODO' ? 'To Do' : s === 'IN_PROGRESS' ? 'In Progress' : 'Done'}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Priority</label>
                <select
                  name="priority"
                  value={form.priority}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm
                             focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                             bg-white"
                >
                  {(['LOW', 'MEDIUM', 'HIGH'] as TaskPriority[]).map((p) => (
                    <option key={p} value={p}>
                      {p.charAt(0) + p.slice(1).toLowerCase()}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Due date + Assignee */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Due date</label>
                <input
                  name="dueDate"
                  type="date"
                  value={form.dueDate ?? ''}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm
                             focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Assign to</label>
                <select
                  name="assignedToId"
                  value={form.assignedToId ?? ''}
                  onChange={handleChange}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm
                             focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent
                             bg-white"
                >
                  <option value="">Unassigned</option>
                  {users.map((u) => (
                    <option key={u.id} value={u.id}>
                      {u.firstName} {u.lastName}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            {/* Actions */}
            <div className="flex gap-3 pt-2">
              <button
                type="submit"
                disabled={saving}
                className="flex-1 py-2.5 bg-blue-600 text-white rounded-lg text-sm font-semibold
                           hover:bg-blue-700 transition-colors disabled:opacity-60"
              >
                {saving ? 'Saving...' : isNew ? 'Create task' : 'Save changes'}
              </button>
              <button
                type="button"
                onClick={() => navigate('/dashboard')}
                className="px-6 py-2.5 border border-gray-300 text-gray-700 rounded-lg text-sm
                           font-medium hover:bg-gray-50 transition-colors"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
}
