import rover.Rover;

class Main {
	public static void main(String args[]) {
		if (args.length < 3) {
			System.err.println("USAGE: javac Main [ID:int] [MULTICAST_IP:String] [PORT:int]");
			System.exit(1);
		}

		try {
			byte id = Byte.parseByte(args[0]);
			String multicastIP = args[1];
			int portNum = Integer.parseInt(args[2]);
			Rover r = new Rover(id, multicastIP, portNum);

			// start the rover brrrr
			r.run();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
