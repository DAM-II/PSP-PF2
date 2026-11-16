package tasks;

import models.concurrency.SolderingControlCentre;

import java.util.Random;

public class BreakdownSensorTask implements Runnable {
    private final SolderingControlCentre solderingCentre;
    private final Random random;

    public BreakdownSensorTask(SolderingControlCentre solderingCentre){
        this.solderingCentre = solderingCentre;
        this.random = new Random();
    }

    @Override
    public void run(){
        try{
            while(!Thread.currentThread().isInterrupted()){
                // Wait a random time between 2 and 4 seconds before causing a breakdown
                int delay = 2000 + random.nextInt(2000);
                Thread.sleep(delay);
                solderingCentre.triggerRandomBreakdown();
            }
        }catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
