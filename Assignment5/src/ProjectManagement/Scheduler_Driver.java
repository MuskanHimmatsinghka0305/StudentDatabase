package ProjectManagement;


import java.io.*;
//import java.util.Queue;
import java.net.URL;
import java.util.ArrayList;
import PriorityQueue.*;
import RedBlack.*;
import Trie.*;

public class Scheduler_Driver extends Thread implements SchedulerInterface {
       RBTree<String, Project> rbtree = new RBTree<String, Project>();
		MaxHeap<Job> jobs=new MaxHeap<Job>();
		int idcount=0;
		Job jobtoexecute;
        Trie<User> trie=new Trie<User>(); 
        ArrayList<User> users=new ArrayList<User>();
     ArrayList<Job> joblist=new ArrayList<Job>();  
     ArrayList<Job> completedjoblist=new ArrayList<Job>();
     ArrayList<Job> incompjoblist=new ArrayList<Job>();  
     ArrayList<Job> updatedlist=new ArrayList<Job>();
     ArrayList<Job> unfinishedlist=new ArrayList<Job>();
     RBTree<Project,Job> remjobs=new RBTree<Project, Job>();
     RBTree <String, Job> projectwisejobs=new RBTree<String, Job>();
     RBTree <String, Job> userwisejobs=new RBTree<String, Job>();
     RBTree <String, Job> projectanduserwisejobs=new RBTree<String, Job>();
     MaxHeap<Job> newheap=new MaxHeap<Job>();
        int globaltime=0;
        int jobcount=0;
       MaxHeap< UserReport> ur=new MaxHeap<UserReport>();

    public static void main(String[] args) throws IOException {
//

        Scheduler_Driver scheduler_driver = new Scheduler_Driver();
        File file;
        if (args.length == 0) {
            URL url = Scheduler_Driver.class.getResource("INP");
            file = new File(url.getPath());
        } else {
            file = new File(args[0]);
        }

        scheduler_driver.execute(file);
    }

    public void execute(File commandFile) throws IOException {


        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(commandFile));

            String st;
            while ((st = br.readLine()) != null) {
                String[] cmd = st.split(" ");
                if (cmd.length == 0) {
                    System.err.println("Error parsing: " + st);
                    return;
                }
                String project_name, user_name;
                Integer start_time, end_time;

                long qstart_time, qend_time;

                switch (cmd[0]) {
                    case "PROJECT":
                        handle_project(cmd);
                        break;
                    case "JOB":
                        handle_job(cmd);
                        break;
                    case "USER":
                        handle_user(cmd[1]);
                        break;
                    case "QUERY":
                        handle_query(cmd[1]);
                        break;
                    case "": // HANDLE EMPTY LINE
                        handle_empty_line();
                        break;
                    case "ADD":
                        handle_add(cmd);
                        break;
                    //--------- New Queries
                    case "NEW_PROJECT":
                    	 timed_report(cmd);
                    	 break;
                    case "NEW_USER":
                    	//System.out.println("User query");
                    	 timed_report(cmd);
                    	 break;
                    case "NEW_PROJECTUSER":
                    	//System.out.println("Project User query");
                    	 timed_report(cmd);
                    	 break;
                    case "NEW_PRIORITY":
                        timed_report(cmd);
                        break;
                    case "NEW_TOP":
                    	//System.out.println("Top query");
                        qstart_time = System.nanoTime();
                        timed_top_consumer(Integer.parseInt(cmd[1]));
                        qend_time = System.nanoTime();
                       // System.out.println("Printing top: "+cmd[1]+" users");
                      //  System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                        break;
                    case "NEW_FLUSH":
                    	//System.out.println("Flush query");
                        qstart_time = System.nanoTime();
                        timed_flush( Integer.parseInt(cmd[1]));
                        qend_time = System.nanoTime();
                       // System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                        break;
                    default:
                        System.err.println("Unknown command: " + cmd[0]);
                }

            }


