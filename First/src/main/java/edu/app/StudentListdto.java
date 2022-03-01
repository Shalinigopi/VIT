package edu.app;

public class StudentListdto {
	private int rollNumber;
	private String name;
	private static final String collegeName="VIT";
	private String departMent;
	private int stufees;
	private int currentfees;
	private int totalfees;
	public int getRollNumber() {
		return rollNumber;
	}
	public void setRollNumber(int rollNumber) {
		this.rollNumber = rollNumber;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepartMent() {
		return departMent;
	}
	public void setDepartMent(String departMent) {
		this.departMent = departMent;
	}
	public int getStufees() {
		return stufees;
	}
	public void setStufees(int stufees) {
		this.stufees = stufees;
	}
	public int getCurrentfees() {
		return currentfees;
	}
	public void setCurrentfees(int currentfees) {
		this.currentfees = currentfees;
	}
	public int getTotalfees() {
		return totalfees;
	}
	public void setTotalfees(int totalfees) {
		this.totalfees = totalfees;
	}
	public static String getCollegename() {
		return collegeName;
	}
}