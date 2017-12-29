package code8.launcher.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * todo: javadoc
 */
public class ResultAttempt {
    private final Multiplication multiplication;
    private final int attempt;

    protected ResultAttempt() {
        this(null, 0);
    }


    public ResultAttempt(Multiplication multiplication, int attempt) {
        this.multiplication = multiplication;
        this.attempt = attempt;
    }

    public boolean check() {
        return multiplication.getLeft() * multiplication.getRight() == attempt;
    }

    public Multiplication getMultiplication() {
        return multiplication;
    }

    public int getAttempt() {
        return attempt;
    }
}
