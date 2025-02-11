import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  UserAnalytics,
  AdminAnalytics,
} from '../../models/analytics/analytics.model';

@Injectable({
  providedIn: 'root',
})
export class AnalyticsService {
  private readonly API_URL = 'http://localhost:8080/api/analytics';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('No token found! User is not authenticated.');
    }
    return new HttpHeaders({
      Authorization: `Bearer ${token}`, // Attach the JWT token
      'Content-Type': 'application/json',
    });
  }

  getUserAnalytics(): Observable<UserAnalytics> {
    return this.http.get<UserAnalytics>(`${this.API_URL}/user`, {
      headers: this.getHeaders(),
    });
  }

  getAdminAnalytics(): Observable<AdminAnalytics> {
    return this.http.get<AdminAnalytics>(`${this.API_URL}/admin`, {
      headers: this.getHeaders(),
    });
  }

  getMonthlyBlogTrends(): Observable<any[]> {
    return this.http.get<any[]>(`${this.API_URL}/monthly-trends`, {
      headers: this.getHeaders(),
    });
  }
}
