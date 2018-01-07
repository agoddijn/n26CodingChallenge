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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

import java.util.Date;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTestsTimedBasic {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void basicStatisticsShouldBeCorrect() throws Exception {

        Date now = new Date();
        Transaction t1 = new Transaction(10, now.getTime() - 55000);

        this.mockMvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(t1)));

        Statistic expectedStat = new Statistic(10, 10, 10,10, 1);

        this.mockMvc.perform(get("/statistics"))
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedStat)));

        Thread.sleep(6000);

        Statistic expectedStat2 = new Statistic(0, Float.NaN, Float.NEGATIVE_INFINITY ,Float.POSITIVE_INFINITY, 0);

        this.mockMvc.perform(get("/statistics"))
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedStat2)));
    }

}
