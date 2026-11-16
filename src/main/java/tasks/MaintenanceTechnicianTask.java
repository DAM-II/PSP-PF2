package tasks;

import models.concurrency.SolderingControlCentre;

public class MaintenanceTechnicianTask implements Runnable{
    private final SolderingControlCentre solderingCentre;
    private String status;

    public MaintenanceTechnicianTask(SolderingControlCentre solderingCentre){
        this.solderingCentre = solderingCentre;
        this.status = "ON_STANDBY";
    }

    @Override
    public void run(){
        try{
            while(!Thread.currentThread().isInterrupted()){
                setStatus("ON_STANDBY");
                int stationToBreak = solderingCentre.waitForBrokenStation();
                setStatus("REPAIRING (Station " + stationToBreak + ")");
                Thread.sleep(5000);
                solderingCentre.completeRepair(stationToBreak);
            }
        } catch (InterruptedException e) {
            setStatus("SHIFT_ENDED");
            Thread.currentThread().interrupt();
        }
    }

    public synchronized String getStatus(){
        return status;
    }

    private synchronized void setStatus(String status){
        this.status = status;
    }
}
