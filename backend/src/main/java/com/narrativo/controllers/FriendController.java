package com.narrativo.controllers;

import com.narrativo.models.User;
import com.narrativo.services.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> sendFriendRequest(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable String username) {
        friendService.sendFriendRequest(userDetails.getUsername(), username);
        return ResponseEntity.ok("Friend request sent.");
    }

    @PutMapping("/accept/{requestId}")
    public ResponseEntity<String> acceptFriendRequest(@PathVariable Long requestId) {
        friendService.acceptFriendRequest(requestId);
        return ResponseEntity.ok("Friend request accepted.");
    }

    @DeleteMapping("/reject/{requestId}")
    public ResponseEntity<String> rejectFriendRequest(@PathVariable Long requestId) {
        friendService.rejectFriendRequest(requestId);
        return ResponseEntity.ok("Friend request rejected.");
    }
}
