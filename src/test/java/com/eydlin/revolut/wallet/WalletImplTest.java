package com.eydlin.revolut.wallet;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import com.eydlin.revolut.ApplicationException;
import com.eydlin.revolut.wallet.Wallet;
import com.eydlin.revolut.wallet.WalletImpl;

public class WalletImplTest {
	
	@Test
	public void createWalletTest() {
		Wallet wallet = new WalletImpl();
		assertTrue(wallet.getBalance().equals(BigDecimal.ZERO));
	}
	
	@Test
	public void addPositiveAmountTest() throws ApplicationException {
		Wallet wallet = new WalletImpl();
		wallet.addAmount(new BigDecimal("3.44"));
		wallet.addAmount(new BigDecimal("2.56"));
		assertTrue(wallet.getBalance().compareTo(new BigDecimal("6")) == 0);
	}
	
	@Test
	public void addNegativeAmountTest() throws ApplicationException {
		Wallet wallet = new WalletImpl();
		wallet.addAmount(new BigDecimal("3.44"));
		wallet.addAmount(new BigDecimal("-2.14"));
		assertTrue(wallet.getBalance().compareTo(new BigDecimal("1.3")) == 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void addZeroAmountTest() throws ApplicationException {
		Wallet wallet = new WalletImpl();
		wallet.addAmount(new BigDecimal("0"));		
	}
	
	@Test(expected = ApplicationException.class)
	public void substractAmountAboveBalanceTest() throws ApplicationException {
		Wallet wallet = new WalletImpl();
		wallet.addAmount(new BigDecimal("10"));	
		wallet.addAmount(new BigDecimal("-100"));	
	}
}
