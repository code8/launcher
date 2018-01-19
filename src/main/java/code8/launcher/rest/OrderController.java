package code8.launcher.rest;

import code8.launcher.logic.OrderService;
import code8.launcher.model.Order;
import code8.launcher.model.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * todo: javadoc
 */
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {
    public static final String BASE_URL = "/orders";
    public static final String NEW_ORDER = "/new";
    public static final String CANCEL_ORDER = "/cancel";

    @Autowired
    private OrderService orderService;

    @PostMapping(OrderController.NEW_ORDER)
    public ResponseEntity<Order> makeOrder(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.makeOrder(request));
    }

    @GetMapping
    public ResponseEntity<Order> getOrder(@RequestParam long orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PutMapping(OrderController.CANCEL_ORDER)
    public void cancelOrder(@RequestParam long orderId) {
        orderService.cancelOrder(orderId);
    }
}
