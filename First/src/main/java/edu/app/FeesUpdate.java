package edu.app;

public class FeesUpdate {
	public StudentListdto amount(StudentListdto fee)
	{
		int add= fee.getRollNumber();
	    String add1=fee.getName()+fee.getDepartMent();
	    int s1=fee.getCurrentfees()+fee.getStufees();
	    fee.setTotalfees(s1);
		return fee;
	    
	    }

	

}


