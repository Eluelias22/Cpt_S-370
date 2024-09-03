import java.util.*;

public class Scheduler extends Thread {
    // Define three queues with different time quanta: 500ms, 1000ms, 
    // and 2000ms respectively.
    private Vector Queue0; // priority time quantum 500ms
    private Vector Queue1; // priority time quantum 1000ms
    private Vector Queue2; // priority time quantum 2000ms

    private int timeSlice;
    // Default time slice for tasks
    private static final int DEFAULT_TIME_SLICE = 1000;

    // New data added to p161
    private boolean[] tids; // Indicate which ids have been used
    private static final int DEFAULT_MAX_THREADS = 10000;

    // A new feature added to p161
    // Allocate an ID array, each element indicating if that id has been used
    private int nextId = 0;
    // Initialize thread ID tracking array
    private void initTid(int maxThreads) {
        tids = new boolean[maxThreads];
        for (int i = 0; i < maxThreads; i++)
            tids[i] = false;
    }

    // A new feature added to p161
    // Search an available thread ID and provide a new thread with this ID
    private int getNewTid() {
        for (int i = 0; i < tids.length; i++) {
            int tentative = (nextId + i) % tids.length;
            if (tids[tentative] == false) {
                tids[tentative] = true;
                nextId = (tentative + 1) % tids.length;
                return tentative;
            }
        }
        return -1;
    }

    // A new feature added to p161
    // Return the thread ID and set the corresponding tids element to be unused
    private boolean returnTid(int tid) {
        if (tid >= 0 && tid < tids.length && tids[tid] == true) {
            tids[tid] = false;
            return true;
        }
        return false;
    }

    // A new feature added to p161
    // Retrieve the current thread's from any of the three queues. 
    public TCB getMyTcb() {
        // Get the current thread object
        Thread myThread = Thread.currentThread();  
        synchronized (Queue0) {
            for (int i = 0; i < Queue0.size(); i++) {
                TCB tcb = (TCB) Queue0.elementAt(i);
                Thread thread = tcb.getThread();
                if (thread == myThread) 
                    return tcb;
            }
        }

        synchronized (Queue1) {
            for (int i = 0; i < Queue1.size(); i++) {
                TCB tcb = (TCB) Queue1.elementAt(i);
                Thread thread = tcb.getThread();
                if (thread == myThread)
                    return tcb;
            }
        }

        synchronized (Queue2) {
            for (int i = 0; i < Queue2.size(); i++) {
                TCB tcb = (TCB) Queue2.elementAt(i);
                Thread thread = tcb.getThread();
                if (thread == myThread) 
                    return tcb;
            }
        }
        // Return null if the TCB is not found.
        return null; 
    }

    // A new feature added to p161
    // Return the maximal number of threads to be spawned in the system
    public int getMaxThreads() {
        return tids.length;
    }
    // constructor initializes the scheduler with the default time slice and queues.
    public Scheduler() {
        timeSlice = DEFAULT_TIME_SLICE;
        Queue0 = new Vector();
        Queue1 = new Vector();
        Queue2 = new Vector();
        initTid(DEFAULT_MAX_THREADS);
    }
    public Scheduler(int quantum) {
        timeSlice = quantum;
        Queue0 = new Vector();
        Queue1 = new Vector();
        Queue2 = new Vector();
        initTid(DEFAULT_MAX_THREADS);
    }

    // A new feature added to p161
    // A constructor to receive the max number of threads to be spawned
    public Scheduler(int quantum, int maxThreads) {
        timeSlice = quantum;
        Queue0 = new Vector();
        Queue1 = new Vector();
        Queue2 = new Vector();

        initTid(maxThreads);
    }
    // Simulate sleep based on the queue's priority adjusting the time slice accordingly.
    private void schedulerSleep() {
        try {
            if (Queue0.size() != 0) {
                Thread.sleep(timeSlice / 2); //For half time tasks

            } else if (Queue1.size() != 0) { 
                Thread.sleep(timeSlice); //For full time tasks

            } else {
                Thread.sleep(timeSlice * 2); // For double time tasks
            }
        } catch (InterruptedException e) {
        }
    }

