
## RabbitMQ Practice


### 소개
rabbit mq 튜토리얼에 따라 work queue, pub/sub, rpc 모델을 연습해보자

<br/>


### 실습 전 준비사항
RabbitMQ 이미지를 사용한 컨테이너를 생성

<pre><code> docker run -d --hostname my-rabbit --name some-rabbit -p 5672:5672 -p 15672:15672  -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=password rabbitmq:3-management </code></pre>  

<br/>


### 설명
### Work Queue

많은 자원을 사용하는 일과 긴 시간을 기다려 완료되는 것을 피하기위한 큐다.

Queue에 들어가는 데이터들을 worker에 **순서대로** 일을 분배한다 (round robin dispatching)

<br/>

### Pub/Sub
1. ### fanout
모든 큐에 대해서 메시지를 전달함(braodcast)


2. ### direct(exchange type)
라우팅 키를 설정하여 특정한 바인딩 키를 가진 큐만 데이터를 받도록 함

3. ### topic
단일 라우팅키로 인한 direct 설정의 한계를 넘어서
다중 라우팅키를 #과 *로 설정가능하다.

<br/>

### rpc (remote procedure call)

replyTo(callback_queue name)와 correlation_id를 사용하여
클라이언트는 특정요청(위의 두 데이터와 함께) 특정 큐에 보낸다

서버는 큐의 데이터를 가져온 후 데이터 처리(메서드 실행)하고 replyTo 콜백 큐에 응답 값을 전달(correlation_id와 함꼐)

클라이언트는 replyTo(콜백큐)에서 값을 가져오고 correlation_id를 비교
일치하다면 결과값을 전달

