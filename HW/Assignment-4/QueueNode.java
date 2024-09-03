// QueueNode 

import java.util.LinkedList;
import java.util.Queue;

class QueueNode {
    // Queue to hold thread IDs (TIDs). We're using LinkedList which implements Queue interface,
    // offering FIFO (First-In-First-Out)
    private Queue<Integer> queue;

    // Constructor initializes for the queue.
    public QueueNode() {
        this.queue = new LinkedList<>(); // LinkedList is chosen for its efficient queue operations.
    }

    // Puts a thread into a waiting state until it can be woken up by another thread.
    public synchronized int sleep() {
        try {
            // Loop to wait conditionally. 
            // A thread should continue waiting if the queue is still empty after being woken up.
            while (queue.isEmpty()) {
                wait(); 
            }
        } catch (InterruptedException e) {
            System.out.println("Thread interrupted during sleep");
        }

        // After being notified and finding a non-empty queue, the thread removes and returns
        return queue.poll(); 
        }

    // Wakes up a thread that's waiting on this QueueNode.
    public synchronized void wakeup(int tid) {
        queue.offer(tid); // Inserts the specified element into this queue.
        notify(); 
    }
}
