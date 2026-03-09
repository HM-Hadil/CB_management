package pfe.cb_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CbManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbManagementApplication.class, args);
    }

}
