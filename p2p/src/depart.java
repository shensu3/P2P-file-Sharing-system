import java.util.*;

/**Initiates the departure protocol for this node. Send DEPART message to all neighbors. 
 * If all nodes send acknowledgment then send RECONNECT.*/
public class depart extends Thread{

	public depart()
	{
		//Send depart request to all neighbors to start the process of departing from the P2P network.
		String msg = "DEPART_REQ "+computer.id+" "+computer.myhost+" "+computer.port;
		computer.broadcast(msg);	//Send depart request message to all neighbors.
		while(computer.acks<computer.neighbor_list.size())	//Waits till it receives all depart acknowledgments.
		{
			try {
				sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(computer.acks==computer.neighbor_list.size())	//Initiates reconnect process when it receives all acknowledgments.
		{
			Random rand = new Random();
			int random = rand.nextInt(computer.neighbor_list.size());
			String new_neighbour = computer.neighbor_list.get(random);	//Selects a random new neighbor and sends all neighbors to connect to it.
			String mssg = "RECONNECT "+computer.id+" "+computer.myhost+" "+computer.port+" "+new_neighbour;
			computer.broadcast(mssg);	//Send reconnect message to all the neighbors.
		}
	}
	
}
