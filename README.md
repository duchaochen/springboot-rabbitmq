### rabbitmq配置
    
      在application.properties全局配置文件中配置以下代码:
        spring.rabbitmq.host=192.168.25.144
        spring.rabbitmq.username=guest
        spring.rabbitmq.password=guest
        spring.rabbitmq.port=5672
       
      1.手动在后台添加交换器和消息队列,可以查看word文档
        链接：
        
      2.自定义message发送代码
          发送代码
                byte[] str = "你好吗，我是代码".getBytes();
                Message message = new Message(str,new MessageProperties());
                rabbitTemplate.send("exchange.kindstar.listest","queues",message);
                
          接收代码
            Message receive = rabbitTemplate.receive("queues.lis.kc");
            if (receive != null) {
                byte[] body = receive.getBody();
                System.out.println(new String(body));
            }
            
      3.对象形式发送与接收
            1)由于内置的默认序列化不是使用Jackson2JsonMessageConverter，所以现在需要自己配置一个使用Jackson2JsonMessageConverter的配置对象
                代码如下：
                    @Configuration
                    public class MyRabbitmqConfig {
                    
                        @Bean
                        public MessageConverter messageConverter() {
                            return new Jackson2JsonMessageConverter();
                        }
                    }
             这样就会在后台web端查看时是json格式的数据了，而不是被序列化过的乱码
             
            测试发送代码：
                Map map = new HashMap(10);
        		map.put("1","张三111");
        		map.put("2","李四111");
        		map.put("3","王五111");
        		rabbitTemplate.convertAndSend("exchange.kindstar.listest","queues.lis",map);
        		
            测试接收代码：
                Object o = rabbitTemplate.receiveAndConvert("queues.lis.kc");
                System.out.println(o);
                
      4.时时监听接收消息队列
            1)需要开启消息队列监听,只需要在启动类添加一个@EnableRabbit注解
            2)编写一个监听的service类代码
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
                
             解释一下:@RabbitListener(queues = "queues.lis.zl")表示监听消息队列名称为《queues.lis.zl》信息
              public void receive_zl(Map<String,Object> map)表示接收对方发送的信息,直接转换成map类型
              
              
              public void receive_ks(Message message)表示接收message类型，里面包含信息头和未还有数据,数据为byte类型.
              也就是说这个是可以接收所有类型的信息
            
      5.代码创建消息队列，交换器，消息队列，以及绑定
        代码如下：
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