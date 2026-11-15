package models.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BatteryConveyorBelt {
    private int currentCapacity;
    private final int maxCapacity;
    private final ReentrantLock lock;
    private final Condition beltNotFull; // For AGVs to wait
    private final Condition beltNotEmpty; // For robots to wait

    public BatteryConveyorBelt(int maxCapacity){
        this.currentCapacity = 0;
        this.maxCapacity = maxCapacity;
        this.lock = new ReentrantLock();
        this.beltNotFull = lock.newCondition();
        this.beltNotEmpty = lock.newCondition();
    }

    public void putBattery() throws InterruptedException{
        lock.lock();
        try{
            while(currentCapacity == maxCapacity){
                beltNotFull.await();
            }

            currentCapacity++;
            beltNotEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public void takeBattery() throws InterruptedException{
        lock.lock();
        try{
            while (currentCapacity == 0){
                beltNotEmpty.await();
            }

            currentCapacity--;
            beltNotFull.signal();
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentCapacity(){
        lock.lock();
        try{
            return currentCapacity;
        } finally {
            lock.unlock();
        }
    }

    public int getMaxCapacity(){
        return maxCapacity;
    }
}
