package edu.app;

public class MiniStatement {

	public static void main(String[] args) {
		
		Bankdto bank=new Bankdto();
		AccountStatement accst=new AccountStatement();
		bank.setAccName("SDC");
		bank.setAccNum(12345);
		bank.setSaveAcc(500);
		bank.setDebitAcc(2000);
		bank.setCreditAcc(400);
		System.out.println("**********");
		System.out.println(bank.getAccName());
		System.out.println(bank.getAccNum());
		System.out.println(bank.getSaveAcc());
		accst.debitedAccount(bank);
		System.out.println(bank.getTotal());
		System.out.println( bank.getTotal1());
	}

}
