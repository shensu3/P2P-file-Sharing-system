import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Scanner;

/**Search class initiates the search process for a file. It sends a search request and waits 
 * for reply till the file was found or hop count exceeds 16.*/
public class search{
	
	int ttl=150;
	int hopcount=1;
	int port;
	long time = System.currentTimeMillis();
	File file;
	String file_name;
	
	public search(String file_name)
	{
		this.file_name = file_name;
		try{
			
			while(hopcount<16 && !computer.found)
			{
				//Send search request to all neighbors to start the process of searching for a file.
				String message="SEARCH_REQ"+" "+computer.id+" "+computer.myhost
						+" "+computer.port+" "+file_name+" "+hopcount+" "+computer.id;
				computer.broadcast(message);
				Thread.sleep(ttl*hopcount);
				hopcount=hopcount*2;
			}
			
			if(computer.found)
			{
				@SuppressWarnings("resource")
				Scanner inp =new Scanner(System.in);
				System.out.println("Hop count: "+hopcount);
				System.out.println("Time elapsed in ms: "+(System.currentTimeMillis()-time));
				System.out.println("Where do you want to download from?");
				for(int i=0;i<computer.result_list.size();i++)
				{
					System.out.println(i+") "+computer.result_list.get(i));
				}
				int choice = Integer.parseInt(inp.nextLine());
				if(choice<0||choice>computer.result_list.size())
					System.out.println("Invalid option. File was not downloaded.");
				else
				{
					Random rand = new Random();
					port = 4000+rand.nextInt(10);
					String msg = "SEND_FILE "+file_name+" "+computer.myhost+" "+port;
					String[] node = computer.result_list.get(choice).split(" ");
					new client(node[0],node[1],msg);
					System.out.println("Starting file download...");
					download();
					System.out.println("Download complete.");
				}
			}
			else
				System.out.println("Requested file does not exist.");
			
			computer.found=false;
			computer.result_list.clear();
			computer.writeFileNames();
			computer.readFileNames();
		
		}catch(Exception e){
			System.out.println("Exception: Possible reasons \n 1.Target machine may have gone offline. \n" +
					" 2.Text file you are downloading might be empty. \n 3.Thread might have been interrupted from its sleep.");
			computer.found=false;
			computer.result_list.clear();
			computer.writeFileNames();
			computer.readFileNames();
		}
	}
	
	/**Checks if the search request is present in the state_info list.*/
	public static Boolean requestHandle(String message[])
	{
		Boolean flag=false;
		
		for(int i=0;i<computer.state.size();i++)
		{
			if(computer.state.get(i).filename.equals(message[4]) && computer.state.get(i).fromid==Integer.parseInt(message[1]))
				flag=true;
		}
		if(flag==false)
		{
			state_info st=new state_info(Integer.parseInt(message[1]),message[4],Integer.parseInt(message[6]));
			computer.state.add(st);
			if((find(message[4]))==true)
				return true;
			else
				return false;
		}	
		else 
			return false;
	}
	
	/**Checks if the requested file is present in the file_list.*/
	public static Boolean find(String filename)
	{
		Boolean flag=false;
		
		for(int i=0;i<computer.file_list.size();i++)
		{
			if(computer.file_list.get(i).equals(filename))
				flag=true;
		}
		
		return flag;
	}
	
	/**Collects all replies till the timer expires.*/
	public static void consume(String message[])
	{
		computer.result_list.add(message[2]+" "+message[3]);
		computer.found=true;
	}
	
	/**Start a separate server and listen for client sending the file to download.*/
	public void download()
	{
		int bytesRead;
		ServerSocket serverSocket;
		try {
			serverSocket = new ServerSocket(port);
			Socket socket = serverSocket.accept();
			byte [] bytearray = new byte [2048]; 
			InputStream is = socket.getInputStream(); 
			File file = new File("./directory_"+computer.id+"/"+file_name);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file)); 
			bytesRead = is.read(bytearray,0,bytearray.length);  
			bos.write(bytearray, 0 , bytesRead); 
			bos.close();
			socket.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
