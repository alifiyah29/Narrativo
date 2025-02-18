import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { Blog } from '../../models/blog/blog.model';

@Injectable({
  providedIn: 'root',
})
export class BlogService {
  private readonly API_URL = 'http://localhost:8080/api/blogs';
  private visibilityFilter = new BehaviorSubject<string>('ALL');
  private blogsCache = new BehaviorSubject<Blog[]>([]);

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('JWT Token is missing in localStorage.');
    }
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    });
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let errorMessage = 'An error occurred';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  setVisibilityFilter(visibility: string): void {
    this.visibilityFilter.next(visibility);
  }

  getFilteredBlogs(): Observable<Blog[]> {
    return this.visibilityFilter.pipe(
      switchMap((filter) =>
        filter === 'ALL'
          ? this.getAllBlogs()
          : this.getBlogsByVisibility(filter)
      ),
      catchError(this.handleError)
    );
  }

  createBlog(blog: Partial<Blog>): Observable<Blog> {
    return this.http
      .post<Blog>(this.API_URL, blog, { headers: this.getHeaders() })
      .pipe(
        map((newBlog) => {
          const currentBlogs = this.blogsCache.value;
          this.blogsCache.next([...currentBlogs, newBlog]);
          return newBlog;
        }),
        catchError(this.handleError)
      );
  }

  getAllBlogs(): Observable<Blog[]> {
    return this.http
      .get<Blog[]>(this.API_URL, { headers: this.getHeaders() })
      .pipe(
        map((blogs) => {
          this.blogsCache.next(blogs);
          return blogs;
        }),
        catchError(this.handleError)
      );
  }

  getBlogById(id: number): Observable<Blog> {
    return this.http
      .get<Blog>(`${this.API_URL}/${id}`, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  getBlogsByVisibility(visibility: string): Observable<Blog[]> {
    return this.http
      .get<Blog[]>(`${this.API_URL}/visibility/${visibility}`, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((blogs) => {
          this.blogsCache.next(blogs);
          return blogs;
        }),
        catchError(this.handleError)
      );
  }

  updateBlog(id: number, blog: Partial<Blog>): Observable<Blog> {
    return this.http
      .put<Blog>(`${this.API_URL}/${id}`, blog, { headers: this.getHeaders() })
      .pipe(
        map((updatedBlog) => {
          const currentBlogs = this.blogsCache.value;
          const updatedBlogs = currentBlogs.map((b) =>
            b.id === id ? updatedBlog : b
          );
          this.blogsCache.next(updatedBlogs);
          return updatedBlog;
        }),
        catchError(this.handleError)
      );
  }

  incrementViews(id: number): Observable<Blog> {
    return this.http
      .put<Blog>(`${this.API_URL}/${id}/view`, null, {
        headers: this.getHeaders(),
      })
      .pipe(
        map((updatedBlog) => {
          const currentBlogs = this.blogsCache.value;
          const updatedBlogs = currentBlogs.map((b) =>
            b.id === id ? updatedBlog : b
          );
          this.blogsCache.next(updatedBlogs);
          return updatedBlog;
        }),
        catchError(this.handleError)
      );
  }

  deleteBlog(id: number): Observable<void> {
    return this.http
      .delete<void>(`${this.API_URL}/${id}`, { headers: this.getHeaders() })
      .pipe(
        map(() => {
          const currentBlogs = this.blogsCache.value;
          this.blogsCache.next(currentBlogs.filter((b) => b.id !== id));
        }),
        catchError(this.handleError)
      );
  }

  getCachedBlogs(): Observable<Blog[]> {
    return this.blogsCache.asObservable();
  }
}