package ProjectManagement;

public class JobReport implements JobReport_ {
	
	 String user;
	 int id;
	 String project_name;
	private int budget;
	private int arrival_time;
	private int completion_time;
	int runtime;
	String jobname;
	String jobstatus="NOT FINISHED";
	Job j;
	
	public JobReport(String user) {
		this.user=user;
	}
	
	public void setbudget(int budget)
	{
		this.budget=budget;
	}
	
	public void setarrivaltime(int time)
	{
		this.arrival_time=time;
	}
	
	public void setcompletiontime(int time)
	{
		this.completion_time=time;
	}
	
     public String user()
	{
		return user;
	}
	
	public String project_name() {
		return project_name;
	}
	
	public int budget() {
		return budget;
	}
	
	public int arrival_time() {
		return this.arrival_time;
	}
	
	public int completion_time() {
		return this.completion_time;
	}
	

}
