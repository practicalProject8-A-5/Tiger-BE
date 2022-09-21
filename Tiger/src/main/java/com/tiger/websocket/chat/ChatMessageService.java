//package com.tiger.websocket.chat;
//
//import com.tiger.domain.member.Member;
//import com.tiger.notification.NotificationRepository;
//import com.tiger.notification.NotificationService;
//import com.tiger.redis.RedisMessagePublisher;
//import com.tiger.repository.MemberRepository;
//import com.tiger.websocket.chatDto.MessageRequestDto;
//import com.tiger.websocket.chatDto.MessageResponseDto;
//import com.tiger.websocket.chatDto.RoomMsgUpdateDto;
//import com.tiger.websocket.chatroom.ChatRoom;
//import com.tiger.websocket.chatroom.ChatRoomRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//import javax.transaction.Transactional;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ChatMessageService {
//
//    private final RedisMessagePublisher redisMessagePublisher;
//    private final ChatRoomRepository roomRepository;
//    private final ChatMessageRepository messageRepository;
//    private final MemberRepository memberRepository;
//    private final NotificationRepository notificationRepository;
//    private final SimpMessageSendingOperations messagingTemplate;
//    private final NotificationService notificationService;
//
//    private final RedisTemplate redisTemplate;
//    private final String imageDirName = "chatMessage";
//
//
//    private Map<Long, Integer> roomUsers;
//
//    @PostConstruct
//    private void init() {
//        roomUsers = new HashMap<>();
//    }
//
//    // 채팅 메시지 및 알림 저장하기
//    @Transactional
//    public MessageResponseDto saveMessage(MessageRequestDto requestDto,
//                                          String email,
//                                          String name) {
//
//        MessageRequestDto sendMessageDto = new MessageRequestDto();
//
//        ChatRoom chatRoom = roomRepository.findByIdFetch(requestDto.getRoomId()).orElseThrow(
//                ()-> new NullPointerException("해당 채팅방이 존재하지 않습니다.")
//        );
//        chatRoom.accOut(false);
//        chatRoom.reqOut(false);
//
//        Member receiver = chatRoom.getAcceptor();
//        Member sender = chatRoom.getRequester();
//
//        ChatMessage message = messageRepository.save(ChatMessage.createOf(requestDto, email, name));
//
//        // pub -> 채널 구독자에게 전달
//        redisMessagePublisher.publish(requestDto);
//
//        // 알림 보내기
//        notificationService.send(receiver);
//        notificationService.sender(sender);
//
//        return MessageResponseDto.createOf(message, email, name);
//    }
//
//
//    // 패팅 메시지 발송하기
//    public void sendMessage(MessageRequestDto requestDto, String memberId, MessageResponseDto responseDto) {
//
//        RoomMsgUpdateDto msgUpdateDto = RoomMsgUpdateDto.createFrom(requestDto);
//
//        MessageRequestDto sendMessageDto = new MessageRequestDto();
//
//        redisMessagePublisher.publish(requestDto);
//
//        // 개별 채팅 목록 보기 업데이트
//        messagingTemplate.convertAndSend("/sub/chat/rooms/" + memberId, msgUpdateDto);
//
//        // 채팅방 내부로 메시지 전송
//        messagingTemplate.convertAndSend("/sub/chat/room" + requestDto.getRoomId(), responseDto);
//    }
//
//
//    public List<MessageResponseDto> getMessage(Long roomId, Long memberId, String name) {
//
//        List<ChatMessage> messages = messageRepository.findAllByRoomIdOrderByIdAsc(roomId);
//
//        List<MessageResponseDto> responseDtos = new ArrayList<>();
//
//        messageRepository.updateChatMessage(roomId, memberId);
//
//        for (ChatMessage message : messages) {
//            responseDtos.add(MessageResponseDto.createFrom(message));
//        }
//
//        return responseDtos;
//
//    }
//
//
//
//
//}
