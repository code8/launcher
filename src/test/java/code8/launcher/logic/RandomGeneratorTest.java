package code8.launcher.logic;

import code8.launcher.logic.RandomGenerator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * todo: javadoc
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RandomGeneratorTest {
    private static final String ORIGIN = "42";
    private static final String BOUND = "77";

    @Autowired
    private RandomGenerator randomGenerator;

    @BeforeClass
    static public void setup() {
        System.setProperty(RandomGenerator.ORIGIN_PARAMETER_KEY, ORIGIN);
        System.setProperty(RandomGenerator.BOUND_PARAMETER_KEY, BOUND);
    }

    @Test
    public void makeMultiplicationTest() {
        List<Integer> randomSequence = IntStream.range(0, 200)
                .map(i -> randomGenerator.getRandom())
                .boxed()
                .collect(Collectors.toList());

        assertThat(randomSequence).containsOnlyElementsOf(
                IntStream.range(Integer.valueOf(ORIGIN), Integer.valueOf(BOUND))
                .boxed().collect(Collectors.toList())
        );
    }
}
