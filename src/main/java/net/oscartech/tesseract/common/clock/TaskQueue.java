package net.oscartech.tesseract.common.clock;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by tylaar on 15/4/26.
 */
class TaskQueue {

    private static class Task implements Comparable<Task> {
        final long taskNumber;
        final long t;
        final Runnable runnable;

        private Task(final long taskNumber, final long when, final Runnable runnable) {
            this.taskNumber = taskNumber;
            this.t = when;
            this.runnable = runnable;
        }

        @Override
        public int compareTo(Task o) {
            if (t == o.t) {
                return (taskNumber < o.taskNumber ? -1 : (taskNumber == o.taskNumber ? 0 : 1));
            } else {
                return (t < o.t) ? -1 : 1;
            }
        }
    }

    private AtomicLong taskCount = new AtomicLong();
    private final PriorityQueue<Task> taskQueue = new PriorityQueue<>();

    private synchronized void addTask(Task task) {
        taskQueue.add(task);
    }

    private synchronized List<Task> removeTasks(long now) {
        Task task ;
        List<Task> readyTasks = new ArrayList<>();
        while( (task = taskQueue.peek()) != null && task.t <= now) {
            readyTasks.add(task);
        }
        return readyTasks;
    }

    public void runTasks(long now) {
        List<Task> tasksToRun = removeTasks(now);
        for (Task task : removeTasks(now)) {
            runTask(task);
        }
    }

    private void runTask(Task task) {
        try {
            task.runnable.run();
        } catch (Throwable t) {
            System.out.println("this task is fucking down.");
        }
    }

    /**
     * If it's not the time yet, add it to the queue, otherwise, run it immediately.
     * @param now
     * @param when
     * @param runnable
     * @return
     */
    public boolean schedule(long now, long when, Runnable runnable) {
        runTasks(now);
        Task task = new Task(taskCount.getAndIncrement(), when, runnable);
        if (when <= now) {
            runTask(task);
            return true;
        } else {
            addTask(task);
            return false;
        }
    }
}
