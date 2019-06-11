package com.eydlin;

import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.eydlin.wallet.ApplicationException;
import com.eydlin.wallet.SimpleWalletDbImpl;
import com.eydlin.wallet.WalletDb;

@Path("wallets")
public class WalletResource {

	private WalletDb walletDb = SimpleWalletDbImpl.INSTANCE;
	
	@Path("/{walletId}/addAmount/{amount}")
	@PUT
	public void addAmount(@PathParam("walletId") Integer walletId,
			@PathParam("amount") BigDecimal amount) throws ApplicationException {
		walletDb.addAmount(walletId, amount);		
	}
	
	@Path("/{from}/transfer/{to}/{amount}")
	@PUT
	public void transfer(@PathParam("from") Integer from,
			@PathParam("to") Integer to,
			@PathParam("amount") BigDecimal amount) throws ApplicationException {
		walletDb.transfer(from, to, amount);
	}
	
	@Path("/create")
	@POST
	public Integer createWallet() {
		return walletDb.createWallet();
	}
	
	@Path("/{walletId}/balance")
	@GET
	public BigDecimal getBalance(@PathParam("walletId") Integer walletId) throws ApplicationException {
		return walletDb.getBalance(walletId);
	}
	
}
