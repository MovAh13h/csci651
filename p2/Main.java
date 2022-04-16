class Main {
    public static void main(String args[]) {
        if (args.length < 3) {
            System.exit(1);
        }

        int id = Integer.parseInt(args[0]);
        String multicastIP = args[1];
        int port = Integer.parseInt(args[2]);

        try {
            Rover r = new Rover(id, multicastIP, port);
            r.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}