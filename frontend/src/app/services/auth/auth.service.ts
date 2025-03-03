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

interface DecodedToken extends JwtPayload {
  userId: number;
  username: string;
  role: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api/auth';
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
        localStorage.setItem('user', JSON.stringify(response)); // Store user details
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
      window.location.href = '/login';
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
      const decoded = jwtDecode<DecodedToken>(token);
      const currentTime = Math.floor(Date.now() / 1000);
      return decoded.exp ? decoded.exp < currentTime : true;
    } catch (error) {
      console.error('Error decoding token:', error);
      return true;
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

  getCurrentUser(): {
    userId: number;
    username: string;
    email: string;
    role: string;
  } | null {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  getCurrentUserRole(): string | null {
    const token = this.getToken();
    if (token) {
      try {
        const decoded = jwtDecode<DecodedToken>(token);
        return decoded.role;
      } catch (error) {
        console.error('Error decoding token:', error);
        return null;
      }
    }
    return null;
  }
}