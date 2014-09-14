import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//Client class sends messages to servers of other nodes.
	public class client{
    	
		Socket clientSocket;
		
		public client(String host,String port,String msg) throws java.net.ConnectException,IOException
		{
			try {
				clientSocket = new Socket(host,Integer.parseInt(port));
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				out.write(msg);
				out.close();
			}
			/*catch (Exception e) {
				e.printStackTrace();
			}*/
			/*catch(IOException io)
			{
				//System.out.println("Could not connect in order to download");
				//System.out.println("Target machine may have gone offline !");
			}*/
			finally
			{	
			}
		}
	}