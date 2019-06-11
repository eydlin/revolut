package com.eydlin.revolut.account;

import java.math.BigDecimal;

import com.eydlin.revolut.ApplicationException;

public interface AccountDb {
	Integer createAcount();
	BigDecimal getBalance(Integer accountId) throws ApplicationException;
	void transfer(Integer from, Integer to, BigDecimal amount) throws ApplicationException;
	void addAmount(Integer accountId, BigDecimal amount) throws ApplicationException;
}
