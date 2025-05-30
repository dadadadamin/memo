package com.example.demo.controller;

import com.example.demo.dto.FriendRequest;
import com.example.demo.model.User;
import com.example.demo.service.FriendService;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;
    private final UserService userService;

    @PostMapping("/request")
    public ResponseEntity<?> sendRequest(@RequestBody Map<String, String> body) {
        Long currentUserId = userService.getCurrentUserId();
        String email = body.get("email");

        friendService.sendFriendRequest(currentUserId, email);
        return ResponseEntity.ok(Map.of("message", "친구 요청 완료"));
    }

    @PostMapping("/respond/{id}")
    public ResponseEntity<?> respondRequest(@PathVariable Long id, @RequestParam boolean accept) {
        friendService.respondToRequest(id, accept);
        return ResponseEntity.ok(Map.of("message", accept ? "수락됨" : "거절됨"));
    }

    @GetMapping("/list")
    public ResponseEntity<List<FriendRequest>> getFriendList() {
        User currentUser = userService.getCurrentUser();
        List<FriendRequest> friends = friendService.getFriends(currentUser);
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/requests")
    public ResponseEntity<?> getPendingRequests() {
        User user = userService.getCurrentUser();
        return ResponseEntity.ok(friendService.getPendingRequests(user));
    }
}
