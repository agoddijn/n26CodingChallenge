package hello;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import java.util.Date;

@RestController
public class StatisticsController {

    Statistics stats = new Statistics();

    // Post transactions endpoint
    @RequestMapping("/transactions")
    @ResponseBody
    public ResponseEntity transaction(@RequestBody Transaction input) {
        Date now = new Date();
        // 204 if too old
        if (input.getTimestamp() < (now.getTime() - 60000)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        // 201 if it is added
        stats.update(input);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    // Get statistics endpoint
    @RequestMapping("/statistics")
    public Statistic statistics() {
        return stats.getStats();
    }

    // Check the transactions every half second
    @Scheduled(fixedRate = 500)
    public void checkQueue() {
        stats.check();
    }

}
