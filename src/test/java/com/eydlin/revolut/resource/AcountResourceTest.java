package com.eydlin.revolut.resource;

import static org.glassfish.grizzly.http.util.HttpStatus.BAD_REQUEST_400;
import static org.glassfish.grizzly.http.util.HttpStatus.NOT_FOUND_404;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eydlin.revolut.Main;

public class AcountResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }
    
    @Test
    public void createAcountAndGetBalanceTest() {
        Response createResp = target.path("accounts/create").request().post(null);
        Integer accountId = createResp.readEntity(Integer.class);
        Response balanceResp = target.path("accounts/" + accountId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(BigDecimal.ZERO.compareTo(balance) == 0);
    }
    
    @Test
    public void nonExistentAcountBalanceTest() {
        Response createResp = target.path("accounts/create").request().post(null);
        Integer accountId = createResp.readEntity(Integer.class);
        Response balanceResp = target.path("accounts/" + (accountId + 1) + "/balance").request().get();
        assertTrue(balanceResp.getStatus() == NOT_FOUND_404.getStatusCode());
    }
    
    @Test
    public void createAcountAndAddAmountTest() {
        Response createResp = target.path("accounts/create").request().post(null);
        Integer accountId = createResp.readEntity(Integer.class);
        target.path("accounts/" + accountId + "/addAmount/" + new BigDecimal("3.50")).request().post(null);
        Response balanceResp = target.path("accounts/" + accountId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("3.50").compareTo(balance) == 0);
    }
    
    @Test
    public void createAcountAndAddZeroAmountTest() {
        Response createResp = target.path("accounts/create").request().post(null);
        Integer accountId = createResp.readEntity(Integer.class);
        target.path("accounts/" + accountId + "/addAmount/" + new BigDecimal("3.50")).request().post(null);
        Response addBalanceResp = 
        		target.path("accounts/" + accountId + "/addAmount/" + new BigDecimal("0")).request().post(null);
        assertTrue(addBalanceResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balance has not been affected
        Response balanceResp = target.path("accounts/" + accountId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("3.50").compareTo(balance) == 0);
    }
    
    @Test
    public void createAcountAndSubstractAboveBalanceTest() {
        Response createResp = target.path("accounts/create").request().post(null);
        Integer accountId = createResp.readEntity(Integer.class);
        target.path("accounts/" + accountId + "/addAmount/" + new BigDecimal("3.50")).request().post(null);
        Response substractResp = 
        		target.path("accounts/" + accountId + "/addAmount/" + new BigDecimal("-100")).request().post(null);
        assertTrue(substractResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balance has not been affected
        Response balanceResp = target.path("accounts/" + accountId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("3.50").compareTo(balance) == 0);
    }
    
    @Test
    public void transferAmountTest() {
        Response createFromResp = target.path("accounts/create").request().post(null);
        Integer from = createFromResp.readEntity(Integer.class);	
        target.path("accounts/" + from + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response createToResp = target.path("accounts/create").request().post(null);
        Integer to = createToResp.readEntity(Integer.class);
        target.path("accounts/" + to + "/addAmount/" + new BigDecimal("20")).request().post(null);	
        target.path("accounts/" + from + "/transfer/" + to + "/" + new BigDecimal("5")).request().post(null);
        Response balanceResp = target.path("accounts/" + from + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("25").compareTo(balance) == 0);
        Response balanceToResp = target.path("accounts/" + to + "/balance").request().get();
        BigDecimal balanceTo = balanceToResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("25").compareTo(balanceTo) == 0);
    }
    
    @Test
    public void transferAmountAboveBalanceTest() {
        Response createFromResp = target.path("accounts/create").request().post(null);
        Integer from = createFromResp.readEntity(Integer.class);	
        target.path("accounts/" + from + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response createToResp = target.path("accounts/create").request().post(null);
        Integer to = createToResp.readEntity(Integer.class);
        target.path("accounts/" + to + "/addAmount/" + new BigDecimal("20")).request().post(null);	
        Response transferResp = 
        		target.path("accounts/" + from + "/transfer/" + to + "/" + new BigDecimal("55")).request().post(null);
        assertTrue(transferResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balances have not been affected
        Response balanceResp = target.path("accounts/" + from + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("30").compareTo(balance) == 0);
        Response balanceToResp = target.path("accounts/" + to + "/balance").request().get();
        BigDecimal balanceTo = balanceToResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("20").compareTo(balanceTo) == 0);
    }
    
    @Test
    public void transferZeroAmountTest() {
        Response createFromResp = target.path("accounts/create").request().post(null);
        Integer from = createFromResp.readEntity(Integer.class);	
        target.path("accounts/" + from + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response createToResp = target.path("accounts/create").request().post(null);
        Integer to = createToResp.readEntity(Integer.class);
        target.path("accounts/" + to + "/addAmount/" + new BigDecimal("20")).request().post(null);	
        Response transferResp = 
        		target.path("accounts/" + from + "/transfer/" + to + "/" + new BigDecimal("0")).request().post(null);
        assertTrue(transferResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balances have not been affected
        Response balanceResp = target.path("accounts/" + from + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("30").compareTo(balance) == 0);
        Response balanceToResp = target.path("accounts/" + to + "/balance").request().get();
        BigDecimal balanceTo = balanceToResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("20").compareTo(balanceTo) == 0);
    }
    
    @Test
    public void transferNegativeAmountTest() {
        Response createFromResp = target.path("accounts/create").request().post(null);
        Integer from = createFromResp.readEntity(Integer.class);	
        target.path("accounts/" + from + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response createToResp = target.path("accounts/create").request().post(null);
        Integer to = createToResp.readEntity(Integer.class);
        target.path("accounts/" + to + "/addAmount/" + new BigDecimal("20")).request().post(null);	
        Response transferResp = 
        		target.path("accounts/" + from + "/transfer/" + to + "/" + new BigDecimal("-273")).request().post(null);
        assertTrue(transferResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balances have not been affected
        Response balanceResp = target.path("accounts/" + from + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("30").compareTo(balance) == 0);
        Response balanceToResp = target.path("accounts/" + to + "/balance").request().get();
        BigDecimal balanceTo = balanceToResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("20").compareTo(balanceTo) == 0);
    }
    
    @Test
    public void transferToSelfTest() {
        Response createFromResp = target.path("accounts/create").request().post(null);
        Integer accountId = createFromResp.readEntity(Integer.class);	
        target.path("accounts/" + accountId + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response transferResp = 
        		target.path("accounts/" + accountId + "/transfer/" + accountId + "/" + new BigDecimal("10")).request().post(null);
        assertTrue(transferResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balance has not been affected
        Response balanceResp = target.path("accounts/" + accountId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("30").compareTo(balance) == 0);
    }

}
