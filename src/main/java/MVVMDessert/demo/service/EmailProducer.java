package MVVMDessert.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import MVVMDessert.demo.dto.EmailRequest;

@Service
public class EmailProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public EmailProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmailRequest(String to, String subject, String body) throws JsonProcessingException {
        EmailRequest email = new EmailRequest(to, subject, body);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(email);
        kafkaTemplate.send("email-topic", json).addCallback(
        	    success -> System.out.println("Email request sent: " + email),
        	    failure -> System.err.println("Failed to send email request: " + failure.getMessage()));
    }
}