    // A modified addThread of p161 example
    public TCB addThread(Thread t) {
        TCB parentTcb = getMyTcb(); // get my TCB of the current thread
        int pid = (parentTcb != null) ? parentTcb.getTid() : -1; // Get the parent thread's ID or -1 if not found
        int tid = getNewTid(); // get a new TID
        if (tid == -1)
            return null;
        TCB tcb = new TCB(t, tid, pid); // create a new TCB
        Queue0.add(tcb); // Add the new TCB to the high priority queue
        return tcb; // Return the newly created TCB
    }

    // A new feature added to p161
    // Removing the TCB of a terminating thread
    public boolean deleteThread() {
        TCB tcb = getMyTcb(); // Retrieve the TCB of the current thread
        if (tcb != null)
            return tcb.setTerminated();// mark the thread as terminated
        else
            return false; // Return false if the TCB wasn't found
    }
    // Allows a thread to sleep for a specified number of milliseconds
    public void sleepThread(int milliseconds) {
        try {
            sleep(milliseconds);
        } catch (InterruptedException e) {
        }
    }
    // The main loop of the scheduler which manages thread execution on priority
    public void run() {
        Thread current = null;
        while (true) {
            try {

                if (Queue0.size() == 0 && Queue1.size() == 0 && Queue2.size() == 0)
                    continue;

                //For priority Queue0 
                if (Queue0.size() != 0) {
                    TCB currentTCB = (TCB) Queue0.firstElement();
                    if (currentTCB.getTerminated() == true) {
                        Queue0.remove(currentTCB);
                        returnTid(currentTCB.getTid());
                        continue;
                    }

                    current = currentTCB.getThread();
                    if (current != null) {
                        if (current.isAlive())
                            current.resume();
                        else {
                            // Spawn must be controlled by Scheduler
			                // Scheduler must start a new thread
                            current.start();
                            run();
                        }
                    }

                    schedulerSleep();
                    // System.out.println("* * * Context Switch * * * ");

                    synchronized (Queue0) {
                        if (current != null && current.isAlive())
                            current.suspend();
                    }
                    Queue0.remove(currentTCB);
                    Queue1.add(currentTCB);
                }
                
                else if (Queue1.size() != 0) {
                    TCB currentTCB = (TCB) Queue1.firstElement();

                    if (currentTCB.getTerminated() == true) {
                        Queue1.remove(currentTCB);
                        returnTid(currentTCB.getTid());
                        continue;
                    }

                    current = currentTCB.getThread();
                    if (current != null) {
                        if (current.isAlive()) {
                            
                            current.resume();
                        } else {
                            
                            current.start();

                            
                        }
                    }

                    schedulerSleep();
                    // System.out.println("* * * Context Switch * * * ");

                    synchronized (Queue1) {
                        if (current != null && current.isAlive()) {
                            
                            current.suspend();
                        }
                        Queue1.remove(currentTCB);
                        Queue2.add(currentTCB);
                    }
                }

                else {
                    TCB currentTCB = (TCB) Queue2.firstElement();

                    if (currentTCB.getTerminated() == true) {
                        Queue2.remove(currentTCB);
                        returnTid(currentTCB.getTid());
                        continue;
                    }

                    current = currentTCB.getThread();
                    if (current != null) {
                        if (current.isAlive()) { 
                            current.resume();
                        } else {
                            // Spawn must be controlled by Scheduler
                            // Scheduler must start a new thread
                            current.start();
                            
                        }
                    }

                    schedulerSleep();
                    // System.out.println("* * * Context Switch * * * ");

                    synchronized (Queue2) {
                        if (current != null && current.isAlive()) {
                            current.suspend();
                        }
                        Queue2.remove(currentTCB);
                        Queue2.add(currentTCB);
                    }
                }
            } catch (NullPointerException e3) {};
        }

    }
}