/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hello;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTestsTimedHard {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    public void shouldGiveCorrectStatsFor1000Transactions() throws Exception {

        Date now = new Date();
        Random rand = new Random();

        int num = 1000;
        BigDecimal sum1 = new BigDecimal(0), sum2;
        BigDecimal avg1, avg2;
        float min1 = Float.POSITIVE_INFINITY, min2;
        float max1 = Float.NEGATIVE_INFINITY, max2;

        for(int i = 0; i < num / 2; i++) {
            float amount = rand.nextFloat() * 100;
            if (amount < min1) min1 = amount;
            if (amount > max1) max1 = amount;
            sum1 = sum1.add(new BigDecimal(amount));
            Transaction t = new Transaction(amount, now.getTime() - 50000);

            this.mockMvc.perform(post("/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(t)));
        }

        avg1 = sum1.divide(new BigDecimal(num / 2), RoundingMode.CEILING);

        min2 = min1;
        max2 = max1;
        sum2 = new BigDecimal(sum1.floatValue());

        for(int i = num / 2; i < num; i++) {
            float amount = rand.nextFloat() * 100;
            if (amount < min2) min2 = amount;
            if (amount > max2) max2 = amount;
            sum2 = sum2.add(new BigDecimal(amount));
            Transaction t = new Transaction(amount, now.getTime() - 55000);

            this.mockMvc.perform(post("/transactions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(t)));
        }

        avg2 = sum2.divide(new BigDecimal(num));

        Statistic expectedStat = new Statistic(sum2.floatValue(), avg2.floatValue(), max2, min2, num);

        this.mockMvc.perform(get("/statistics"))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedStat)));

        Thread.sleep(6000);

        Statistic expectedStat2 = new Statistic(sum1.floatValue(), avg1.floatValue(), max1, min1, num / 2);

        this.mockMvc.perform(get("/statistics"))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedStat2)));

        Thread.sleep(5000);

        Statistic expectedStat3 = new Statistic(0, Float.NaN, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, 0);

        this.mockMvc.perform(get("/statistics"))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedStat3)));
    }


}
