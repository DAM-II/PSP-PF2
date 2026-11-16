package tasks;

import models.concurrency.BatteryConveyorBelt;
import models.concurrency.CentralRegistryBoard;
import models.concurrency.CentralSupervisor;
import models.concurrency.SolderingControlCentre;

public class RobotTask implements Runnable{
    private final String robotName;
    private final BatteryConveyorBelt conveyorBelt;
    private final SolderingControlCentre solderingCentre;
    private final CentralRegistryBoard registryBoard;
    private final CentralSupervisor supervisor;
    private final int carsToAssemble;
    private String status;

    public RobotTask(String robotName,
                     BatteryConveyorBelt conveyorBelt,
                     SolderingControlCentre solderingCentre,
                     CentralRegistryBoard registryBoard,
                     CentralSupervisor supervisor,
                     int carsToAssemble) {
        this.robotName = robotName;
        this.conveyorBelt = conveyorBelt;
        this.solderingCentre = solderingCentre;
        this.registryBoard = registryBoard;
        this.supervisor = supervisor;
        this.carsToAssemble = carsToAssemble;
        this.status = "IDLE";
    }

    @Override
    public void run(){
        try{
            for(int i = 0; i < carsToAssemble; i++){
                setStatus("CHECKING_SAFETY");
                supervisor.checkEmergencyStop();

                setStatus("WAITING_BATTERY");
                conveyorBelt.takeBattery();

                setStatus("WAITING_STATION");
                int stationId = solderingCentre.occupyStation();
                try{
                    setStatus("WELDING (at station " + stationId + ")");
                    // Simulate welding time
                    Thread.sleep(600);
                } finally {
                    solderingCentre.releaseStation(stationId);
                }

                setStatus("SIGNING_BOARD");
                registryBoard.signCertificate();
            }
            setStatus("FINISHED");
        } catch (InterruptedException e) {
            setStatus("INTERRUPTED");
            Thread.currentThread().interrupt();
        }
    }

    public String getRobotName() {
        return robotName;
    }

    public synchronized String getStatus() {
        return status;
    }

    private synchronized void setStatus(String status) {
        this.status = status;
    }
}
