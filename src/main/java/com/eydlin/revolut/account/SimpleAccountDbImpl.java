package com.eydlin.revolut.account;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.eydlin.revolut.ApplicationException;
import com.eydlin.revolut.ErrorType;

/**
 * Simple account storage singleton implementation.
 * @author eydlin
 *
 */
public enum SimpleAccountDbImpl implements AccountDb {
	
	INSTANCE;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private Map<Integer, Account> accounts = new ConcurrentHashMap<>();
	
	AtomicInteger idSequence = new AtomicInteger();

	@Override
	public Integer createAcount() {
		Integer accountId = idSequence.incrementAndGet();
		accounts.put(accountId, new AccountImpl());
		log.trace("created account with id " + accountId);
		return accountId;
	}

	@Override
	public BigDecimal getBalance(Integer accountId) throws ApplicationException {
		requireNotNull(accountId, ErrorType.ACCOUNT_NOT_SPECIFIED, "account not specified");
		Account account = accounts.get(accountId);
		requireNotNull(account, ErrorType.NO_SUCH_ACCOUNT, "account with id " + accountId + " not found");
		log.trace("balance of account with id " + accountId + " is " + account.getBalance());
		return account.getBalance();
	}
	
	@Override
	public void addAmount(Integer accountId, BigDecimal amount) throws ApplicationException {
		requireNotNull(accountId, ErrorType.ACCOUNT_NOT_SPECIFIED, "account not specified");
		requireNotNull(amount, ErrorType.AMOUNT_NOT_SPECIFIED, "amount not specified");
		Account account = accounts.get(accountId);
		requireNotNull(account, ErrorType.NO_SUCH_ACCOUNT, "account with id " + accountId + " not found");
		account.addAmount(amount);
		log.trace("added " + amount + " to account with id " + accountId);
	}

	@Override
	public void transfer(Integer accountId, Integer to, BigDecimal amount) throws ApplicationException {
		requireNotNull(accountId, ErrorType.ACCOUNT_NOT_SPECIFIED, "account not specified");
		requireNotNull(to, ErrorType.TO_ACCOUNT_NOT_SPECIFIED, "receiver account not specified");
		requireNotNull(amount, ErrorType.AMOUNT_NOT_SPECIFIED, "amount not specified");
		if (amount.compareTo(BigDecimal.ZERO) < 1) {
			throw new ApplicationException(ErrorType.ILLEGAL_AMOUNT, "amount must be positive");
		}
		synchronized(accounts) {
			Account fromAcount = accounts.get(accountId);
			requireNotNull(fromAcount, ErrorType.NO_SUCH_ACCOUNT, "account with id " + accountId + " not found");
			Account toAcount = accounts.get(to);
			requireNotNull(toAcount, ErrorType.NO_SUCH_TO_ACCOUNT, "account with id " + to + " not found");
			if (toAcount == fromAcount) {
				throw new ApplicationException(ErrorType.TO_AND_FROM_ACCOUNTS_MATCH, "cannot transfer money to self");
			}
			fromAcount.addAmount(amount.negate()); // can throw an exception
			toAcount.addAmount(amount); // will never throw an exception
		}
		log.trace("transferred " + amount + " from account " + accountId + " to account " + to);
	}
	
	private void requireNotNull(Object param, ErrorType errorType, String message) throws ApplicationException {
		if (param == null) {
			throw new ApplicationException(errorType, message);
		}
	}
}
