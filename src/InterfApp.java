import java.util.Scanner;

class InterfApp {
    String command;
    int number;
    public void StartApp() {
        ElevatorEmulator ee = new ElevatorEmulator();

        ElevatorEmulator transformation = new ElevatorEmulator();

        Scanner in = new Scanner(System.in);

        System.out.print("Hi, this is an application simulating the operation of 2 elevators in a multi-storey building\n" +
                "List of commands to control:\n" +
                "run - for start\n" +
                "get info - get info about emulation settings\n" +
                "slt - set limit time of work\n" +
                "sf - set floors (limit (15, 20))\n" +
                "si - set interval in sec between requests generating (limit (1, 10))\n" +
                "scr - set number of requests generating (limit (1, 10))\n" +
                "exit - command for exit\n" +
                "--help - info about command\n\n");

        System.out.println("Enter command");
        System.out.print(">> ");
        command = in.nextLine();

        while (!command.equalsIgnoreCase("exit")){
            if (command.equalsIgnoreCase("--help")){
                System.out.print("List of commands to control:\n" +
                        "run - for start\n" +
                        "get info - get info about emulation settings\n" +
                        "slt - set limit time of work\n" +
                        "sf - set floors (limit (15, 20))\n" +
                        "si - set interval in sec between requests generating (limit (1, 10))\n" +
                        "scr - set number of requests generating (limit (1, 10))\n" +
                        "exit - command for exit\n" +
                        "--help - info about command\n\n");
            }
            else if(command.equalsIgnoreCase("run")){
                ee.run();
            }
            else if(command.equalsIgnoreCase("get info")){
                System.out.println("Number of floors: " + ee.floors + "\nTime of work(sec): " + ee.limit_time_work +
                        "\nInterval(sec): " + ee.interval + "\nNumber of requests per interval: " + ee.count_req);
            }
            else if (command.equalsIgnoreCase("sf")){
                System.out.print("Input number of floors: ");
                number = in.nextInt();
                if (number >= 15 && number <=20){
                    ee.floors = number;
                }
                else{
                    System.out.println("Incorrect number");
                }
            }
            else if (command.equalsIgnoreCase("scr")){
                System.out.print("Input number of floors: ");
                number = in.nextInt();
                if (number > 0 && number <=10){
                    ee.count_req = number;
                }
                else{
                    System.out.println("Incorrect number");
                }
            }
            else if (command.equalsIgnoreCase("si")){
                System.out.print("Input intercal in seconds: ");
                number = in.nextInt();
                if (number > 0 && number <= 10 && number < ee.limit_time_work)
                    ee.interval = number;
                else
                    System.out.println("Incorrect number");
            }
            else if (command.equalsIgnoreCase("slt")){
                System.out.print("Input limit_time in seconds: ");
                number = in.nextInt();
                if (number > 0 && number > ee.interval)
                    ee.limit_time_work = number;
                else
                    System.out.println("Incorrect number");
            }
            else if (command.equalsIgnoreCase("")){
                command = in.nextLine();
                continue;
            }
            else{
                System.out.println("Command not found");
            }
            System.out.println("Enter command");
            System.out.print(">> ");
            command = in.nextLine();
        }
    }
}