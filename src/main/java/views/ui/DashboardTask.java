package views.ui;

import models.concurrency.BatteryConveyorBelt;
import models.concurrency.CentralRegistryBoard;
import models.concurrency.CentralSupervisor;
import models.concurrency.SolderingControlCentre;
import tasks.MaintenanceTechnicianTask;
import tasks.RobotTask;

import java.util.List;

public class DashboardTask implements Runnable {
    private final SolderingControlCentre solderingCentre;
    private final BatteryConveyorBelt conveyorBelt;
    private final CentralRegistryBoard registryBoard;
    private final CentralSupervisor supervisor;
    private final MaintenanceTechnicianTask technician;
    private final List<RobotTask> robots;
    private final int totalStations;

    public DashboardTask(SolderingControlCentre solderingCentre,
                         BatteryConveyorBelt conveyorBelt,
                         CentralRegistryBoard registryBoard,
                         CentralSupervisor supervisor,
                         MaintenanceTechnicianTask technician,
                         List<RobotTask> robots,
                         int totalStations) {
        this.solderingCentre = solderingCentre;
        this.conveyorBelt = conveyorBelt;
        this.registryBoard = registryBoard;
        this.supervisor = supervisor;
        this.technician = technician;
        this.robots = robots;
        this.totalStations = totalStations;
    }

    @Override
    public void run(){
        try{
            while(!Thread.currentThread().isInterrupted()){
                printStatus();
                supervisor.checkEmergencyStop();
                // Refresh dashboard every 0.5 seconds
                Thread.sleep(500);
            }
        } catch (InterruptedException e){
            printStatus();
            Thread.currentThread().interrupt();
        }
    }

    private void printStatus(){
        System.out.println("__________________________________________________________________");
        System.out.println("Assembly Dashboard");
        System.out.println("Overheating Alert   : " + (supervisor.isOverheatingAlertActive() ? "ACTIVE" : "NORMAL"));
        System.out.println("Welding Stations    : " + solderingCentre.getNumOccupiedStations() + "/" + totalStations + " occupied( " + solderingCentre.getNumBrokenStations() + " broken)");
        System.out.println("Conveyor Belt       : " + conveyorBelt.getCurrentCapacity() + "/" + conveyorBelt.getMaxCapacity() + " batteries");
        System.out.println("Num. of Signed Cars : " + registryBoard.getSignedCertificates());
        System.out.println("Robots:-");
        for (RobotTask robot : robots){
            System.out.println("\t" + robot.getRobotName() + ": " + robot.getStatus());
        }
        System.out.println("Maintenance Tech    : " + technician.getStatus());
        System.out.println("__________________________________________________________________");
    }
}
