package code8.launcher.persistence;

import code8.launcher.model.User;
import code8.persistence.public_.tables.records.UserRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static code8.persistence.public_.tables.User.USER;

@Service
public class UserDao extends AbstractDao<UserRecord> {

    private static final RecordMapper<Record, User> MAPPER = record -> {
        User user = new User();
        user.setId(record.get(USER.ID, Long.class));
        user.setEmail(record.get(USER.EMAIL, String.class));
        user.setName(record.get(USER.NAME, String.class));
        return user;
    };

    @Autowired
    public UserDao(DSLContext dslContext) {
        super(dslContext);
    }

    public User findByEmail(String email) {
        return dslContext.select()
                .from(USER)
                .where(USER.EMAIL.eq(email))
                .fetchOne()
                .map(MAPPER);
    }

}
