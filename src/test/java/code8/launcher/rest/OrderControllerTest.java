package code8.launcher.rest;

import code8.launcher.logic.changer.OrderService;
import code8.launcher.logic.changer.WalletService;
import code8.launcher.model.changer.Coin;
import code8.launcher.model.changer.CoinPair;
import code8.launcher.model.changer.Order;
import code8.launcher.model.changer.OrderRequest;
import code8.launcher.model.changer.Wallet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static code8.launcher.model.changer.Coin.USD;
import static code8.launcher.model.changer.CoinPair.BTCvsUSD;
import static code8.launcher.model.changer.OrderRequest.Type.Bid;
import static code8.launcher.rest.OrderController.BASE_URL;
import static code8.launcher.rest.OrderController.NEW_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * todo: javadoc
 */
@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mvc;

    private JacksonTester<OrderRequest> json;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void makeOrderTest() throws Exception {
        OrderRequest request = makeRequest(BTCvsUSD, Bid);

        MockHttpServletResponse response = mvc.perform(
                post(BASE_URL + NEW_ORDER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.write(request).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        //assertThat(response.getContentAsString()).isEqualTo(json.write(origin).getJson());
    }

    private OrderRequest makeRequest(CoinPair pair, OrderRequest.Type type) {
        OrderRequest request = new OrderRequest();
        request.setPair(pair);
        request.setType(type);
        request.setVolume(BigDecimal.ONE);
        request.setRate(BigDecimal.TEN);
        return request;
    }
}
