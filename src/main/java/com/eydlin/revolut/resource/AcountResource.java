package com.eydlin.revolut.resource;

import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.eydlin.revolut.ApplicationException;
import com.eydlin.revolut.account.AccountDb;
import com.eydlin.revolut.account.SimpleAccountDbImpl;

@Path("accounts")
public class AcountResource {

	private AccountDb accountDb = SimpleAccountDbImpl.INSTANCE;
	
	@Path("/{accountId}/addAmount/{amount}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public void addAmount(@PathParam("accountId") Integer accountId,
			@PathParam("amount") BigDecimal amount) throws ApplicationException {
		accountDb.addAmount(accountId, amount);		
	}
	
	@Path("/{from}/transfer/{to}/{amount}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public void transfer(@PathParam("from") Integer from,
			@PathParam("to") Integer to,
			@PathParam("amount") BigDecimal amount) throws ApplicationException {
		accountDb.transfer(from, to, amount);
	}
	
	@Path("/create")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Integer createAcount() {
		return accountDb.createAcount();
	}
	
	@Path("/{accountId}/balance")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public BigDecimal getBalance(@PathParam("accountId") Integer accountId) throws ApplicationException {
		return accountDb.getBalance(accountId);
	}
	
}
