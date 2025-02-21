package com.narrativo.services;

import com.narrativo.models.FriendRequest;
import com.narrativo.models.User;
import com.narrativo.repositories.FriendRequestRepository;
import com.narrativo.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public List<User> getFriends(String username) {
        User user = userRepository.findByUsername(username);
        return userRepository.findByFriendsContaining(user);
    }

    @Transactional
    public void sendFriendRequest(String senderUsername, String receiverUsername) {
        User sender = userRepository.findByUsername(senderUsername);
        User receiver = userRepository.findByUsername(receiverUsername);

        if (sender == null || receiver == null) {
            throw new RuntimeException("User not found");
        }

        if (sender.equals(receiver)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        if (friendRequestRepository.existsBySenderAndReceiverAndStatus(sender, receiver, FriendRequest.Status.PENDING)) {
            throw new RuntimeException("Friend request already sent");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        friendRequestRepository.save(request);
    }

    @Transactional
    public void acceptFriendRequest(Long requestId) {
        Optional<FriendRequest> requestOpt = friendRequestRepository.findById(requestId);
        requestOpt.ifPresent(request -> {
            request.setStatus(FriendRequest.Status.ACCEPTED);
            friendRequestRepository.save(request);
            request.getSender().getFriends().add(request.getReceiver());
            request.getReceiver().getFriends().add(request.getSender());
            userRepository.save(request.getSender());
            userRepository.save(request.getReceiver());
        });
    }

    @Transactional
    public void rejectFriendRequest(Long requestId) {
        friendRequestRepository.deleteById(requestId);
    }

    // Fetch friend requests
    public List<FriendRequest> getFriendRequests(String username) {
        User user = userRepository.findByUsername(username);
        return friendRequestRepository.findByReceiverAndStatus(user, FriendRequest.Status.PENDING);
    }
}