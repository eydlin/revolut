package com.eydlin.revolut.account;

import java.math.BigDecimal;

import com.eydlin.revolut.ApplicationException;

public interface Account {
	BigDecimal getBalance();
	void addAmount(BigDecimal amount) throws ApplicationException;
}
