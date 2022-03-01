package edu.app;

public class AccountStatement {
	public void debitedAccount (Bankdto am)
	{
	int total=am.getSaveAcc()+am.getDebitAcc();
	int total1=am.getSaveAcc()+am.getDebitAcc()-am.getCreditAcc();
	am.setTotal(total);
	am.setTotal1(total1);
	
	}

}
