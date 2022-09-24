//package com.tiger.websocket.chatroom;
//
//import com.tiger.domain.member.Member;
//import com.tiger.exception.CustomException;
//import com.tiger.exception.StatusCode;
//import com.tiger.notification.NotificationRepository;
//import com.tiger.repository.MemberRepository;
//import com.tiger.websocket.chat.ChatMessage;
//import com.tiger.websocket.chat.ChatMessageRepository;
//import com.tiger.websocket.chatDto.MessageResponseDto;
//import com.tiger.websocket.chatDto.RoomDto;
//import com.tiger.websocket.chatDto.RoomResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.tiger.websocket.chatroom.ChatRoomService.UserTypeEnum.Type.ACCEPTOR;
//import static com.tiger.websocket.chatroom.ChatRoomService.UserTypeEnum.Type.REQUESTER;
//
//@RequiredArgsConstructor
//@Service
//public class ChatRoomService {
//
//    private final NotificationRepository notificationRepository;
//    private final ChatRoomRepository roomRepository;
//    private final ChatMessageRepository messageRepository;
//
//    private final ChatMessage chatMessage;
//    private final SimpMessageSendingOperations messagingTemplate;
//    private final MemberRepository memberRepository;
//
//
//    // 채팅방 만들기
//    @Transactional
//    public Long createRoom(Long memberId, Long acceptId) {
//
//        if (memberId.equals(acceptId)) {
//            throw new CustomException(StatusCode.CANNOT_CHAT_BY_ONESELF);
//        }
//        Member acceptor = memberRepository.findByIdAndIsValid(acceptId, true).orElseThrow(() -> {
//            throw new CustomException(StatusCode.USER_NOT_FOUND);
//        });
//        Member requester = memberRepository.findByIdAndIsValid(memberId, true).orElseThrow(() -> {
//            throw new CustomException(StatusCode.USER_NOT_FOUND);
//        });
//        ChatRoom chatRoom = roomRepository.findByMember(requester, acceptor).orElseGet(() -> {
//            ChatRoom c = roomRepository.save(ChatRoom.createOf(requester, acceptor));
//            return c;
//        });
//        chatRoom.enter();
//        return chatRoom.getId();
//    }
//
//
//    // 방을 나간 상태로 변경하기
//    @Transactional
//    public void exitRoom(Long roomId, Long memberId) {
//
//        Member member = memberRepository.findByIdAndIsValid(memberId, true).orElseThrow(() -> {
//            throw new CustomException(StatusCode.USER_NOT_FOUND);
//        });
//        ChatRoom chatRoom = roomRepository.findByIdFetch(roomId).orElseThrow(() -> {
//            throw new NullPointerException("해당 룸 아이디가 존재하지 않습니다.");
//        });
//
//        if (chatRoom.getRequester().getId().equals(memberId)) {
//            chatRoom.reqOut(true);
//        } else if (chatRoom.getAcceptor().getId().equals(memberId)) {
//            chatRoom.accOut(true);
//        } else {
//            throw new CustomException(StatusCode.INVALID_CHATROOM_EXIT);
//        }
//        if (chatRoom.getAccOut() && chatRoom.getReqOut()) {
//            roomRepository.deleteById(chatRoom.getId());
//        } else {
//            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(),
//                    MessageResponseDto.createFrom(
//                            messageRepository.save(ChatMessage.createOutOf(roomId, member))
//                    ));
//        }
//
//    }
//
//
//    // 사용자별 채팅방 전체 목록 가져오기
//    public List<RoomResponseDto> getRooms(Long memberId, String name) {
//
//        Member member = memberRepository.findByIdAndIsValid(memberId, true).orElseThrow(() -> {
//            throw new CustomException(StatusCode.USER_NOT_FOUND);
//        });
//        List<RoomDto> roomDtos = roomRepository.findAllWith(member);
//        return getMessages(roomDtos, memberId, name);
//    }
//
//    public List<RoomResponseDto> getMessages(List<RoomDto> roomDtos, Long memberId, String name) {
//
//        List<RoomResponseDto> prefix = new ArrayList<>();
//        List<RoomResponseDto> suffix = new ArrayList<>();
//
//        String type = chatMessage.getType();
//
//        for (RoomDto roomDto : roomDtos) {
//            if (roomDto.getAccId().equals(memberId)) {
//                if (!roomDto.getAccOut()) { // 만약 Acc(내)가 나가지 않았다면
//                    int unreadCnt = messageRepository.countMsg(roomDto.getReqId(), roomDto.getRoomId());
//                    if (roomDto.getAccFixed()) {
//                        prefix.add(RoomResponseDto.createOf(type, ACCEPTOR, roomDto, unreadCnt, false));
//                    } else {
//                        suffix.add(RoomResponseDto.createOf(type, ACCEPTOR, roomDto, unreadCnt, false));
//                    }
//                }
//            } else if (roomDto.getReqId().equals(memberId)) {
//                if (!roomDto.getReqOut()) { // 만약 Req(내)가 나가지 않았다면
//                    int unreadCnt = messageRepository.countMsg(roomDto.getAccId(), roomDto.getRoomId());
//                    if (roomDto.getReqFixed()) {
//                        prefix.add(RoomResponseDto.createOf(type, REQUESTER, roomDto, unreadCnt, false));
//
//                    } else {
//                        suffix.add(RoomResponseDto.createOf(type, REQUESTER, roomDto, unreadCnt, false));
//                    }
//                }
//            }
//        }
//        prefix.addAll(suffix);
//        return prefix;
//    }
//
//    public enum UserTypeEnum {
//        ACCEPTOR(Type.ACCEPTOR),
//        REQUESTER(Type.REQUESTER);
//
//        private final String userType;
//
//        UserTypeEnum(String userType) {
//            this.userType = userType;
//        }
//
//        public static class Type {
//            public static final String ACCEPTOR = "ACCEPTOR";
//            public static final String REQUESTER = "REQUESTER";
//        }
//    }
//
//}
