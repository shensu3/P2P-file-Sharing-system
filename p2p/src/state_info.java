/**Stores the information if a new search request is made. This information is then
 * added into a list.*/
public class state_info 
{
	String filename;
	int fromid;
	int transitid;
	long time;
	
	public state_info(int id,String file,int tid)
	{
		time=System.currentTimeMillis();
		filename=file;
		fromid= id;
		transitid=tid;
	}
}
