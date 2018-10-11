package com.adu.springboot.mq;

import com.adu.springboot.mq.bean.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootRabbitmqApplicationTests {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Test
	public void contextLoads() {
		byte[] str = "你好吗，我是代码!!!!!!!".getBytes();
		Message message = new Message(str,new MessageProperties());
		rabbitTemplate.send("exchange.kindstar.listest","queues.lis.kc",message);
	}

	@Test
	public void testReceive() {
		Message receive = rabbitTemplate.receive("queues.lis.kc");
		if (receive != null) {
			byte[] body = receive.getBody();
			System.out.println(new String(body));
		}
	}

	/**
	 * 发送
	 */
	@Test
	public void testObject() {
		Map map = new HashMap(10);
		map.put("msg","这是一个消息111");
		map.put("1","张三111");
		map.put("2","李四222");
		map.put("3","王五3333");
		map.put("book",new Book("java核心技术",120));
		rabbitTemplate.convertAndSend("exchange.kindstar.listest","queues.lis",map);
	}

	/**
	 * 接收
	 */
	@Test
	public void testReceiveObject() {
		while (true){
			Object o = rabbitTemplate.receiveAndConvert("queues.lis.zl");
			if (o == null) {
				break;
			}
			System.out.println(o);
		}

	}

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Test
	public void testExchage() {
		//创建交换器
		amqpAdmin.declareExchange(new TopicExchange("exchange.kindstar.lis"));

		//创建消息队列
		amqpAdmin.declareQueue(new Queue("kindstar.lis.cw",true));

		/**
		 * binding:
		 * 		destination:目的地
		 * 		destinationType:绑定的是一个消息队列
		 */
		//消息队列目的地名称
		String destination ="kindstar.lis.cw";
		//使用消息队列
		Binding.DestinationType destinationType = Binding.DestinationType.QUEUE;
		//指定那个交换器
		String exchange = "exchange.kindstar.lis";
		//
		String routingKey = "kindstar.lis.#";
		amqpAdmin.declareBinding(new Binding(destination,destinationType,exchange,routingKey,null));
	}
}
