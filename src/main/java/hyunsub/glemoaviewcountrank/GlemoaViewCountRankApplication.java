package hyunsub.glemoaviewcountrank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GlemoaViewCountRankApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlemoaViewCountRankApplication.class, args);
    }

}
