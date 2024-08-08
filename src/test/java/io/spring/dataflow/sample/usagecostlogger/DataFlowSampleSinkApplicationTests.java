package io.spring.dataflow.sample.usagecostlogger;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.CompositeMessageConverter;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class DataFlowSampleSinkApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void usageCostLogger_WhenSentMessageOnUsageCostChannel_ReceivesUsageCost(CapturedOutput output){

        try(ConfigurableApplicationContext context = new SpringApplicationBuilder(
                TestChannelBinderConfiguration.getCompleteConfiguration(DataFlowSampleSinkApplication.class))
                .web(WebApplicationType.NONE)
                .run())
        {

            InputDestination inputDestination = context.getBean(InputDestination.class);

            CompositeMessageConverter messageConverter = context.getBean(CompositeMessageConverter.class);

            UsageCostDetail usageCostDetail = new UsageCostDetail("user1", 3.0, 5.0);

            Map<String, Object> headers = new HashMap<>();
            headers.put("contentType", "application/json");
            MessageHeaders messageHeaders = new MessageHeaders(headers);

            Message<?> message = messageConverter.toMessage(usageCostDetail, messageHeaders);

            inputDestination.send(message);

            Awaitility.await().until(output::getOut, value -> value.contains("user1"));
        }
    }


}
