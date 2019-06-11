package com.eydlin.revolut.wallet;

import java.math.BigDecimal;

import com.eydlin.revolut.ApplicationException;

public interface WalletDb {
	Integer createWallet();
	BigDecimal getBalance(Integer walletId) throws ApplicationException;
	void transfer(Integer from, Integer to, BigDecimal amount) throws ApplicationException;
	void addAmount(Integer walletId, BigDecimal amount) throws ApplicationException;
}
