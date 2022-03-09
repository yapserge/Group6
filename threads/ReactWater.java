package nachos.threads;

import nachos.machine.*;

public class ReactWater{

	private int hydrogenCount;
	private int oxygenCount;
	private Lock conditionLock;
	private Condition hydrogenCondition;
	private Condition oxygenCondition;
   
	/** 
     *   Constructor of ReactWater
     **/
    public ReactWater() {
    	this.hydrogenCount = 0;
    	this.oxygenCount = 0;
    	this.conditionLock = new Lock();
    	this.hydrogenCondition = new Condition(conditionLock);
    	this.oxygenCondition = new Condition(conditionLock);
    	
    } 

    /** 
     *   When H element comes, if there already exist another H element 
     *   and an O element, then call the method of Makewater(). Or let 
     *   H element wait in line. 
     **/ 
    public void hReady() {
    	conditionLock.acquire();
    	hydrogenCount++;
    	if(hydrogenCount > 1 && oxygenCount > 0) {
    		Makewater();
    		hydrogenCondition.wake();
    		oxygenCondition.wake();
    		hydrogenCount = hydrogenCount - 2;
    		oxygenCount = oxygenCount - 1;
    		
    	}
    	else {
    		hydrogenCondition.sleep();
    	}
    	
    	conditionLock.release();
    	
    } 
 
    /** 
     *   When O element comes, if there already exist another two H
     *   elements, then call the method of Makewater(). Or let O element
     *   wait in line. 
     **/ 
    public void oReady() {
    	conditionLock.acquire();
    	oxygenCount++;
    	if(hydrogenCount > 1 && oxygenCount > 0) {
    		Makewater();
    		oxygenCondition.wake();
    		hydrogenCondition.wake();
    		hydrogenCount = hydrogenCount - 2;
    		oxygenCount = oxygenCount - 1;
    		
    	}
    	else {
    		oxygenCondition.sleep();
    	}
    	conditionLock.release();
    } 
    
    /** 
     *   Print out the message of "water was made!".
     **/
    public void Makewater() {
    	System.out.println("Water was made!");
    } 
    
    public static void selfTest(){
    	
    System.out.println("\nTesting ReactWater:");
    final ReactWater water = new ReactWater();
    
    Runnable hydro = new Runnable(){
    	public void run(){
    		System.out.println("Hydrogen...");
    		water.hReady();
    	}
    };
    
    Runnable oxy = new Runnable(){
    	public void run(){
    		System.out.println("Oxygen...");
    		water.oReady();
    	}
    };
    	
    KThread test1 = new KThread(hydro);
    KThread test2 = new KThread(oxy);
    KThread test3 = new KThread(hydro);
    test1.fork();
    test2.fork();
    test3.fork();
    test1.join();
    test2.join();
    test3.join();
    System.out.println("ReactWater Success!");
    }
}