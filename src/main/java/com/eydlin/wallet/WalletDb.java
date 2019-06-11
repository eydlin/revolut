package com.eydlin.wallet;

import java.math.BigDecimal;

public interface WalletDb {
	Integer createWallet();
	BigDecimal getBalance(Integer walletId) throws ApplicationException;
	void transfer(Integer from, Integer to, BigDecimal amount) throws ApplicationException;
	void addAmount(Integer walletId, BigDecimal amount) throws ApplicationException;
}
