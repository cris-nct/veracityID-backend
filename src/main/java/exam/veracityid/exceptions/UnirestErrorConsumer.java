package exam.veracityid.exceptions;

import kong.unirest.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class UnirestErrorConsumer<T> implements Consumer<HttpResponse<T>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(UnirestErrorConsumer.class);

    private final String message;

    public UnirestErrorConsumer(String message) {
        this.message = message;
    }

    @Override
    public void accept(HttpResponse<T> jsonNode) {
        LOGGER.error(message);
        LOGGER.error("Status: " + jsonNode.getStatus());
        jsonNode.getParsingError().ifPresent(e -> {
            LOGGER.error("Parsing Exception: ", e);
            LOGGER.error("Original body: " + e.getOriginalBody());
        });
    }
}
