package ProjectManagement;
import PriorityQueue.*;

public class UserReport implements Comparable<UserReport>, UserReport_ {

	
	
	 String user;
    int consumptiontime;
  int budgetconsumed;
  int lastcompletiontime;
	
	public String user() {
		return user;
	}
	
	public int consumed() {
		return consumptiontime;
	}
	
	public int compareTo(UserReport ur)
	{
		if ( this.consumptiontime<ur.consumptiontime)
	      {
	    	  return 1;
	      }else
	    if(this.consumptiontime>ur.consumptiontime)
	    {
	    	return -1;
	    }else if(this.consumptiontime==ur.consumptiontime)
	    {
	    	
	    		if(this.lastcompletiontime<ur.lastcompletiontime)
	    		{
	    			return 1;
	    		}else if(this.lastcompletiontime>ur.lastcompletiontime)
	    		{
	    			return -1;
	    		}
	    	}
	    
	      return 0;
	}
}