            run_to_completion();
            print_stats();

        } catch (FileNotFoundException e) {
            System.err.println("Input file Not found. " + commandFile.getAbsolutePath());
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            

        }
    }

    @Override
    public ArrayList<JobReport_> timed_report(String[] cmd) {
        long qstart_time, qend_time;
        ArrayList<JobReport_> res = null;
        switch (cmd[0]) {
            case "NEW_PROJECT":
            	//System.out.println("Project query");
                qstart_time = System.nanoTime();
                res = handle_new_project(cmd);
                qend_time = System.nanoTime();
              /* System.out.println("Between: "+cmd[2]+" and "+cmd[3]+" and belongs to: "+cmd[1]);
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                for(int i=0;i<res.size();i++)
                {
                	JobReport j=(JobReport)res.get(i);
                	System.out.println("Job{id="+j.id+", user='"+j.user+"', project='"+"', jobstatus="+j.j.jobStatus+", start_time="+res.get(i).arrival_time()+", end_time="+res.get(i).completion_time()+", name="+j.jobname);
                }*/
                break;
            case "NEW_USER":
            	System.out.println("User query");
                qstart_time = System.nanoTime();
                res = handle_new_user(cmd);
                qend_time = System.nanoTime();
               /* System.out.println("Between: "+cmd[2]+" and "+cmd[3]+" and belongs to: "+cmd[1]);
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                for(int i=0;i<res.size();i++)
                {
                	JobReport j=(JobReport)res.get(i);
                	System.out.println("Job{id="+j.id+", user='"+j.user+"', project='"+res.get(i).project_name()+"', jobstatus=REQUESTED, start_time="+res.get(i).arrival_time()+", end_time="+res.get(i).completion_time()+", name="+j.jobname);
                }*/
                break;
            case "NEW_PROJECTUSER":
            	System.out.println("Project User query");
                qstart_time = System.nanoTime();
                res = handle_new_projectuser(cmd);
                qend_time = System.nanoTime();
               /* System.out.println("Between: "+cmd[3]+" and "+cmd[4]+" and belongs to: "+cmd[1]+","+cmd[2]);
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                for(int i=0;i<res.size();i++)
                {
                	JobReport j=(JobReport)res.get(i);
                	System.out.println("Job{id="+j.id+", user='"+j.user+"', project='"+res.get(i).project_name()+"', jobstatus=REQUESTED, start_time="+res.get(i).arrival_time()+", end_time="+res.get(i).completion_time()+", name="+j.jobname);
                }*/
                break;
            case "NEW_PRIORITY":
            	System.out.println("Priority query");
                qstart_time = System.nanoTime();
                res = handle_new_priority(cmd[1]);
                qend_time = System.nanoTime();
             /* System.out.println("Priority:" + cmd[1]+" or more");
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                for(int i=0;i<res.size();i++)
                {
                	JobReport j=(JobReport)res.get(i);
                	System.out.println("Job{id="+j.id+", user='"+j.user+"', project='"+res.get(i).project_name()+"', jobstatus=REQUESTED, start_time="+res.get(i).arrival_time()+", end_time="+res.get(i).completion_time()+", name="+j.jobname);
                }*/
                break;
        }

        return res;
    }

    @Override
    public ArrayList<UserReport_> timed_top_consumer(int top) {
    	for(int i=0;i<users.size();i++)
    	{
    		ur.insert(users.get(i).report);
    	}
        ArrayList<UserReport_>  toreturn=new ArrayList<UserReport_>();
        for(int i=0;(i<top && !ur.heap.isEmpty());i++)
        {
        	UserReport rep= ur.extractMax();
        	//System.out.println(rep.user);
        toreturn.add((UserReport_)rep);}
    /*    
        for(int i=0;i<toreturn.size();i++)
        {
        	System.out.println("User{ name='"+toreturn.get(toreturn.size()-i-1).user()+"', usage="+toreturn.get(toreturn.size()-i-1).consumed()+"}");
        }*/
        return toreturn;
    }



    @Override
    public void timed_flush(int waittime) {
    	MaxHeap<Job> flush=new MaxHeap<Job>();
    	MaxHeap<Job> newheap=new MaxHeap<Job>();
       for(int i=0;i<unfinishedlist.size();i++)
       {
    	   Job j=unfinishedlist.get(i);
    	   int wait=globaltime-j.report.arrival_time();
    	   if(wait>=waittime && j.p.budget>=j.runtime )
    	   {
    		  // j.priority=9999;
    		   flush.insert(j);
    		 // System.out.println(j.id);
    	   }
    	   //System.out.println("--Flushed: Job{user="+j.u.username+", project='"+j.p.name+"', jobstatus="+j.jobStatus+", execution_time="+j.runtime+", end_time=null, priority="+j.priority+", name='"+j.name+"'}");
       }
      
   while(flush.top()!=null) {

    	Job j=flush.extractMax();
    	if(j.p.budget>=j.runtime) {
    	  jobtoexecute=j;
    	  j.jobStatus="COMPLETED";
		   execute_a_job();}
    	//System.out.println("Flushed: Job{id="+j.report.id+", priority="+j.p.getpriority()+" user='"+j.report.user+"', project='"+j.p.name+"', jobstatus="+j.jobStatus+", start_time="+j.report.arrival_time()+", end_time="+j.report.completion_time()+", name="+j.name+"'}");
    }

    }
    

    private ArrayList<JobReport_> handle_new_priority(String s) {
        ArrayList<JobReport_> toreturn=new ArrayList<JobReport_>();
        int priority=Integer.parseInt(s);
        for(int i=0;i<unfinishedlist.size();i++)
        {
        	
        	if(unfinishedlist.get(i).priority>=priority)
        	{
        		JobReport jr=unfinishedlist.get(i).report;
        		toreturn.add(jr);
        	}
        }
        return toreturn;
    }

    private ArrayList<JobReport_> handle_new_projectuser(String[] cmd) {
    	ArrayList<JobReport_> toreturn=new ArrayList<JobReport_>();
    	ArrayList<Job> jobsofprojectanduser=(ArrayList<Job>)projectanduserwisejobs.search((cmd[1]+cmd[2])).getValues();
    	int t1=Integer.parseInt(cmd[3]);
    	int t2=Integer.parseInt(cmd[4]);
    	for(int i=0;i<jobsofprojectanduser.size();i++)
    	{
    	    JobReport jr=jobsofprojectanduser.get(i).report;
    	    if(jr.arrival_time()>=t1 && jr.arrival_time()<=t2)
    	    {
    	    	toreturn.add(jr);
    	    }
    	}
        return toreturn;
    }

    private ArrayList<JobReport_> handle_new_user(String[] cmd) {
    	ArrayList<JobReport_> toreturn=new ArrayList<JobReport_>();
    	ArrayList<Job> jobsofuser=(ArrayList<Job>)userwisejobs.search(cmd[1]).getValues();
    	int t1=Integer.parseInt(cmd[2]);
    	int t2=Integer.parseInt(cmd[3]);
    	for(int i=0;i<jobsofuser.size();i++)
    	{
    	    JobReport jr=jobsofuser.get(i).report;
    	    if(jr.arrival_time()>=t1 && jr.arrival_time()<=t2)
    	    {
    	    	toreturn.add(jr);
    	    }
    	}
        return toreturn;
    }

    private ArrayList<JobReport_> handle_new_project(String[] cmd) {
    	ArrayList<JobReport_> toreturn=new ArrayList<JobReport_>();
    	ArrayList<Job> jobsofproject=(ArrayList<Job>)projectwisejobs.search(cmd[1]).getValues();
    	int t1=Integer.parseInt(cmd[2]);
    	int t2=Integer.parseInt(cmd[3]);
    	for(int i=0;i<jobsofproject.size();i++)
    	{
    	    JobReport jr=jobsofproject.get(i).report;
    	    if(jr.arrival_time()>=t1 && jr.arrival_time()<=t2)
    	    {
    	    	toreturn.add(jr);
    	    }
    	}
        return toreturn;
    }




    public void schedule() {
Job j=jobs.top();
    if(j==null)
    {
    	return;
    }
    	if(j.jobStatus.equals("COMPLETED")) {
    		jobs.extractMax();
    		schedule();
    		return;
    	}
    	if(!incompjoblist.isEmpty()&&j!=null) {
    		if(incompjoblist.get(0).compareTo(j)>=0) {
    			j=incompjoblist.get(0);
    			int budget=j.getproject().getbudget();
  	          int time=j.runtime;
    	   if(budget>=time)
    	   {
    		   System.out.println("Executing: "+j.name+" from: "+j.p.name);
    		   j.jobStatus="COMPLETED";
    		   j.report.jobstatus="COMPLETED";
    		   jobtoexecute=j;
            execute_a_job();
            System.out.println("Project: "+j.p.name+" budget remaining: "+j.p.budget);
            return;
    	   }}
    	}
    	    
    	    	  j=jobs.extractMax();
    	    	  if(j==null)
    	    	  {
    	    		  return;
    	    	  }

    	          
    	          int budget=j.getproject().getbudget();
    	          int time=j.runtime;
    	          if(budget>=time)
    	          {
    	        	  System.out.println("Executing: "+j.name+" from: "+j.p.name);
    	    		   j.jobStatus="COMPLETED";
    	    		   j.report.jobstatus="COMPLETED";
    	    		   jobtoexecute=j;
    	        	  execute_a_job(); 
    	        	  System.out.println("Project: "+j.p.name+" budget remaining: "+j.p.budget);
    	        	  }else {
    	        		  System.out.println("Executing: "+j.name+" from: "+j.p.name);
    	            	  System.out.println("Un-sufficient budget.");
    	            	  j.jobStatus="REQUESTED";
    	            	  j.report.jobstatus="REQUESTED";
    	            	  jobcount--;
    	            	  incompjoblist.add(j);
    	            	  schedule();
    	              }
    	            
    	              return;
    }

    public void run_to_completion() {
        while(!jobs.heap.isEmpty())
    	{
    		//jobs.extractMax();
    		System.out.println("Running code");
        	System.out.println("Remaining jobs: "+jobcount);
        	schedule();
        	System.out.println("System execution completed");
    	}
 

    }

    public void print_stats() {
     System.out.println("--------------STATS---------------");
    	System.out.println("Total jobs done: "+completedjoblist.size());
    	for(int i=0;i<completedjoblist.size();i++)
    	{
    		System.out.println(completedjoblist.get(i).toString());
    		
    	}
    	System.out.println("------------------------");
    	System.out.println("Unfinished jobs: ");
    	for(int i=0;i<incompjoblist.size();i++)
    	{
    		System.out.println(incompjoblist.get(i).toString());
    	}
    	System.out.println("Total unfinished jobs: "+incompjoblist.size());
    	System.out.println("--------------STATS DONE---------------");


    }

    public void handle_add(String[] cmd) {
               int addition = Integer.parseInt(cmd[2]);
         
          RedBlackNode<String, Project> rbnode= rbtree.search(cmd[1]);
          rbnode.getValue().budget+=addition;
          for(int i=0;i<incompjoblist.size();i++)
          {
        	  if(incompjoblist.get(i).p.name.equals(cmd[1]))
        	  {
        		  jobcount++;
        	  }
          }
          System.out.println("ADDING Budget");

    }

    public void handle_empty_line() {	
       System.out.println("Running code");
    	System.out.println("Remaining jobs: "+jobcount);
    	schedule();
    	System.out.println("Execution cycle completed");
    }


    public void handle_query(String key) {
      System.out.println("Querying");
    	int count=0;
         for(int i=0;i<joblist.size();i++)
         {
        	 if(joblist.get(i).name.equals(key))
        	 {
        		 System.out.println( key+": "  + joblist.get(i).getStatus());
        		 count++;
        		 break;
        	 }
         }
         if(count==0)
         {
        	 System.out.println("Doesnotexists: NO SUCH JOB");
         }
    }

    public void handle_user(String name) {
        User u=new User();
        u.username=name;
        u.report.user=name;
        trie.insert(name, u);
        System.out.println("Creating user");
  
       users.add(u);
    //   System.out.println(ur.extractMax().user);

    }

    public void handle_job(String[] cmd) {
     System.out.println("Creating job");
    	Job j;
        RedBlackNode<String, Project> rb=rbtree.search(cmd[2]);
        if(rb==null)
        {
        	// System.out.println("+++++++++++++++++++++++++++");
        	System.out.println("No such project exists. "+cmd[2]);
        	return;
        }else {
       
        
        TrieNode<User> tn= trie.search(cmd[3]);
        if(tn==null)
        {
        	System.out.println("No such user exists: "+cmd[3]);
        	return;
        }else
        {
        	 j=new Job();
     
             Project p=rb.getValue();
             j.p=p;
             j.u=tn.getValue();
             j.priority=p.priority;
             j.runtime=Integer.parseInt(cmd[4]);
             j.name=cmd[1];
          j.report.setarrivaltime(globaltime);
              j.report.setbudget(j.p.getbudget());
              j.report.user=j.u.username;
              jobs.insert(j);
              joblist.add(j);
              unfinishedlist.add(j);
              String key=p.name+j.u.username;
              projectanduserwisejobs.insert(key, j);
             userwisejobs.insert(j.u.username, j);
             projectwisejobs.insert(j.p.name, j);
             jobcount++;
             j.report.id=idcount;
             idcount++;
             
             j.report.runtime=j.runtime;
             j.report.jobname=j.name;
    }}

    }

    public void handle_project(String[] cmd) {
        Project p=new Project(cmd[1], Integer.parseInt(cmd[2]),Integer.parseInt(cmd[3]));
        rbtree.insert( cmd[1], p);
        System.out.println("Creating project");

    }

    public void execute_a_job() {
     
    	int budget=jobtoexecute.getproject().getbudget();
          int time=jobtoexecute.runtime;
         	  globaltime+=time;
         	  jobtoexecute.projectcompletedtime=Integer.toString(globaltime);
         	  jobtoexecute.getproject().budget=budget-time;
         	 jobtoexecute.u.report.budgetconsumed+=time;
        	  jobtoexecute.u.report.consumptiontime+=time;
        	  jobtoexecute.u.report.lastcompletiontime=globaltime;
         	 jobtoexecute.report.setcompletiontime(globaltime);
         	 completedjoblist.add(jobtoexecute);
       	  incompjoblist.remove(jobtoexecute);
       	  unfinishedlist.remove(jobtoexecute);
       	  jobcount--;
       	 jobtoexecute.priority=jobtoexecute.p.getpriority();
       	
   
   	       	     return;
    }
    public void timed_handle_user(String name){
        User u=new User();
        u.username=name;
        u.report.user=name;
        trie.insert(name, u);
     //   System.out.println("Creating user");
  
       users.add(u);	
    }
    public void timed_handle_job(String[] cmd){
    //	System.out.println("Creating job");
    	Job j;
        RedBlackNode<String, Project> rb=rbtree.search(cmd[2]);
        if(rb==null)
        {
        	// System.out.println("+++++++++++++++++++++++++++");
        	//System.out.println("No such project exists. "+cmd[2]);
        	return;
        }else {
       
        
        TrieNode<User> tn= trie.search(cmd[3]);
        if(tn==null)
        {
        	//System.out.println("No such user exists: "+cmd[3]);
        	return;
        }else
        {
        	 j=new Job();
     
             Project p=rb.getValue();
             j.p=p;
             j.u=tn.getValue();
             j.priority=p.priority;
             j.runtime=Integer.parseInt(cmd[4]);
             j.name=cmd[1];
          j.report.setarrivaltime(globaltime);
              j.report.setbudget(j.p.getbudget());
              j.report.user=j.u.username;
              jobs.insert(j);
              joblist.add(j);
              unfinishedlist.add(j);
              String key=p.name+j.u.username;
              projectanduserwisejobs.insert(key, j);
             userwisejobs.insert(j.u.username, j);
             projectwisejobs.insert(j.p.name, j);
             jobcount++;
             j.report.id=idcount;
             idcount++;
             
             j.report.runtime=j.runtime;
             j.report.jobname=j.name;
    }}
    }
    public void timed_handle_project(String[] cmd){
    	  Project p=new Project(cmd[1], Integer.parseInt(cmd[2]),Integer.parseInt(cmd[3]));
          rbtree.insert( cmd[1], p);
        //  System.out.println("Creating project");
    }
    public void timed_run_to_completion(){
    	 while(!jobs.heap.isEmpty())
     	{
     		//jobs.extractMax();
     	//	System.out.println("Running code");
         //	System.out.println("Remaining jobs: "+jobcount);
         	schedule();
         	//System.out.println("System execution completed");
     	}
    }


    }

