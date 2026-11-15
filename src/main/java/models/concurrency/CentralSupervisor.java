package models.concurrency;

public class CentralSupervisor {
    private boolean overheatingAlert;

    public CentralSupervisor(){
        this.overheatingAlert = false;
    }

    public synchronized void checkEmergencyStop() throws InterruptedException{
        while(overheatingAlert)
            wait();
    }

    public synchronized void activateOverheatingAlert(){
        this.overheatingAlert = true;
    }

    public synchronized void resolveOverheatingAlert(){
        this.overheatingAlert = false;
        notifyAll();
    }

    public synchronized boolean isOverheatingAlertActive(){
        return overheatingAlert;
    }
}
