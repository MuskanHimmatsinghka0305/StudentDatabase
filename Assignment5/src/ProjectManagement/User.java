package ProjectManagement;

public class User implements Comparable<User> {


	String username;
	UserReport report;
	
	User(){
		report=new UserReport();
	}
	
	

    @Override
    public int compareTo(User user) {
      return this.username.compareTo(user.username);
    }
    
   // UserReport report=new UserReport();
    
    public String getusername() {
    	return username;
    }
}
