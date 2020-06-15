package ProjectManagement;

public class Job implements Comparable<Job> {

	//String username;
	//String pname;
    int id;
	Project p;
	int priority;
	int runtime;
	String name;
	User u;
	JobReport report; 
	String projectcompletedtime;
	String jobStatus="NOT FINISHED";
	int timetaken;
	Job(){
	report =new JobReport(name);
	report.j=this;
	}
	
    @Override
    public int compareTo(Job job) {
        if(this.priority>job.priority)
        {
        	return 1;
        }else if(this.priority<job.priority)
        {
        	return -1;
        }else if(this.priority==job.priority)
        {
        	if(this.p.getpriority()>job.p.getpriority())
        	{
        		return 1;
        	}else if(this.p.getpriority()<job.p.getpriority())
        	{
        		return -1;
        	}
        }
        return 0;
    }
   
    
    
    public Project getproject() {
    	return p;
    }
    
    public User getuser() {
    	return u;
    }
    
    public String getStatus() {
    	return jobStatus;
    }
    
    public String toString()
    {
    	Job j=this;
		return("Job{id="+this.report.id+", user='"+j.getuser().username+"', project='"+j.getproject().name+"', jobstatus="+j.getStatus()+", execution_time="
				           +j.runtime+", end_time="+ j.projectcompletedtime+", name='"+j.name+"'}");
		
    }
    
  
}