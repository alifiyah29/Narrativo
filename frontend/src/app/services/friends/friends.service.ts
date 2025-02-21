import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class FriendService {
  private apiUrl = 'http://localhost:8080/api/friends';

  constructor(private http: HttpClient) {}

  // Helper method to get headers with JWT token
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); // Get JWT from storage
    if (!token) {
      console.error('No token found! User is not authenticated.');
    }
    return new HttpHeaders({
      Authorization: `Bearer ${token}`, // Attach token to header
      'Content-Type': 'application/json',
    });
  }

  getFriends(): Observable<any> {
    return this.http.get(`${this.apiUrl}`, { headers: this.getAuthHeaders() });
  }

  getFriendRequests(): Observable<any> {
    return this.http.get(`${this.apiUrl}/requests`, { headers: this.getAuthHeaders() });
  }

  sendFriendRequest(username: string): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/add/${username}`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  acceptFriendRequest(requestId: number): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/accept/${requestId}`,
      {},
      { headers: this.getAuthHeaders() }
    );
  }

  rejectFriendRequest(requestId: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/reject/${requestId}`, {
      headers: this.getAuthHeaders(),
    });
  }
}