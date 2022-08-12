package hello.mailService.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
@Component
public class Bucket4jService {

//    private static final int TOKEN_REFILL_INTERVAL_SECONDS = 10;
//    private static final int TOKEN_REFILL_COUNT_AT_ONCE = 3;
    private static final int MAX_BANDWIDTH = 5;
    private static final int TOKEN_REFILL_DURATION_MINUTES = 1;

//    // intervally
//    public Refill getIntervalRefill() {
//        return Refill.intervally(TOKEN_REFILL_COUNT_AT_ONCE, Duration.ofSeconds(TOKEN_REFILL_INTERVAL_SECONDS));
//    }

    public static Bandwidth getSimpleBandwidth() {
        return Bandwidth.simple(MAX_BANDWIDTH, Duration.ofMinutes(TOKEN_REFILL_DURATION_MINUTES));
    }

    // 단일 Bandwidth
    public static Bucket generateSimpleBucket() {
        return Bucket.builder().addLimit(getSimpleBandwidth()).build();
    }

}
