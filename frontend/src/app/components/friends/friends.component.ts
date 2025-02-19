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
  searchUsername: string = '';  // ✅ Add the searchUsername property

  constructor(private friendService: FriendService) {}

  ngOnInit() {
    this.loadFriends();
    this.loadFriendRequests();
  }

  loadFriends() {
    this.friendService.getFriends().subscribe((data) => {
      this.friends = data;
    });
  }

  loadFriendRequests() {
    this.friendService.getFriendRequests().subscribe((data) => {
      this.friendRequests = data;
    });
  }

  sendRequest(username: string) {
    this.friendService.sendFriendRequest(username).subscribe(() => {
      alert('Friend request sent!');
    });
  }

  acceptRequest(requestId: number) {
    this.friendService.acceptFriendRequest(requestId).subscribe(() => {
      alert('Friend request accepted!');
      this.loadFriendRequests();
      this.loadFriends();
    });
  }

  rejectRequest(requestId: number) {
    this.friendService.rejectFriendRequest(requestId).subscribe(() => {
      alert('Friend request rejected!');
      this.loadFriendRequests();
    });
  }
}
