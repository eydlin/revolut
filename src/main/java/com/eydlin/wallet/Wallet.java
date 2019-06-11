package com.eydlin.wallet;

import java.math.BigDecimal;

public interface Wallet {
	BigDecimal getBalance();
	void addAmount(BigDecimal amount) throws ApplicationException;
}
