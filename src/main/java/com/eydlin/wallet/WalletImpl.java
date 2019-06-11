package com.eydlin.wallet;

import java.math.BigDecimal;

public class WalletImpl implements Wallet {

	private BigDecimal balance = BigDecimal.ZERO;
	
	@Override
	public BigDecimal getBalance() {
		return balance;
	}

	@Override
	synchronized public void addAmount(BigDecimal amount) throws ApplicationException {
		BigDecimal newBalance = balance.add(amount);
		if (newBalance.compareTo(BigDecimal.ZERO) == -1) {
			throw new ApplicationException(ErrorType.BALANCE_INSUFFICIENT, "balance insufficient");
		} else {
			balance = balance.add(amount);
		}
	}

}
