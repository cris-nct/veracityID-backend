package exam.veracityid;

import kong.unirest.Unirest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VeracityidApplication {

    public static void main(String[] args) {
        final String googleApiKey = System.getProperty("google.api.key");
        if (Strings.isBlank(googleApiKey)){
            System.err.println("You need to start the server with -Dgoogle.api.key=<your_key_for_google_places_api>");
            return;
        }
        Unirest.config().addShutdownHook(true);
        SpringApplication.run(VeracityidApplication.class, args);
    }

}
