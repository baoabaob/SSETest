package edu.bupt.ssetest.demos.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BroadcastTask {

    @Autowired
    private SseController sseController;

    @Scheduled(fixedRate = 180000) // 每3分钟执行一次
    public void sendBroadcastMessage() {
        sseController.sendMessageToAllRooms("广播消息");
    }
}
