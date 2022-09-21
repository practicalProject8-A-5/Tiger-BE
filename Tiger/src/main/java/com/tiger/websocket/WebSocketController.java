//package com.tiger.websocket;
//
//import com.tiger.config.security.jwt.TokenProvider;
//import com.tiger.domain.UserDetailsImpl;
//import com.tiger.domain.member.Member;
//import com.tiger.notification.NotificationRepository;
//import com.tiger.websocket.chat.ChatMessageService;
//import com.tiger.websocket.chatDto.MessageRequestDto;
//import com.tiger.websocket.chatDto.MessageResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@Controller
//@RequiredArgsConstructor
//public class WebSocketController {
//
//    private final ChatMessageService messageService;
//    private final NotificationRepository notificationRepository;
//    private final SimpMessageSendingOperations messagingTemplate;
//
//    private final TokenProvider tokenProvider;
//
//    @MessageMapping("/chat/message")
//    public void message(MessageRequestDto messageRequestDto,
//                        @AuthenticationPrincipal UserDetails userDetails) {
//
//        Member member = ((UserDetailsImpl) userDetails).getMember();
//        String email = member.getEmail();
//        String name = member.getName();
//
//        // saveMessage
//        MessageResponseDto messageResponseDto = messageService.saveMessage(messageRequestDto, email, name);
//
//        // sendMessage
//        messageService.sendMessage(messageRequestDto, email, messageResponseDto);
//    }
//
//
//
//
//}
