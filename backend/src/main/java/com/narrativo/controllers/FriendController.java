package com.narrativo.controllers;

import com.narrativo.models.FriendRequest;
import com.narrativo.models.User;
import com.narrativo.services.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<List<User>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        List<User> friends = friendService.getFriends(userDetails.getUsername());
        return ResponseEntity.ok(friends);
    }

    @PostMapping("/add/{username}")
    public ResponseEntity<Map<String, String>> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable String username
    ) {
        friendService.sendFriendRequest(userDetails.getUsername(), username);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend request sent.");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/accept/{requestId}")
    public ResponseEntity<Map<String, String>> acceptFriendRequest(@PathVariable Long requestId) {
        friendService.acceptFriendRequest(requestId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend request accepted.");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reject/{requestId}")
    public ResponseEntity<Map<String, String>> rejectFriendRequest(@PathVariable Long requestId) {
        friendService.rejectFriendRequest(requestId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Friend request rejected.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendRequest>> getFriendRequests(@AuthenticationPrincipal UserDetails userDetails) {
        List<FriendRequest> requests = friendService.getFriendRequests(userDetails.getUsername());
        return ResponseEntity.ok(requests);
    }
}