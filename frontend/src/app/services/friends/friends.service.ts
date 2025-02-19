import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FriendService {
  private apiUrl = 'http://localhost:8080/api/friends';

  constructor(private http: HttpClient) {}

  getFriends(): Observable<any> {
    return this.http.get(`${this.apiUrl}`);
  }

  getFriendRequests(): Observable<any> {
    return this.http.get(`${this.apiUrl}/requests`);
  }

  sendFriendRequest(username: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/add/${username}`, {});
  }

  acceptFriendRequest(requestId: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/accept/${requestId}`, {});
  }

  rejectFriendRequest(requestId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/reject/${requestId}`);
  }
}
