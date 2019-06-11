package com.eydlin.revolut.resource;

import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eydlin.revolut.ApplicationException;
import com.eydlin.revolut.wallet.SimpleWalletDbImpl;
import com.eydlin.revolut.wallet.WalletDb;

@Path("wallets")
public class WalletResource {

	private WalletDb walletDb = SimpleWalletDbImpl.INSTANCE;
	
	@Path("/{walletId}/addAmount/{amount}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public void addAmount(@PathParam("walletId") Integer walletId,
			@PathParam("amount") BigDecimal amount) throws ApplicationException {
		walletDb.addAmount(walletId, amount);		
	}
	
	@Path("/{from}/transfer/{to}/{amount}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public void transfer(@PathParam("from") Integer from,
			@PathParam("to") Integer to,
			@PathParam("amount") BigDecimal amount) throws ApplicationException {
		walletDb.transfer(from, to, amount);
	}
	
	@Path("/create")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Integer createWallet() {
		return walletDb.createWallet();
	}
	
	@Path("/{walletId}/balance")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public BigDecimal getBalance(@PathParam("walletId") Integer walletId) throws ApplicationException {
		return walletDb.getBalance(walletId);
	}
	
}
