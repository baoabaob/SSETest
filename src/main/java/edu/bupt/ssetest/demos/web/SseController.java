package edu.bupt.ssetest.demos.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class SseController {

    private final Map<String, Map<String, SseEmitter>> roomEmitters = new ConcurrentHashMap<>();

    @GetMapping("/sse/connect/{roomId}")
    public SseEmitter connect(@PathVariable String roomId) {
        SseEmitter emitter = new SseEmitter();
        roomEmitters.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>()).put(emitter.toString(), emitter);

        emitter.onCompletion(() -> roomEmitters.get(roomId).remove(emitter.toString()));
        emitter.onTimeout(() -> roomEmitters.get(roomId).remove(emitter.toString()));

        return emitter;
    }

    public void sendMessageToRoom(String roomId, String message) {
        Map<String, SseEmitter> emitters = roomEmitters.get(roomId);
        if (emitters != null) {
            for (SseEmitter emitter : emitters.values()) {
                try {
                    emitter.send(SseEmitter.event().data(message));
                } catch (IOException e) {
                    emitters.remove(emitter.toString());
                }
            }
        }
    }

    @PostMapping("/sse/send")
    public void sendMessage(@RequestParam String roomId, @RequestBody String message) {
        System.out.println(message);
        sendMessageToRoom(roomId, message);
    }

    public void sendMessageToAllRooms(String message) {
        for (Map<String, SseEmitter> emitters : roomEmitters.values()) {
            for (SseEmitter emitter : emitters.values()) {
                try {
                    emitter.send(SseEmitter.event().data(message));
                } catch (IOException e) {
                    emitters.remove(emitter.toString());
                }
            }
        }
    }
}

