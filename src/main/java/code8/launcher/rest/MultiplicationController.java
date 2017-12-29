package code8.launcher.rest;

import code8.launcher.logic.MultiplicationService;
import code8.launcher.model.Multiplication;
import code8.launcher.model.ResultAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * todo: javadoc
 */
@RestController
@RequestMapping(MultiplicationController.BASE_URL)
public class MultiplicationController {
    public static final String BASE_URL = "/multiplications";
    public static final String GET_RANDOM = "/random";
    public static final String CHECK_RESULT = "/check";

    private final MultiplicationService service;

    @Autowired
    public MultiplicationController(MultiplicationService service) {
        this.service = service;
    }

    @GetMapping(MultiplicationController.GET_RANDOM)
    public Multiplication getRandomMultiplication() {
        return service.makeMultiplication();
    }

    @PostMapping(MultiplicationController.CHECK_RESULT)
    public ResponseEntity<Boolean> getRandomMultiplication(@RequestBody ResultAttempt resultAttempt) {
        return ResponseEntity.ok(resultAttempt.check());
    }
}
