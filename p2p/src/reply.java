
/**This class is used to handle the search reply message. It forwards the message to the 
 * neighbor from which it received the request.*/
public class reply {
	
	public reply(String message)
	{
		String rm[]=message.split(" ");
		int element=-1;
		for(int i=0;i<computer.state.size();i++)
		{
			if(Integer.parseInt(rm[1])==computer.state.get(i).fromid)
			element=i;
		}
		if(element>=0) // exists in the state list
		{
			int idd=computer.state.get(element).transitid;
			for(int i=0;i<computer.neighbor_list.size();i++)
			{
				String temp[]=computer.neighbor_list.get(i).split(" ");
				if(Integer.parseInt(temp[0])==idd)
				{
					try {
						new client(temp[1],temp[2],message);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			System.out.println("Search request failed: Reason - Reply message cannot be forwarded. Neighbor may have left" +
					"the network.!");
		}
	}

}
