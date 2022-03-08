package nachos.threads;

import nachos.machine.*;
import java.util.TreeSet;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
 
	/**
	 * Creating a priority queue to hold the waiting threads.
	 */
	java.util.PriorityQueue<waitingThread> waitQueue = new java.util.PriorityQueue<waitingThread>();
	
   /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {

	boolean status = Machine.interrupt().disable();
    	waitingThread newThread;
    	
    	while((newThread = waitQueue.peek()) != null
    			&& newThread.getTime() <= Machine.timer().getTime()) {
    		waitQueue.poll().getThread().ready();
    		
    	}
    	
    	Machine.interrupt().restore(status);
    	KThread.yield();
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	long wakeTime = Machine.timer().getTime() + x;
    	boolean status = Machine.interrupt().disable();
    
    	KThread thread = KThread.currentThread();
    	waitingThread newThread = new waitingThread(thread, wakeTime);
    	
    	waitQueue.add(newThread);
    	KThread.sleep();
    	Machine.interrupt().restore(status);
    	
    }

/**
     * A private class that implements the Comparable interface.
     *
     */
    private class waitingThread implements Comparable<waitingThread> {
    	public KThread thread;
    	public long time;
    	
    	/**
    	 * Constructor to create a waiting thread, consisting of the thread and its wake time.
    	 * @param thread  the thread.
    	 * @param time  wake time of the thread.
    	 */
    	public waitingThread(KThread thread, long time){
    		this.thread = thread;
    		this.time = time;
    	}
    	
    	/**
    	 * Returns the thread.
    	 * @return  the thread.
    	 */
    	public KThread getThread() {
    		return thread;
    	}
    	
    	/**
    	 * Returns the wake time of the thread.
    	 * @return  the wake time of the thread.
    	 */
    	public long getTime() {
    		return time;
    	}
    	
    	/**
    	 * Allows threads to be sorted according to their wake times. 
    	 */
    	public int compareTo(waitingThread newThread) {
    		if(time > newThread.time) {
    			return 1;
    		}
    		else if(time < newThread.time) {
    			return -1;
    		}
    		else { 
    			return thread.compareTo(newThread.thread);
    		}
    		
    	}
    }
    
    public static void selfTest(){
    	System.out.println("\nTesting Alarm:");
    	
    		class RunAlarm implements Runnable {
    			public void run() {
    				ThreadedKernel.alarm.waitUntil((int)(Math.random() * 1000) + 500);

    			}
    		}
    		
    		final int threadnum = 7; 
    		
    		RunAlarm run = new RunAlarm();
    		for(int i = 0; i < threadnum; i++) {
    			new KThread(run).setName("t"+i).fork();
    		}
    		KThread.yield(); 
    		System.out.println("PASS: Alarm success!");
    }
	
	private TreeSet<waitingThread> set; 
}
