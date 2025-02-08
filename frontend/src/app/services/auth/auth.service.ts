import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { JwtPayload, jwtDecode } from 'jwt-decode';
import {
  LoginRequest,
  RegisterRequest,
  AuthResponse,
} from '../../models/auth/auth.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth'; // Backend URL
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {
    this.checkToken();
  }

  register(request: RegisterRequest): Observable<any> {
    return this.http.post(`${this.API_URL}/register`, request).pipe(
      catchError((error) => {
        console.error('Registration error:', error);
        return throwError(() => error);
      })
    );
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_URL}/login`, request).pipe(
      tap((response) => {
        this.storeToken(response.token);
        this.isAuthenticatedSubject.next(true);
      }),
      catchError((error) => {
        console.error('Login error:', error);
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('token');
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']).catch((error) => {
      console.error('Logout redirection failed:', error);
      window.location.href = '/login'; // Fallback
    });
  }

  private checkToken(): void {
    const token = this.getToken();
    if (token) {
      const isExpired = this.isTokenExpired(token);
      this.isAuthenticatedSubject.next(!isExpired);
      if (isExpired) {
        this.logout();
      }
    } else {
      this.isAuthenticatedSubject.next(false);
    }
  }

  private isTokenExpired(token: string): boolean {
    try {
      const decoded = jwtDecode<JwtPayload>(token);
      const currentTime = Math.floor(Date.now() / 1000); // Current time in seconds
      return decoded.exp ? decoded.exp < currentTime : true;
    } catch (error) {
      console.error('Error decoding token:', error);
      return true; // Treat invalid tokens as expired
    }
  }

  storeToken(token: string): void {
    localStorage.setItem('token', token);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return token ? !this.isTokenExpired(token) : false;
  }
}
