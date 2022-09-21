//package com.tiger.websocket.chatroom;
//
//import com.tiger.domain.UserDetailsImpl;
//import com.tiger.domain.member.Member;
//import com.tiger.service.MemberService;
//import com.tiger.websocket.chat.ChatMessageService;
//import com.tiger.websocket.chatDto.ChatUserRequestDto;
//import com.tiger.websocket.chatDto.MessageResponseDto;
//import com.tiger.websocket.chatDto.RoomResponseDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/chat")
//@RequiredArgsConstructor
//public class ChatRoomController {
//
//    private final ChatRoomService roomService;
//    private final ChatRoomRepository roomRepository;
//    private final ChatMessageService messageService;
//
//
//    // 채팅방 생성
//    @PostMapping("/room")
//    public Long createRoom(@AuthenticationPrincipal UserDetails userDetails,
//                           @RequestBody ChatUserRequestDto requestDto) {
//
//        Member member = ((UserDetailsImpl) userDetails).getMember();
//
//        Long memberId = member.getId();
//        Long acceptorId = requestDto.getOwnerId();
//
//        return roomService.createRoom(memberId, acceptorId);
//    }
//
//
//    // 전체 채팅방 목록 가져오기
//    @GetMapping("/rooms")
//    public List<RoomResponseDto> getRooms(@AuthenticationPrincipal UserDetails userDetails) {
//
//        Member member = ((UserDetailsImpl) userDetails).getMember();
//        Long memberId = member.getId();
//        String name = member.getName();
//
//        return roomService.getRooms(memberId, name);
//    }
//
//
//    // 개별 채팅방 메시지 불러오기
//    @GetMapping("/room/{roomId}")
//    public List<MessageResponseDto> getMessage(@PathVariable Long roomId,
//                                               @AuthenticationPrincipal UserDetails userDetails) {
//
//        Member member = ((UserDetailsImpl) userDetails).getMember();
//
//        Long memberId = member.getId();
//        String name = member.getName();
//
//        return messageService.getMessage(roomId, memberId, name);
//    }
//
//
//    // 채팅방 나가기
//    @GetMapping("/room/exit/{roomId}")
//    public ResponseEntity<?> exitRoom(@AuthenticationPrincipal UserDetails userDetails,
//                                          @PathVariable Long roomId) {
//
//        Member member = ((UserDetailsImpl) userDetails).getMember();
//        Long memberId = member.getId();
//
//        roomService.exitRoom(roomId, memberId);
//        return ResponseEntity.ok().body("ok");
//
//    }
//
//
//}
