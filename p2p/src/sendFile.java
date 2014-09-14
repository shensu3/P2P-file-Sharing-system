import java.io.*;
import java.net.*;

/**Sends the file from the local directory over the network to the computer
 * that requested for the file.*/
class sendFile extends Thread{
	
	String[] message;
	
	public sendFile(String[] message)
	{
		this.message = message;
	}
	
	public void run(){
		
		try {
			Socket socket = new Socket(message[2],Integer.parseInt(message[3]));  
			OutputStream os = socket.getOutputStream(); 
			File file = new File("./directory_"+computer.id+"/"+message[1]);
			byte [] bytearray = new byte [(int)file.length()];
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)); 
			bis.read(bytearray,0,bytearray.length);
			os.write(bytearray,0,bytearray.length);
		    os.flush();
		    bis.close();
			socket.close();			
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
}