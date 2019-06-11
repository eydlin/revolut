package com.eydlin.wallet;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * Simple wallet storage singleton implementation.
 * @author eydlin
 *
 */
public enum SimpleWalletDbImpl implements WalletDb {
	
	INSTANCE;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private Map<Integer, Wallet> wallets = new ConcurrentHashMap<>();
	
	AtomicInteger idSequence = new AtomicInteger();

	@Override
	public Integer createWallet() {
		Integer walletId = idSequence.incrementAndGet();
		wallets.put(walletId, new WalletImpl());
		log.trace("created wallet with id " + walletId);
		return walletId;
	}

	@Override
	public BigDecimal getBalance(Integer walletId) throws ApplicationException {
		requireNotNull(walletId, ErrorType.WALLET_NOT_SPECIFIED, "wallet not specified");
		Wallet wallet = wallets.get(walletId);
		requireNotNull(wallet, ErrorType.NO_SUCH_WALLET, "wallet with id " + walletId + " not found");
		log.trace("balance of wallet with id " + walletId + " is " + wallet.getBalance());
		return wallet.getBalance();
	}
	
	@Override
	public void addAmount(Integer walletId, BigDecimal amount) throws ApplicationException {
		requireNotNull(walletId, ErrorType.WALLET_NOT_SPECIFIED, "wallet not specified");
		requireNotNull(amount, ErrorType.AMOUNT_NOT_SPECIFIED, "amount not specified");
		Wallet wallet = wallets.get(walletId);
		requireNotNull(wallet, ErrorType.NO_SUCH_WALLET, "wallet with id " + walletId + " not found");
		wallet.addAmount(amount);
		log.trace("added " + amount + " to wallet with id " + walletId);
	}

	@Override
	public void transfer(Integer walletId, Integer to, BigDecimal amount) throws ApplicationException {
		requireNotNull(walletId, ErrorType.WALLET_NOT_SPECIFIED, "wallet not specified");
		requireNotNull(to, ErrorType.TO_WALLET_NOT_SPECIFIED, "receiver wallet not specified");
		requireNotNull(amount, ErrorType.AMOUNT_NOT_SPECIFIED, "amount not specified");
		if (amount.compareTo(BigDecimal.ZERO) < 1) {
			throw new ApplicationException(ErrorType.ILLEGAL_AMOUNT, "amount must be positive");
		}
		synchronized(wallets) {
			Wallet fromWallet = wallets.get(walletId);
			requireNotNull(fromWallet, ErrorType.NO_SUCH_WALLET, "wallet with id " + walletId + " not found");
			Wallet toWallet = wallets.get(to);
			requireNotNull(toWallet, ErrorType.NO_SUCH_TO_WALLET, "wallet with id " + to + " not found");
			if (toWallet == fromWallet) {
				throw new ApplicationException(ErrorType.TO_AND_FROM_WALLETS_MATCH, "cannot transfer money to self");
			}
			fromWallet.addAmount(amount.negate()); // can throw an exception
			toWallet.addAmount(amount); // will never throw an exception
		}
		log.trace("transferred " + amount + " from wallet " + walletId + " to wallet " + to);
	}
	
	private void requireNotNull(Object param, ErrorType errorType, String message) throws ApplicationException {
		if (param == null) {
			throw new ApplicationException(errorType, message);
		}
	}
}
