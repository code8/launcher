package code8.launcher.logic.changer;

import org.jooq.DSLContext;
import org.jooq.Record;

abstract public class AbstractDao<R extends Record> {

    protected final DSLContext dslContext;

    public AbstractDao(DSLContext dslContext) {

        this.dslContext = dslContext;
    }
}
