import { TaskStatus } from '../types';

const config: Record<TaskStatus, { label: string; classes: string }> = {
  TODO:        { label: 'To Do',       classes: 'bg-gray-100 text-gray-700' },
  IN_PROGRESS: { label: 'In Progress', classes: 'bg-blue-100 text-blue-700' },
  DONE:        { label: 'Done',        classes: 'bg-green-100 text-green-700' },
};

export function StatusBadge({ status }: { status: TaskStatus }) {
  const { label, classes } = config[status];
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${classes}`}>
      {label}
    </span>
  );
}
