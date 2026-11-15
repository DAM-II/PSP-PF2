package tasks;

import models.concurrency.BatteryConveyorBelt;
import models.concurrency.CentralSupervisor;

public class AGVTask implements Runnable{
    private final String agvName;
    private final BatteryConveyorBelt conveyorBelt;
    private final CentralSupervisor supervisor;
    private final int batteriesToDeliver;

    public AGVTask(String agvName, BatteryConveyorBelt conveyorBelt, CentralSupervisor supervisor, int batteriesToDeliver) {
        this.agvName = agvName;
        this.conveyorBelt = conveyorBelt;
        this.supervisor = supervisor;
        this.batteriesToDeliver = batteriesToDeliver;
    }

    @Override
    public void run(){
        try{
            for(int i = 0; i < batteriesToDeliver; i++){
                supervisor.checkEmergencyStop();
                // Simulate fetching battery from storage
                Thread.sleep(300);
                conveyorBelt.putBattery();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
