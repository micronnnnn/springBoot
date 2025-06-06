package MVVMDessert.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import MVVMDessert.demo.dto.EmailRequest;

@Service
public class EmailConsumer {

    @Autowired
    private MailService mailService; // 假設你用 JavaMailSender 實作

    @KafkaListener(topics = "email-topic", groupId = "email-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(EmailRequest request) {
        System.out.println("收到寄信任務: " + request.getTo());
        mailService.sendMail(request.getTo(), request.getSubject(), request.getBody());
    }
}
