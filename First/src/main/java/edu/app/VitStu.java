package edu.app;

import java.util.ArrayList;
import java.util.List;

public class VitStu {



	public static void main(String[] args) {
		
		StudentListdto dto=new StudentListdto();
		dto.setRollNumber(001);
		dto.setName("Shalini");
		dto.setDepartMent("MCA");
	   dto.setStufees(50000);
		dto.setCurrentfees(1000);
		
		StudentListdto dto1=new StudentListdto();
		dto1.setRollNumber(002);
		dto1.setName("Ramya");
		dto1.setDepartMent("MSC");
		 dto1.setStufees(50000);
		dto1.setCurrentfees(2000);
		
		StudentListdto dto3=new StudentListdto();
		dto3.setRollNumber(003);
		dto3.setName("Durga");
		dto3.setDepartMent("BCA");
		 dto3.setStufees(50000);
		dto3.setCurrentfees(1500);

		List<StudentListdto> studentDetail = new ArrayList<StudentListdto>();
		List<FeesUpdate> FeesUpdate = new ArrayList<FeesUpdate>();
		
		FeesUpdate dto2=new FeesUpdate();
		FeesUpdate.add(dto2);
		studentDetail.add(dto);
		studentDetail.add(dto1);
		studentDetail.add(dto3);
		FeesUpdate dto4=new FeesUpdate();
		dto4.amount(dto);
		dto4.amount(dto1);
		dto4.amount(dto3);
		
		
		studentDetail.forEach(stud ->
		{
			System.out.println("Student RollNumber:"+stud.getRollNumber());
			System.out.println("Student Name:"+stud.getName());
			System.out.println("Student Department:"+stud.getDepartMent());
			System.out.println("Student CollegeName:"+StudentListdto.getCollegename());
//			System.out.println("Student TuitionFee & sem Fee:
			System.out.println(stud.getTotalfees());
			
		});
	}
	


}
	    
	    
	    
		
	
		

