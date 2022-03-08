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
    	conditionLock.acquire();
    		while(inTrans || listenQueue.getThreadCount() == 0) {
    			speakQueue.sleep();
    		}
    		inTrans = true;
    		int message = word;
    		listenQueue.wake();
    		conditionLock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
    	int listened;
    	conditionLock.acquire();
    		while(!inTrans) {
    			if(speakQueue.getThreadCount()>0) {
    				speakQueue.wake();
    				listenQueue.sleep();
    			}
    		}
    		listened = message;
    		inTrans = false;
    		if(listenQueue.getThreadCount() > 0 && speakQueue.getThreadCount() > 0)
    			speakQueue.wake();
    		
    		conditionLock.release();
    		return listened;
    }
    
    private Lock conditionLock = new Lock();
    private Condition2 speakQueue = new Condition2(conditionLock);
    private Condition2 listenQueue = new Condition2(conditionLock);
    private int message;
    private boolean inTrans;
}

