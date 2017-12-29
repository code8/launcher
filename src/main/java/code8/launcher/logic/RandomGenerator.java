package code8.launcher.logic;

/**
 * todo: javadoc
 */
public interface RandomGenerator {
    String ORIGIN_PARAMETER_KEY = "RANDOM_GENERATOR_ORIGIN";
    String BOUND_PARAMETER_KEY = "RANDOM_GENERATOR_BOUND";
    int getRandom();
}
