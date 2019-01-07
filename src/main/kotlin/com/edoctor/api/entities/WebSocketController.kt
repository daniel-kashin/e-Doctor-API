package com.edoctor.api.entities

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Controller

@EnableScheduling
@Controller
class WebSocketController {

    @Autowired
    lateinit var template: SimpMessagingTemplate

    @MessageMapping("/send/message")
    @Scheduled(fixedRate = 5000L)
    fun onReceiveMessage(
            message: String,
            @DestinationVariable roomId: String
    ) {
        template.convertAndSend("/privateRoom", message)
    }

}