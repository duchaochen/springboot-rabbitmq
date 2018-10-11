package com.adu.springboot.mq.service;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class BookService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "queues.lis.zl")
    public void receive_zl(Map<String,Object> map) {
        System.out.println("======================zl============================");
        System.out.println(map);
    }

    @RabbitListener(queues = "queues.lis.cw")
    public void receive_cw(Map<String,Object> map) {
        System.out.println("======================cw============================");
        System.out.println(map);
    }

    @RabbitListener(queues = "queues.lis.ks")
    public void receive_ks(Message message) {
        System.out.println("======================cw============================");
        System.out.println(message);
    }
}
