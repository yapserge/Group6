package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
    	this.conditionLock = new Lock();
    	this.speakQueue = new Condition2(conditionLock);
    	this.listenQueue = new Condition2(conditionLock);
    	inTrans = false;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
    	this.conditionLock.acquire();
    		while(inTrans) {
    			this.speakQueue.sleep();
    		}
    		this.message = word;
    		this.inTrans = true;
    		this.listenQueue.wake();
    		this.conditionLock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
    	int listened = message;
    	this.conditionLock.acquire();
    		while(!inTrans) {
    				this.listenQueue.sleep();
    		}
    		inTrans = false;
    		this.speakQueue.wakeAll();
    		
    		this.conditionLock.release();
    		return listened;
    }
    
    public static void selfTest()
	{
		KThread test1 = new KThread(new Test(1));
		KThread test2 = new KThread(new Test(2));
		KThread test3 = new KThread(new Test(3));
		KThread test4 = new KThread(new Test(4));
		KThread test5 = new KThread(new Test(5));
		

		test1.fork();
		test2.fork();
		test3.fork();
		test4.fork();
		test5.fork();
		
		System.out.println("\nCommunicator Test:");
		new Test(0).run();
	}
	
public static class Test implements Runnable
{

 private int ID;
 private static Communicator com = new Communicator();
 
	Test(int ID) 
	{
	    this.ID = ID;
	}
	

	public void run() {
	    if (ID == 0) 
	    {
	        for (int i = 0; i < 5; i++) 
	        {
	            System.out.println("Test " + ID + " Speak(" + i + ")");
	            com.speak(i);
	        }
	    }
	    else 
	    {
	        for (int i = 0; i < 5; i++) 
	        {
	            System.out.println("Test " +ID + " listening to... " + i);
	            int transfered = com.listen();
	            System.out.println("Test " + ID + " heard word " + transfered);
	        }
	    }
	    	ThreadedKernel.alarm.waitUntil(2000);
	    	System.out.println("PASS: Communicator Success!");
	
		}
    }
    
    private Lock conditionLock = new Lock();
    private Condition2 speakQueue = new Condition2(conditionLock);
    private Condition2 listenQueue = new Condition2(conditionLock);
    private int message;
    private boolean inTrans;
}

