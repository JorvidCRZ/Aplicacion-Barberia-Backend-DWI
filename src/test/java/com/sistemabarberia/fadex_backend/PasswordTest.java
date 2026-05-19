package com.sistemabarberia.fadex_backend;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void generarHash() {
        System.out.println("juan123:   " + passwordEncoder.encode("juan123"));
        System.out.println("luis123:   " + passwordEncoder.encode("luis123"));
        System.out.println("carlos123: " + passwordEncoder.encode("carlos123"));
        System.out.println("ana123:    " + passwordEncoder.encode("ana123"));
    }
}
