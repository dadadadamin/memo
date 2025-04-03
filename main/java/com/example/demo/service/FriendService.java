package com.example.demo.service;

import com.example.demo.dto.FriendRequest;
import com.example.demo.model.Friend;
import com.example.demo.model.User;
import com.example.demo.repository.FriendRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public void sendFriendRequest(Long fromUserId, String toUserEmail) {
        User requester = userRepository.findById(fromUserId)
                .orElseThrow(() -> new RuntimeException("요청자 없음"));
        User receiver = userRepository.findByEmail(toUserEmail)
                .orElseThrow(() -> new RuntimeException("해당 이메일 없음"));

        // 이미 요청한 친구인지 확인
        if (friendRepository.findByRequesterAndReceiver(requester, receiver).isPresent()) {
            throw new RuntimeException("이미 요청된 친구입니다.");
        }

        Friend request = Friend.builder()
                .requester(requester)
                .receiver(receiver)
                .status("pending")
                .build();

        friendRepository.save(request);
    }

    public void respondToRequest(Long friendRequestId, boolean accepted) {
        Friend friend = friendRepository.findById(friendRequestId)
                .orElseThrow(() -> new RuntimeException("요청 없음"));

        friend.setStatus(accepted ? "accepted" : "rejected");
        friendRepository.save(friend);
    }

    public List<Friend> getFriendList(User user) {
        return friendRepository.findByRequesterOrReceiverAndStatus(user, user, "accepted");
    }

    public List<Friend> getPendingRequests(User user) {
        return friendRepository.findByReceiverAndStatus(user, "pending");
    }
    // 친구 목록 가져오기
    public List<FriendRequest> getFriends(User currentUser) {
        List<Friend> friends = friendRepository.findByRequesterOrReceiver(currentUser, currentUser);

        return friends.stream()
                .filter(friend -> "accepted".equals(friend.getStatus()))
                .map(friend -> {
                    User other = friend.getRequester().equals(currentUser)
                            ? friend.getReceiver()
                            : friend.getRequester();
                    return new FriendRequest(
                            other.getId(),
                            other.getEmail(),
                            friend.getStatus()
                    );
                })
                .collect(Collectors.toList());
    }
}