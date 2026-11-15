package models.concurrency;

import java.util.concurrent.Semaphore;

public class SolderingControlCentre {
    // The soldering control centre has many soldering stations under it.
    private final Semaphore centreSemaphores;
    private final boolean[] occupiedStations;

    public SolderingControlCentre(int numStations){
        this.centreSemaphores = new Semaphore(numStations);
        this.occupiedStations = new boolean[numStations];
    }

    public int occupyStation() throws InterruptedException{
        centreSemaphores.acquire();
        return assignFreeIndex();
    }

    private synchronized int assignFreeIndex(){
        for(int i = 0; i < occupiedStations.length; i++){
            if(!occupiedStations[i]){
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
}
