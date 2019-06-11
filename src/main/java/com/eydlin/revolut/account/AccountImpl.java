package com.eydlin.revolut.account;

import java.math.BigDecimal;

import com.eydlin.revolut.ApplicationException;
import com.eydlin.revolut.ErrorType;

public class AccountImpl implements Account {

	private BigDecimal balance = BigDecimal.ZERO;
	
	@Override
	public BigDecimal getBalance() {
		return balance;
	}

	@Override
	synchronized public void addAmount(BigDecimal amount) throws ApplicationException {
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			throw new ApplicationException(ErrorType.ILLEGAL_AMOUNT, "cannot add zero amount");
		}
		BigDecimal newBalance = balance.add(amount);
		if (newBalance.compareTo(BigDecimal.ZERO) == -1) {
			throw new ApplicationException(ErrorType.BALANCE_INSUFFICIENT, "balance insufficient");
		} else {
			balance = balance.add(amount);
		}
	}

}
