package app;

import models.concurrency.BatteryConveyorBelt;
import models.concurrency.CentralRegistryBoard;
import models.concurrency.CentralSupervisor;
import models.concurrency.SolderingControlCentre;
import tasks.AGVTask;
import tasks.BreakdownSensorTask;
import tasks.MaintenanceTechnicianTask;
import tasks.RobotTask;
import views.ui.DashboardTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp {
    static Scanner scanner = new Scanner(System.in);
    public static void main(String[] args){


        // Configuration
        int[] parameters = configParameters();
        if(parameters == null){
            scanner.close();
            return;
        }
        int numStations = parameters[0];
        int beltMaxCapacity = parameters[1];
        int numRobots = parameters[2];
        int numAGVs = parameters[3];
        int carsPerRobot = 3;
        int totalCarsExpected = numRobots * carsPerRobot;

        SolderingControlCentre solderingCentre = new SolderingControlCentre(numStations);
        BatteryConveyorBelt conveyorBelt = new BatteryConveyorBelt(beltMaxCapacity);
        CentralRegistryBoard registryBoard = new CentralRegistryBoard();
        CentralSupervisor supervisor = new CentralSupervisor();
        BreakdownSensorTask sensorTask = new BreakdownSensorTask(solderingCentre);
        Thread sensorThread = new Thread(sensorTask);
        sensorThread.setDaemon(true);

        MaintenanceTechnicianTask technicianTask = new MaintenanceTechnicianTask(solderingCentre);
        Thread technicianThread = new Thread(technicianTask);

        List<Thread> workerThreads = new ArrayList<>();
        List<RobotTask> robotTasks = new ArrayList<>();

        for(int i = 0; i < numRobots; i++){
            RobotTask robotTask = new RobotTask(
                    "Robot-" + (i+1),
                    conveyorBelt,
                    solderingCentre,
                    registryBoard,
                    supervisor,
                    carsPerRobot
            );
            robotTasks.add(robotTask);
            workerThreads.add(new Thread(robotTask));
        }

        // Distribute total batteries evenly across AGVs so supply matches demand
        int baseBatteries = totalCarsExpected / numAGVs;
        int remainder = totalCarsExpected % numAGVs;

        for(int i = 0; i < numAGVs; i++){
            int batteriesForThisAGV = baseBatteries + (i < remainder ? 1 : 0);
            AGVTask agvTask = new AGVTask(
                    "AGV-" + (i + 1),
                    conveyorBelt,
                    supervisor,
                    batteriesForThisAGV
            );
            workerThreads.add(new Thread(agvTask));
        }

        DashboardTask dashboardTask = new DashboardTask(
                solderingCentre,
                conveyorBelt,
                registryBoard,
                supervisor,
                technicianTask,
                robotTasks,
                numStations
        );

        Thread dashboardThread = new Thread(dashboardTask);
        dashboardThread.start();
        sensorThread.start();
        technicianThread.start();
        for(Thread thread : workerThreads){
            thread.start();
        }

        // Simulate an Overheating Emergency mid-run
        try{
            Thread.sleep(1500);
            supervisor.activateOverheatingAlert();
            // Robots stay paused during 10 secs
            Thread.sleep(10000);
            supervisor.resolveOverheatingAlert();

            for(Thread thread : workerThreads){
                thread.join();
            }

            technicianThread.interrupt();
            technicianThread.join();

            dashboardThread.interrupt();
            dashboardThread.join();
        } catch (InterruptedException e) {
            System.err.println("Main simulation interrupted.");
            Thread.currentThread().interrupt();
        }

        // Final Summary
        System.out.println("\n=== SIMULATION COMPLETE ===");
        System.out.println("Total cars assembled & certified: " + registryBoard.getSignedCertificates() + "/" + totalCarsExpected);
        scanner.close();

    }

    private static int[] configParameters(){
        int[] parameters = new int[4];

        int numStations = 0;
        int beltMaxCapacity = 0;
        int numRobots = 0;
        int numAGVs = 0;
        try{
            System.out.print("Enter number of welding stations (e.g., 3): ");
            numStations = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter conveyor belt max capacity (e.g., 5): ");
            beltMaxCapacity = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter number of assembly robots (e.g., 4): ");
            numRobots = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter number of AGVs (e.g., 2): ");
            numAGVs = Integer.parseInt(scanner.nextLine());

            if (numStations <= 0 || beltMaxCapacity <= 0 || numRobots <= 0 || numAGVs <= 0) {
                System.out.println("All values must be greater than 0.");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("A number format exception occurred: " + e.getMessage());
            return null;
        }

        parameters[0] = numStations;
        parameters[1] = beltMaxCapacity;
        parameters[2] = numRobots;
        parameters[3] = numAGVs;
        return parameters;
    }
}
