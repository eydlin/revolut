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

public class WalletResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        // start the server
        server = Main.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(Main.BASE_URI);
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }
    
    @Test
    public void createWalletAndGetBalanceTest() {
        Response createResp = target.path("wallets/create").request().post(null);
        Integer walletId = createResp.readEntity(Integer.class);
        Response balanceResp = target.path("wallets/" + walletId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(BigDecimal.ZERO.compareTo(balance) == 0);
    }
    
    @Test
    public void nonExistentWalletBalanceTest() {
        Response createResp = target.path("wallets/create").request().post(null);
        Integer walletId = createResp.readEntity(Integer.class);
        Response balanceResp = target.path("wallets/" + (walletId + 1) + "/balance").request().get();
        assertTrue(balanceResp.getStatus() == NOT_FOUND_404.getStatusCode());
    }
    
    @Test
    public void createWalletAndAddAmountTest() {
        Response createResp = target.path("wallets/create").request().post(null);
        Integer walletId = createResp.readEntity(Integer.class);
        target.path("wallets/" + walletId + "/addAmount/" + new BigDecimal("3.50")).request().post(null);
        Response balanceResp = target.path("wallets/" + walletId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("3.50").compareTo(balance) == 0);
    }
    
    @Test
    public void createWalletAndAddZeroAmountTest() {
        Response createResp = target.path("wallets/create").request().post(null);
        Integer walletId = createResp.readEntity(Integer.class);
        target.path("wallets/" + walletId + "/addAmount/" + new BigDecimal("3.50")).request().post(null);
        Response addBalanceResp = 
        		target.path("wallets/" + walletId + "/addAmount/" + new BigDecimal("0")).request().post(null);
        assertTrue(addBalanceResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balance has not been affected
        Response balanceResp = target.path("wallets/" + walletId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("3.50").compareTo(balance) == 0);
    }
    
    @Test
    public void createWalletAndSubstractAboveBalanceTest() {
        Response createResp = target.path("wallets/create").request().post(null);
        Integer walletId = createResp.readEntity(Integer.class);
        target.path("wallets/" + walletId + "/addAmount/" + new BigDecimal("3.50")).request().post(null);
        Response substractResp = 
        		target.path("wallets/" + walletId + "/addAmount/" + new BigDecimal("-100")).request().post(null);
        assertTrue(substractResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balance has not been affected
        Response balanceResp = target.path("wallets/" + walletId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("3.50").compareTo(balance) == 0);
    }
    
    @Test
    public void transferAmountTest() {
        Response createFromResp = target.path("wallets/create").request().post(null);
        Integer from = createFromResp.readEntity(Integer.class);	
        target.path("wallets/" + from + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response createToResp = target.path("wallets/create").request().post(null);
        Integer to = createToResp.readEntity(Integer.class);
        target.path("wallets/" + to + "/addAmount/" + new BigDecimal("20")).request().post(null);	
        target.path("wallets/" + from + "/transfer/" + to + "/" + new BigDecimal("5")).request().post(null);
        Response balanceResp = target.path("wallets/" + from + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("25").compareTo(balance) == 0);
        Response balanceToResp = target.path("wallets/" + to + "/balance").request().get();
        BigDecimal balanceTo = balanceToResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("25").compareTo(balanceTo) == 0);
    }
    
    @Test
    public void transferAmountAboveBalanceTest() {
        Response createFromResp = target.path("wallets/create").request().post(null);
        Integer from = createFromResp.readEntity(Integer.class);	
        target.path("wallets/" + from + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response createToResp = target.path("wallets/create").request().post(null);
        Integer to = createToResp.readEntity(Integer.class);
        target.path("wallets/" + to + "/addAmount/" + new BigDecimal("20")).request().post(null);	
        Response transferResp = 
        		target.path("wallets/" + from + "/transfer/" + to + "/" + new BigDecimal("55")).request().post(null);
        assertTrue(transferResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balances have not been affected
        Response balanceResp = target.path("wallets/" + from + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("30").compareTo(balance) == 0);
        Response balanceToResp = target.path("wallets/" + to + "/balance").request().get();
        BigDecimal balanceTo = balanceToResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("20").compareTo(balanceTo) == 0);
    }
    
    @Test
    public void transferZeroAmountTest() {
        Response createFromResp = target.path("wallets/create").request().post(null);
        Integer from = createFromResp.readEntity(Integer.class);	
        target.path("wallets/" + from + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response createToResp = target.path("wallets/create").request().post(null);
        Integer to = createToResp.readEntity(Integer.class);
        target.path("wallets/" + to + "/addAmount/" + new BigDecimal("20")).request().post(null);	
        Response transferResp = 
        		target.path("wallets/" + from + "/transfer/" + to + "/" + new BigDecimal("0")).request().post(null);
        assertTrue(transferResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balances have not been affected
        Response balanceResp = target.path("wallets/" + from + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("30").compareTo(balance) == 0);
        Response balanceToResp = target.path("wallets/" + to + "/balance").request().get();
        BigDecimal balanceTo = balanceToResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("20").compareTo(balanceTo) == 0);
    }
    
    @Test
    public void transferNegativeAmountTest() {
        Response createFromResp = target.path("wallets/create").request().post(null);
        Integer from = createFromResp.readEntity(Integer.class);	
        target.path("wallets/" + from + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response createToResp = target.path("wallets/create").request().post(null);
        Integer to = createToResp.readEntity(Integer.class);
        target.path("wallets/" + to + "/addAmount/" + new BigDecimal("20")).request().post(null);	
        Response transferResp = 
        		target.path("wallets/" + from + "/transfer/" + to + "/" + new BigDecimal("-273")).request().post(null);
        assertTrue(transferResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balances have not been affected
        Response balanceResp = target.path("wallets/" + from + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("30").compareTo(balance) == 0);
        Response balanceToResp = target.path("wallets/" + to + "/balance").request().get();
        BigDecimal balanceTo = balanceToResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("20").compareTo(balanceTo) == 0);
    }
    
    @Test
    public void transferToSelfTest() {
        Response createFromResp = target.path("wallets/create").request().post(null);
        Integer walletId = createFromResp.readEntity(Integer.class);	
        target.path("wallets/" + walletId + "/addAmount/" + new BigDecimal("30")).request().post(null);
        Response transferResp = 
        		target.path("wallets/" + walletId + "/transfer/" + walletId + "/" + new BigDecimal("10")).request().post(null);
        assertTrue(transferResp.getStatus() == BAD_REQUEST_400.getStatusCode());
        // check if balance has not been affected
        Response balanceResp = target.path("wallets/" + walletId + "/balance").request().get();
        BigDecimal balance = balanceResp.readEntity(BigDecimal.class);
        assertTrue(new BigDecimal("30").compareTo(balance) == 0);
    }

}
