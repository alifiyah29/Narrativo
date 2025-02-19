package com.narrativo.repositories;

import com.narrativo.models.FriendRequest;
import com.narrativo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.Status status);
    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequest.Status status);
    boolean existsBySenderAndReceiverAndStatus(User sender, User receiver, FriendRequest.Status status);
}