package code8.launcher.logic;

import code8.launcher.logic.changer.UserDao;
import code8.launcher.model.changer.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

    @Autowired
    private UserDao dao;

    @Test
    public void creationTest() {
        assertNotNull(dao);
    }

    @Test
    public void findByEmailTest() {
        String email = "user@gmail.com";
        User user = dao.findByEmail(email);
        assertNotNull(user);
        assertEquals(user.getEmail(), email);
    }
}
