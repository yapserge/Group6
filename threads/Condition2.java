package nachos.threads;

import nachos.machine.*;
/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
	/**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
	public Condition2(Lock conditionLock) {
        this.conditionLock = conditionLock;

    }
	/**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
	public void sleep() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        boolean status = Machine.interrupt().disable();
        count++;
        waitQueue.waitForAccess(KThread.currentThread());
        conditionLock.release();
        KThread.sleep();
        conditionLock.acquire();
        Machine.interrupt().restore(status);
    }
	/**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
	public void wake() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        boolean status = Machine.interrupt().disable();
        KThread thread = waitQueue.nextThread();
        if(thread != null) {
            count--;
            thread.ready();
        }
        Machine.interrupt().restore(status);
    }
	/**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
	public void wakeAll() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());
        boolean status = Machine.interrupt().disable();
        KThread thread;
        while((thread = waitQueue.nextThread()) != null)
            thread.ready();
        count = 0;
        Machine.interrupt().restore(status);
    }
	/**
	* Gets the thread count and returns it
	* Used in Communicator
	*/
	
	public static void selfTest(){
		
		System.out.println("\nTesting Condition 2:");
		
		final Lock lock = new Lock();
		final Condition2 con2 = new Condition2(lock);
		
		KThread sleep = new KThread(new Runnable(){
			public void run(){
				
				lock.acquire();
				
				System.out.println("Sleep:");
				System.out.println("Going to sleep...");
				con2.sleep();
				System.out.println("PASS: Thread awake!");
				lock.release();
			}
			
			
		});
		sleep.fork();
		
		KThread wake =	new KThread(new Runnable()
		{
		//Test 2: Wake
           public void run()
           {
        	   lock.acquire();
        	   System.out.println("Wake:"); 
               System.out.println("...Waking a thread...");
               con2.wake();      
				System.out.println("PASS: Wake up succesful!");
				lock.release();
       } } );
		wake.fork();
		sleep.join();
		
	}

    public int getThreadCount() {
        return count;
    }

    private ThreadQueue waitQueue = ThreadedKernel.scheduler.newThreadQueue(true);
    private int count;
    private Lock conditionLock;
}
