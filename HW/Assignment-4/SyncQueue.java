// SyncQueue

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SyncQueue {
    // List of QueueNode objects
    private ArrayList<QueueNode> queue;

    // Maps a condition to a specific thread's TID (Thread ID).
    private Map<Integer, Integer> threadTidMap;

    public SyncQueue() {
        this(10); 
    }

    public SyncQueue(int capacity) {
        queue = new ArrayList<>(capacity);
        threadTidMap = new HashMap<>(capacity);
        for (int i = 0; i < capacity; i++) {
            queue.add(new QueueNode()); // Initialize each condition with a new QueueNode.
        }
    }

    // For thread to enqueue itself based on a condition and then go to sleep.
    public int enqueueAndSleep(int cond) {
        if (cond >= 0 && cond < queue.size()) { // Check for valid condition.
            int tid = Thread.currentThread().hashCode(); 
            threadTidMap.put(cond, tid); // Map the condition to the thread's TID.
            return queue.get(cond).sleep(); 
         }
        return -1; // Return -1 if the condition is invalid.
    }

    public void dequeueAndWakeup(int cond) {
        this.dequeueAndWakeup(cond, 0); }

    // Wakes up a thread waiting on a specified condition with a specific TID.
    public void dequeueAndWakeup(int cond, int tid) {
        if (cond >= 0 && cond < queue.size()) { 
            Integer mappedTid = threadTidMap.getOrDefault(cond, null); 
            if (mappedTid != null && (tid == 0 || tid == mappedTid)) { // If TID matches or is default (0) continue.
                queue.get(cond).wakeup(tid); 
                threadTidMap.remove(cond); // Clean up the mapping once the thread is woken up.
            }
        }
    }
}
