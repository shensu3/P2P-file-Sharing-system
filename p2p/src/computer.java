/**
 * Authors	: Arun Kumar Konduru Chandra & Sushen Kumar Manchukanti
 * NetID	: axk138230 
 * Date 	: 07/15/2014
 * Class	: CS6378.0U2
 * Purpose	: Programming project for Advanced Operating Systems course.
 */

import java.io.*;
import java.net.*;
import java.util.*;

/**Class computer */
public class computer extends Thread{

	public static int id, port, choice, acks = 0;
	public static boolean depart = false, found = false;
	public static String myhost;
	public static File file, folder, p2p_file;
	public static List<String> file_list = new ArrayList<String>();
	public static List<String> p2p_list = new ArrayList<String>();
	public static List<String> neighbor_list = new ArrayList<String>();
	public static List<state_info> state = new ArrayList<state_info>();
	public static List<String> result_list = Collections.synchronizedList(new ArrayList<String>()); 
	public static List<String> searchReq_list = Collections.synchronizedList(new ArrayList<String>());
	public static void main(String args[]) 
	{
		Scanner in = new Scanner(System.in);
		System.out.println("Enter computer ID:");
		id = Integer.parseInt(in.nextLine());
		System.out.println("Enter port address for this computer:");
		port = Integer.parseInt(in.nextLine());
		server s = new server();
		stateThread st = new stateThread();
		s.setDaemon(true);
		st.setDaemon(true);
		s.start();	//Starts listening for client connections.
		st.start();
		//Creates all necessary files that do not exist.
		try{
			file = new File("file_"+id+".txt");
			p2p_file = new File("p2p.txt");
			if(!file.exists())
				file.createNewFile();
			if(!p2p_file.exists())
				p2p_file.createNewFile();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		writeFileNames();	//Writes all files in sub-directory to the file_id.txt.
		readFileNames();	//Reads all lines from file_id.txt into file_list.
		joinP2PSystem();	//Initiates the process of joining P2P network.
		do{
			System.out.println("What would you like to do? \n 1.Search and download a file."
					+"\n 2.Exit the P2P network. \n Enter your choice:");
			choice = Integer.parseInt(in.nextLine());
			if(choice==1)
			{
				System.out.println("Enter the file name or keyword:");
				String file = in.nextLine();
				new search(file);
			}
			else if(choice==2)
			{
				depart = true;
				if(neighbor_list.size()>0)
					new depart();					
			}
			else
				System.out.println("Invalid option.");
		}while(choice!=2);
		s.interrupt();
		st.interrupt();
		removeFromActiveP2PSystem();
		in.close();
		System.out.println("System has gone offline.");
	}
	
	/**ReadFileNames() method reads all the list of files and their associated keywords 
	 * written in text file and stores it into file_list.*/
	public static void readFileNames()
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader("file_"+id+".txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				file_list.add(line);	//Adds all file names in the directory and associated keywords into the list.
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**WriteFileNames() method reads all the files in the directory and writes the file 
	 * names into the file_id.txt.*/
	public static void writeFileNames()
	{
		folder = new File("directory_"+id);
		//Creates an empty directory if the directory does not exist already.
		if(!folder.exists())
			folder.mkdir();
		File[] listOfFiles = folder.listFiles();
		try{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < listOfFiles.length; i++) {
				out.write(listOfFiles[i].getName());	//Writes all file names in the directory into the text file.
		    	out.newLine();
			}
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**JoinP2PSystem() method establishes a TCP connection with a random node that is already 
	 * in the P2P System.*/
	public static void joinP2PSystem()
	{
		try{
			myhost = InetAddress.getLocalHost().getHostName();
			//If there is at least one active node then initiate joining the P2P network.
			if(p2p_file.length()>0)
			{
				getActiveP2PSystems();	//Gets all the active P2P nodes in the network.
				Random rand = new Random();
				int random = rand.nextInt(p2p_list.size());
				String[] node = p2p_list.get(random).split(" ");
				String msg = "JOIN "+id+" "+myhost+" "+port;
				new client(node[1],node[2],msg);	//Sends a join message to single random node to connect.
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(p2p_file,true));
			out.write(id+" "+myhost+" "+port);
			out.newLine();
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**GetActiveP2PSystems() method reads all the active nodes and their host addresses 
	 * from p2p text file and adds it into the p2p_list.*/
	public static void getActiveP2PSystems()
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader("p2p.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				p2p_list.add(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**Used to broadcast message to all the neighbors of the node.*/	
	public static void broadcast(String message)
	{
		for(int i=0;i<neighbor_list.size();i++)
		{
			String node_info[]=neighbor_list.get(i).split(" ");
			try {
				new client(node_info[1],node_info[2],message);
			} catch (Exception e) {
				System.out.println("Unable to broadcast message.");
			}
		}
	}
	
	/**RemoveFromActiveP2PSystems() method removes its node information from p2p.txt file
	 * before terminating.*/
	public static void removeFromActiveP2PSystem()
	{
		File tempFile = new File("p2p.tmp");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(p2p_file));
			PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
			String strLine;
			while((strLine = reader.readLine())!= null)
            {
            	if(!strLine.contentEquals(id+" "+myhost+" "+port))
            	{
            		/*Writes all those lines that do not match the name 
            		to be deleted into temporary file.*/
            		writer.println(strLine);	
            	}
            }
	        reader.close();
	        writer.close();
	        p2p_file.delete();	//Deletes the text file.
            tempFile.renameTo(p2p_file);	//Temporary file is renamed to the text file.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
