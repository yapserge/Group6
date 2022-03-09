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
    
    public static void selfTest(){
        	
    		System.out.println("\nTesting Communicator:");
        	final Communicator communicator = new Communicator();	
    		
        	Runnable A = new Runnable(){
        		
        		public void run(){
        			System.out.println("Speaking... Awaiting Verification!");
        			communicator.speak(5);
        			System.out.println("No longer speaking...");
        		}
        	};
        	
        	Runnable B = new Runnable(){
        		public void run(){
        			System.out.println("Listening... Awaiting verification.");
        			int x = communicator.listen();
        			System.out.println("No longer listening... Is this your word?: " + x);
        		}
        	};
        	
        	KThread Test1 = new KThread(A);
        	KThread Test2 = new KThread(B);
    		Test1.fork();
    		Test2.fork();
    		Test1.join();
    		Test2.join();
        	
 
    }
    
    private Lock conditionLock = new Lock();
    private Condition2 speakQueue = new Condition2(conditionLock);
    private Condition2 listenQueue = new Condition2(conditionLock);
    private int message;
    private boolean inTrans;
}

