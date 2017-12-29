package code8.launcher.logic;

import code8.launcher.model.Multiplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * todo: javadoc
 */

@Service
public class RandomMultiplicationService implements MultiplicationService {

    private final RandomGenerator randomGenerator;

    @Autowired
    public RandomMultiplicationService(RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    @Override
    public Multiplication makeMultiplication() {
        return new Multiplication(randomGenerator.getRandom(), randomGenerator.getRandom());
    }
}
