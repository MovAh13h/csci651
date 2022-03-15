import rover.Rover;

import java.net.InetAddress; 

// Main.java
// (C) 2019 Sam Fryer
//
// Starts the UdpMulticastClient and the UdpMulticastSender threads
// and then just sits and waits forever.


class Main {
	public static void main(String args[]) {
		if (args.length < 3) {
			System.err.println("USAGE: javac Main [ID:int] [MULTICAST_IP:String] [PORT:int]");
			System.exit(1);
		}

		byte id = Byte.parseByte(args[0]);
		String multicastIP = args[1];
		int portNum = Integer.parseInt(args[2]);
		try {
			Rover r = new Rover(id, multicastIP, portNum);
			r.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// 	if (args.length>0) {

		// 	// Get the node number (first arg)
		// 	int nodeNum = Integer.parseInt(args[0]);
		// 	System.out.println("I'm node " + nodeNum);

		// 		try
		// 		{
		// 		// Get our sending address (perhaps for filtering later...)
		// 		InetAddress localhost = InetAddress.getLocalHost(); 
		// 		String address = (localhost.getHostAddress()).trim(); 
		// 		System.out.println("Broadcasting from: "+address);

		// 		// Starting Multicast Receiver
		// 		System.out.println("Starting Multicast Receiver...");
		// 		Thread client=new Thread(new UdpMulticastClient(63001,"230.230.230.230"));
		// 		client.start();

		// 		// Starting Multicast Sender
		// 		System.out.println("Starting Multicast Sender...");
		// 		Thread sender=new Thread(new UdpMulticastSender(63001,"230.230.230.230",nodeNum));
		// 		sender.start();

		// 		while(true){Thread.sleep(1000);}
		// 	}
		// 	catch(Exception ex)
		// 	{
		// 		  ex.printStackTrace();
		// 	}
		// }
		// else
		// 	System.out.println("No input args! Must specify Node Number!");
	}

	public static void main1(String[] args) {
		if (args.length>0) {

			// Get the node number (first arg)
			int nodeNum = Integer.parseInt(args[0]);
			System.out.println("I'm node " + nodeNum);

				try
				{
				// Get our sending address (perhaps for filtering later...)
				InetAddress localhost = InetAddress.getLocalHost(); 
				String address = (localhost.getHostAddress()).trim(); 
				System.out.println("Broadcasting from: "+address);

				// Starting Multicast Receiver
				System.out.println("Starting Multicast Receiver...");
				Thread client=new Thread(new UdpMulticastClient(63001,"230.230.230.230"));
				client.start();

				// Starting Multicast Sender
				System.out.println("Starting Multicast Sender...");
				Thread sender=new Thread(new UdpMulticastSender(63001,"230.230.230.230",nodeNum));
				sender.start();

				while(true){Thread.sleep(1000);}
			}
			catch(Exception ex)
			{
				  ex.printStackTrace();
			}
		}
		else
			System.out.println("No input args! Must specify Node Number!");	
	}
}
