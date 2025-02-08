import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Blog } from '../../models/blog/blog.model';

@Injectable({
  providedIn: 'root',
})
export class BlogService {
  private readonly API_URL = 'http://localhost:8080/api/blogs';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token'); // Retrieve token from localStorage
    if (!token) {
      console.error('JWT Token is missing in localStorage.');
    }
    return new HttpHeaders({
      Authorization: `Bearer ${token}`, // Include JWT token
      'Content-Type': 'application/json', // Ensure JSON content type
    });
  }

  createBlog(blog: Partial<Blog>): Observable<Blog> {
    return this.http.post<Blog>(this.API_URL, blog, {
      headers: this.getHeaders(),
    });
  }

  getAllBlogs(): Observable<Blog[]> {
    return this.http.get<Blog[]>(this.API_URL, { headers: this.getHeaders() }); // Add headers
  }

  getBlogById(id: number): Observable<Blog> {
    return this.http.get<Blog>(`${this.API_URL}/${id}`, {
      headers: this.getHeaders(),
    });
  }

  getBlogsByVisibility(visibility: string): Observable<Blog[]> {
    return this.http.get<Blog[]>(`${this.API_URL}/visibility/${visibility}`, {
      headers: this.getHeaders(), // Add headers
    });
  }

  updateBlog(id: number, blog: Partial<Blog>): Observable<Blog> {
    return this.http.put<Blog>(`${this.API_URL}/${id}`, blog, {
      headers: this.getHeaders(), // Add headers
    });
  }

  deleteBlog(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`, {
      headers: this.getHeaders(), // Add headers
    });
  }
}
