import java.io.*;
import java.net.*;


/**Server class starts this serverThread class when a new client joins the server.
 * All actions when a new message is received from a child node is done here.*/
public class serverThread extends Thread{
	
	Socket socket = null;
    
	public serverThread(Socket socket) {
        this.socket = socket;
    }
	
    public void run()
    {
    	//Listen for messages in the socket.
    	try(BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));){
	    		
    		String input;	
    		//Reads the message sent from a neighbor.
    		while((input=in.readLine())!=null)
	    	{	
    			String line[] = input.split(" ");
    			if(line[0].contentEquals("JOIN"))	//If Join message was received add the neighbor info into the list and send acknowledgment.
	    		{
    				String msg = "JOIN_ACK "+computer.id+" "+computer.myhost+" "+computer.port;
    				String id = line[1];
    				String host = line[2];
    				String port = line[3];
    				new client(host,port,msg);
   					if(!computer.neighbor_list.contains(id+" "+host+" "+port))
   						computer.neighbor_list.add(id+" "+host+" "+port);
	    		}
    			else if(line[0].contentEquals("JOIN_ACK")) //If Join Acknowledgment was received add the neighbor info into the list.
    			{
    				String id = line[1];
    				String host = line[2];
    				String port = line[3];
    				if(!computer.neighbor_list.contains(id+" "+host+" "+port))
   						computer.neighbor_list.add(id+" "+host+" "+port);
    			}
    			else if(line[0].contentEquals("SEARCH_REQ")) //If Search Request was received forward the request if file does not exist else reply.
    			{
       				if(!search.requestHandle(line))	//If file does not exist in the file_list then forward the request after decrementing the hop count.
       				{
       					int hops=Integer.parseInt(line[5])-1;
       					if(hops>0)
       					{
       						line[5]=Integer.toString(hops);
       						line[6]=Integer.toString(computer.id);
       						String msg=line[0]+" "+line[1]+" "+line[2]+" "+line[3]+" "+line[4]+" "+line[5]+" "+line[6];
       						computer.broadcast(msg);
       					}
       				}
       				else
       				{
       					String message="SEARCH_REP"+" "+line[1]+" "+computer.myhost+" "+computer.port+" "+line[4];
       					new reply(message);
       				}
    			}
    			else if(line[0].contentEquals("SEARCH_REP"))	//If Search Reply was received and requested node id matches your node id then consume the reply.
    			{
    				if(Integer.parseInt(line[1])==computer.id)
    				{
    					search.consume(line);
    				}
    				else
    				{
    					String message=line[0]+" "+line[1]+" "+line[2]+" "+line[3]+" "+line[4];
    					new reply(message);
    				}
    			}
    			else if(line[0].contentEquals("SEND_FILE"))	//Start sending file to the requested host and port.
    			{
    				new sendFile(line).start();
    			}
    			else if(line[0].contentEquals("DEPART_REQ"))	//If depart request was received send acknowledgment.
    			{
    				String msg = "DEPART_ACK "+computer.id+" "+computer.myhost+" "+computer.port;
    				if(!computer.depart)
    					new client(line[2],line[3],msg);
    				else if(computer.depart&&Integer.parseInt(line[1])<computer.id)	//If there are two or more concurrent requests break tie using id.
    					new client(line[2],line[3],msg);
    			}
    			else if(line[0].contentEquals("DEPART_ACK"))	//If depart acknowledgment was received increment acknowledgments.
    			{
    				computer.acks++;
    			}
    			else if(line[0].contentEquals("RECONNECT"))	//If reconnect was received then reconnect to the new neighbor specified in the message.
    			{
    				String old_neighbor = line[1]+" "+line[2]+" "+line[3];
    				String new_neighbor = line[4]+" "+line[5]+" "+line[6];
    				if(new_neighbor.contentEquals(computer.id+" "+computer.myhost+" "+computer.port))
    					computer.neighbor_list.remove(old_neighbor);
    				else
    				{
    					String msg = "JOIN "+computer.id+" "+computer.myhost+" "+computer.port;
        				new client(line[5],line[6],msg);
        				computer.neighbor_list.remove(old_neighbor);
        				if(computer.depart)//If node had been waiting for acknowledgment from the neighbor that was departing then add its acknowledgment. 
        					computer.acks++;
    				}
    			}	
    				
	    	}
    			
    	}catch (Exception e) {
	        	e.printStackTrace();
	    }
    }
    
}