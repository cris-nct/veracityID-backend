package exam.veracityid;

import kong.unirest.Unirest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VeracityidApplication {

    public static void main(String[] args) {
        Unirest.config().addShutdownHook(true);
        SpringApplication.run(VeracityidApplication.class, args);
    }

}
