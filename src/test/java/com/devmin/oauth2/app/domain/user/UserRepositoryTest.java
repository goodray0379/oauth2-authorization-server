package com.devmin.oauth2.app.domain.user;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @After
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void 회원저장_불러오기() {
        //given
        String username = "devmin";
        String password = "123123";
        String encodePassword = passwordEncoder.encode(password);
        Role role = Role.USER;
        LocalDateTime now = LocalDateTime.of(2021, 10, 28, 0, 0, 0);

        userRepository.save(User.builder()
                .username(username)
                .password(encodePassword)
                .build());

        //when
        List<User> userList = userRepository.findAll();

        //then
        User user = userList.get(0);
        System.out.println(">>>>>>> username=" + user.getUsername() + ", encodePassword=" + user.getPassword() + ", role=" + user.getRole());
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(passwordEncoder.matches(password, encodePassword)).isTrue();
        assertThat(user.getRole()).isEqualTo(role);
        System.out.println(">>>>>>> createDate=" + user.getCreatedDate() + ", modifiedDate=" + user.getModifiedDate());
        assertThat(user.getCreatedDate()).isAfter(now);
        assertThat(user.getModifiedDate()).isAfter(now);
    }
}
