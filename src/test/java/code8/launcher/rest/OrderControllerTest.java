package code8.launcher.rest;

import code8.launcher.logic.changer.OrderService;
import code8.launcher.model.changer.BidOrder;
import code8.launcher.model.changer.CoinPair;
import code8.launcher.model.changer.Order;
import code8.launcher.model.changer.OrderRequest;
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
import java.util.concurrent.ThreadLocalRandom;

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

    @MockBean
    private OrderService orderService;

    private JacksonTester<OrderRequest> requestJson;
    private JacksonTester<BidOrder> orderJson;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void makeOrderTest() throws Exception {
        OrderRequest request = makeRequest(BTCvsUSD, Bid);

        Order order = Order.fromRequest(request);
        order.setId(42);
        given(orderService.makeOrder(request)).willReturn(order);

        MockHttpServletResponse response = mvc.perform(
                post(BASE_URL + NEW_ORDER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson.write(request).getJson()))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(orderJson.parseObject(response.getContentAsString())).isEqualTo(order);
    }

    private OrderRequest makeRequest(CoinPair pair, OrderRequest.Type type) {
        OrderRequest request = new OrderRequest(ThreadLocalRandom.current().nextInt());
        request.setPair(pair);
        request.setType(type);
        request.setVolume(BigDecimal.ONE);
        request.setRate(BigDecimal.TEN);
        return request;
    }
}
