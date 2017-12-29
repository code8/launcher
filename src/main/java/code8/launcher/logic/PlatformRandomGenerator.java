package code8.launcher.logic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * todo: javadoc
 */

@Service
public class PlatformRandomGenerator implements RandomGenerator {
    private final int origin;
    private final int bound;

    public PlatformRandomGenerator(@Value("#{environment[T(code8.launcher.logic.RandomGenerator).ORIGIN_PARAMETER_KEY] ?:1}") int origin,
                                   @Value("#{environment[T(code8.launcher.logic.RandomGenerator).BOUND_PARAMETER_KEY] ?:10}") int bound) {
        this.origin = origin;
        this.bound = bound;
    }

    @Override
    public int getRandom() {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }
}
