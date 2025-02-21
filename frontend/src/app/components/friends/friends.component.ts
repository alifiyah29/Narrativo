import { Component, OnInit } from '@angular/core';
import { FriendService } from '../../services/friends/friends.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-friends',
  imports: [FormsModule, CommonModule],
  templateUrl: './friends.component.html',
  styleUrls: ['./friends.component.css'],
})
export class FriendsComponent implements OnInit {
  friends: any[] = [];
  friendRequests: any[] = [];
  searchUsername: string = '';

  constructor(private friendService: FriendService) {}

  ngOnInit() {
    this.loadFriends();
    this.loadFriendRequests();
  }

  loadFriends() {
    this.friendService.getFriends().subscribe({
      next: (data) => (this.friends = data),
      error: (error) => console.error('Error loading friends:', error),
    });
  }

  loadFriendRequests() {
    this.friendService.getFriendRequests().subscribe({
      next: (data) => (this.friendRequests = data),
      error: (error) => console.error('Error loading friend requests:', error),
    });
  }

  sendRequest(username: string) {
    if (!username) {
      alert('Please enter a username.');
      return;
    }
    this.friendService.sendFriendRequest(username).subscribe({
      next: (response) => {
        alert(response.message); // Display the message from the backend
        this.loadFriendRequests();
      },
      error: (error) => console.error('Error sending friend request:', error),
    });
  }

  acceptRequest(requestId: number) {
    this.friendService.acceptFriendRequest(requestId).subscribe({
      next: (response) => {
        alert(response.message); // Display the message from the backend
        this.loadFriendRequests();
        this.loadFriends();
      },
      error: (error) => console.error('Error accepting friend request:', error),
    });
  }

  rejectRequest(requestId: number) {
    this.friendService.rejectFriendRequest(requestId).subscribe({
      next: (response) => {
        alert(response.message); // Display the message from the backend
        this.loadFriendRequests();
      },
      error: (error) => console.error('Error rejecting friend request:', error),
    });
  }
}
