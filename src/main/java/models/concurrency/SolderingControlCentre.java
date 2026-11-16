package models.concurrency;

import java.util.concurrent.Semaphore;

public class SolderingControlCentre {
    // The soldering control centre has many soldering stations under it.
    private final Semaphore centreSemaphores;
    private final boolean[] occupiedStations;
    private final boolean[] brokenStations;

    public SolderingControlCentre(int numStations){
        this.centreSemaphores = new Semaphore(numStations);
        this.occupiedStations = new boolean[numStations];
        this.brokenStations = new boolean[numStations];
    }

    public int occupyStation() throws InterruptedException{
        centreSemaphores.acquire();
        return assignFreeIndex();
    }

    private synchronized int assignFreeIndex(){
        for(int i = 0; i < occupiedStations.length; i++){
            if(!occupiedStations[i] && !brokenStations[i]){
                occupiedStations[i] = true;
                return i;
            }
        }
        return -1;
    }

    public void releaseStation(int stationIndex){
        releaseIndex(stationIndex);
        centreSemaphores.release();
    }

    private synchronized void releaseIndex(int stationIndex){
        occupiedStations[stationIndex] = false;
    }

    public synchronized int getNumOccupiedStations(){
        int numStationsOccupied = 0;
        for(boolean occupied : occupiedStations){
            if(occupied)
                numStationsOccupied++;
        }
        return numStationsOccupied;
    }

    // Maintenance
    public void triggerRandomBreakdown() throws InterruptedException{
        centreSemaphores.acquire();
        markStationBroken();
    }

    private synchronized void markStationBroken(){
        for(int i = 0; i < brokenStations.length; i++){
            if(!occupiedStations[i] && !brokenStations[i]){
                brokenStations[i] = true;
                notifyAll();
                return;
            }
        }
    }

    public synchronized int waitForBrokenStation() throws InterruptedException{
        while(findBrokenStationIndex() == -1)
            wait();
        return findBrokenStationIndex();
    }

    private synchronized int findBrokenStationIndex(){
        for(int i = 0; i < brokenStations.length; i++){
            if(brokenStations[i])
                return i;
        }
        return -1;
    }

    public void completeRepair(int stationIndex){
        repairStation(stationIndex);
        centreSemaphores.release();
    }

    private synchronized void repairStation(int stationIndex){
        brokenStations[stationIndex] = false;
    }

    public synchronized int getNumBrokenStations(){
        int numBrokenStations = 0;
        for(boolean station : brokenStations){
            if(station)
                numBrokenStations++;
        }
        return numBrokenStations;
    }
}
