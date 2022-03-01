package edu.app;

public class Cals {

	 private String name;
	private int acno;
	
	 
	 public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getAcno() {
		return acno;
	}


	public void setAcno(int acno) {
		this.acno = acno;
	}
public void display (Cals a)
{
	Cals a4=a;
	System.out.println(a.getAcno());
	System.out.println(a.getName());
	
	a.setAcno(200);
	
	
}

	public static void main(String[] args) {
		Cals a1=new Cals();
		Cals a2=new Cals();
		a1.setAcno(100);
		a1.setName("abc");
		a2.setAcno(1000);
		a2.setName("efg");
		a1.display(a2);
		System.out.println("a1.getAcno");
		
	}

}
