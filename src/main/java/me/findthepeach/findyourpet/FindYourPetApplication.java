package me.findthepeach.findyourpet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class FindYourPetApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindYourPetApplication.class, args);
    }

}
