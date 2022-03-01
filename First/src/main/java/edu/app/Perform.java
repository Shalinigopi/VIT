package edu.app;

public class Perform {
	
public static void main(String[] args) {
	Studentdto stu= new Studentdto();
	Servicedto s1=new Servicedto();
	stu.setStuRoll(18744);
	stu.setStuName("Shalinigopi");
	stu.setMathsMark(80);
	stu.setSciMark(90);
	stu.setTamilMark(92);
	stu.setTotalMaxMark(150);
	stu.setTotalsciMark(150);
	stu.setTotaltamilMark(150);
	s1.calculateMark(stu);
	System.out.println("**********"); 
	System.out.println("StudentName:"+stu.getStuName());
	System.out.println("Rollnumber:"+stu.getStuRoll());
	System.out.println(stu.getMaxMark() + "/"+stu.getTotalMaxMark());
}
}