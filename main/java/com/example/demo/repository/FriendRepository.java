package com.example.demo.repository;

import com.example.demo.model.Friend;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    Optional<Friend> findByRequesterAndReceiver(User requester, User receiver);

    List<Friend> findByReceiverAndStatus(User receiver, String status);

    List<Friend> findByRequesterAndStatus(User requester, String status);

    List<Friend> findByRequesterOrReceiverAndStatus(User requester, User receiver, String status);
    List<Friend> findByRequesterOrReceiver(User requester, User receiver);
}