package code8.launcher.logic;

import code8.launcher.model.Multiplication;
import code8.launcher.logic.RandomGenerator;
import code8.launcher.logic.RandomMultiplicationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * todo: javadoc
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class RandomMultiplicationServiceTest {
    @MockBean
    private RandomGenerator randomGenerator;

    @Autowired
    private RandomMultiplicationService randomMultiplicationService;

    @Test
    public void makeMultiplicationTest() {
        int first = 50, second = 30;

        given(randomGenerator.getRandom()).willReturn(first,second);

        Multiplication multiplication = randomMultiplicationService.makeMultiplication();

        assertThat(multiplication.getLeft()).isEqualTo(first);
        assertThat(multiplication.getRight()).isEqualTo(second);
    }
}
