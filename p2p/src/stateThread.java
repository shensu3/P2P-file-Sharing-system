/**Used to delete the state information periodically i.e, when the time period of 
 * the search request expires.*/
public class stateThread extends Thread {
	public void run()
	{
		try
		{
			while(true)
			{
				for(int i=0;i<computer.state.size();i++)
				{
					if(System.currentTimeMillis()-computer.state.get(i).time>2000)
						computer.state.remove(i);
				}
			Thread.sleep(50);
			}
		}
		catch(Exception e)
		{
			//e.printStackTrace();
		}
	}
	
}
