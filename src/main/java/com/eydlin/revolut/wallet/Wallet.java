package com.eydlin.revolut.wallet;

import java.math.BigDecimal;

import com.eydlin.revolut.ApplicationException;

public interface Wallet {
	BigDecimal getBalance();
	void addAmount(BigDecimal amount) throws ApplicationException;
}
