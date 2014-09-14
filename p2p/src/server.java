import java.net.ServerSocket;

/**Server class listens for clients and accepts when various clients from other classes
 * wants to send message to the server.*/
public class server extends Thread{
	
	ServerSocket serverSocket;
	
	public void run()
	{
		try{ 
			serverSocket = new ServerSocket(computer.port);
			while(true){
				new serverThread(serverSocket.accept()).start();
				if(Thread.interrupted())
				{
					System.out.println("Socket "+computer.port+" stopped listening.");
					break;
				}
			}
	    } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
	