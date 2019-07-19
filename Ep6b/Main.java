public class Main {
    private static int numPrisoners = 20;

    public static void main(String[] args) throws Exception {
        if (args.length >= 1)
            numPrisoners = Integer.parseInt(args[0]);

        System.out.println(numPrisoners);

        Prisoner[] prisoners = new Prisoner[numPrisoners];
        Room room = new Room();
        DoorClosed doorClosed = new DoorClosed();
        Warden warden = new Warden(room, prisoners, doorClosed);
        prisoners[0] = new Leader(0, room, warden, prisoners.length, doorClosed);
        for (int it = 1; it < prisoners.length; it++)
            prisoners[it] = new Prisoner(it, room, warden, doorClosed);

        warden.start();
        for (int it = 0; it < prisoners.length; it++)
            prisoners[it].start();

        warden.join();
        for (int it = 0; it < prisoners.length; it++)
            prisoners[it].join();
    }
}
