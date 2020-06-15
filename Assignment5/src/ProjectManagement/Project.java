package ProjectManagement;



public class Project implements Comparable<Project>{

	String name;
	Integer budget;
	Integer priority;
	
	public Project(String name, int priority, int budget)
	{
		this.name=name;
		this.priority=priority;
		this.budget=budget;
	}
	
	public Project(String name)
	{
		this.name=name;
	}
	public String getname() {
		return name;
	}
	
	public int getbudget() {
		return budget;
	}
	
	public int getpriority() {
		return priority;
	}
	public String toString() {
		return name;
	}
	
	public int compareTo(Project p)
	{
		if(p.getpriority()>this.getpriority())
		{
			return -1;
		}else if(p.getpriority()<this.getpriority())
		{
			return 1;
		}
		return 0;
	}
}
