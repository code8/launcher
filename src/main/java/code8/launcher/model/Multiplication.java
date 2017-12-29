package code8.launcher.model;

/**
 * todo: javadoc
 */
public class Multiplication {
    final int left,right;

    protected Multiplication() {
        this(0,0);
    }

    public Multiplication(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }
}
