package com.eydlin.revolut.account;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.eydlin.revolut.ApplicationException;
import com.eydlin.revolut.account.Account;
import com.eydlin.revolut.account.AccountImpl;

public class AccountImplTest {
	
	@Test
	public void createAccountTest() {
		Account account = new AccountImpl();
		assertTrue(account.getBalance().equals(BigDecimal.ZERO));
	}
	
	@Test
	public void addPositiveAmountTest() throws ApplicationException {
		Account account = new AccountImpl();
		account.addAmount(new BigDecimal("3.44"));
		account.addAmount(new BigDecimal("2.56"));
		assertTrue(account.getBalance().compareTo(new BigDecimal("6")) == 0);
	}
	
	@Test
	public void addNegativeAmountTest() throws ApplicationException {
		Account account = new AccountImpl();
		account.addAmount(new BigDecimal("3.44"));
		account.addAmount(new BigDecimal("-2.14"));
		assertTrue(account.getBalance().compareTo(new BigDecimal("1.3")) == 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void addZeroAmountTest() throws ApplicationException {
		Account account = new AccountImpl();
		account.addAmount(new BigDecimal("0"));		
	}
	
	@Test(expected = ApplicationException.class)
	public void substractAmountAboveBalanceTest() throws ApplicationException {
		Account account = new AccountImpl();
		account.addAmount(new BigDecimal("10"));	
		account.addAmount(new BigDecimal("-100"));	
	}
}
