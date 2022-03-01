package edu.app;

public class Servicedto {
	public void  calculateMark (Studentdto stu)
	{
		int maxTotal=stu.getMathsMark()+stu.getSciMark()+stu.getTamilMark();
		int rankTotal=stu.getTotalmathsMark()+stu.getTotalsciMark()+stu.getTotaltamilMark();
		stu.setMaxMark(rankTotal);
		stu.setTotalMaxMark(maxTotal);
		
	}	
	}
	
