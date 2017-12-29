package code8.launcher.rest;

import code8.launcher.logic.changer.OrderService;
import code8.launcher.model.changer.Order;
import code8.launcher.model.changer.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo: javadoc
 */
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {
    public static final String BASE_URL = "/orders";
    public static final String NEW_ORDER = "/new";

    @Autowired
    private OrderService orderService;

    @PostMapping(OrderController.NEW_ORDER)
    public ResponseEntity<Order> makeOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.makeOrder(request));
    }
}
