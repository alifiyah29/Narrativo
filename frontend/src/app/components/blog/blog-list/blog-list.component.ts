import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Router } from '@angular/router';

import { BlogService } from '../../../services/blog/blog.service';
import { AuthService } from '../../../services/auth/auth.service';
import { FriendService } from '../../../services/friends/friends.service'; // Import FriendService
import { Blog, Visibility } from '../../../models/blog/blog.model';
import { NavbarComponent } from '../../navbar/navbar.component';

@Component({
  selector: 'app-blog-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatIconModule,
    MatButtonModule,
    MatMenuModule,
    MatToolbarModule,
    MatProgressSpinnerModule,
    NavbarComponent,
  ],
  templateUrl: './blog-list.component.html',
  styleUrls: ['./blog-list.component.css'],
})
export class BlogListComponent implements OnInit, OnDestroy {
  blogs: Blog[] = [];
  currentVisibilityFilter: Visibility | 'ALL' = 'ALL';
  isLoading: boolean = true;
  username: string = '';
  isAdmin: boolean = false; // Add isAdmin property
  friends: string[] = []; // Add friends property
  Visibility = Visibility; // Expose the enum to the template
  private destroy$ = new Subject<void>();

  constructor(
    private blogService: BlogService,
    private authService: AuthService,
    private friendService: FriendService, // Inject FriendService
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initializeComponent();
    this.subscribeToBlogs();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private initializeComponent(): void {
    const currentUser = this.authService.getCurrentUser();
    this.username = currentUser?.username || 'Guest';
    this.isAdmin = currentUser?.role === 'ADMIN'; // Set isAdmin based on user role

    // Load friends for the current user
    this.friendService.getFriends().subscribe({
      next: (friends) => {
        this.friends = friends.map(
          (friend: { username: string }) => friend.username
        ); // Explicitly type the `friend` parameter
      },
      error: (error) => {
        console.error('Error loading friends:', error);
      },
    });

    this.loadBlogs();
  }

  private subscribeToBlogs(): void {
    this.blogService
      .getCachedBlogs()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blogs) => {
          this.blogs = blogs;
          this.isLoading = false;
        },
        error: (error) => this.handleError('Error loading blogs', error),
      });
  }

  loadBlogs(): void {
    this.isLoading = true;
    this.blogService
      .getBlogsByVisibility(this.currentVisibilityFilter)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blogs) => {
          this.blogs = blogs;
          this.isLoading = false;
        },
        error: (error) => this.handleError('Error loading blogs', error),
      });
  }

  onVisibilityFilterChange(visibility: Visibility | 'ALL'): void {
    this.currentVisibilityFilter = visibility;
    this.blogService.setVisibilityFilter(visibility);
    this.loadBlogs();
  }

  deleteBlog(id: number): void {
    if (confirm('Are you sure you want to delete this blog?')) {
      this.blogService
        .deleteBlog(id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.showMessage('Blog deleted successfully');
          },
          error: (error) => this.handleError('Error deleting blog', error),
        });
    }
  }

  viewBlog(id: number): void {
    this.blogService
      .incrementViews(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blog) => {
          console.log('Blog views incremented:', blog.views);
        },
        error: (error) => this.handleError('Error incrementing views', error),
      });
  }

  private handleError(message: string, error: any): void {
    console.error(message, error);
    this.isLoading = false;
    this.showMessage(`${message}: ${error.message}`);
  }

  private showMessage(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
    });
  }

  trackByBlogId(index: number, blog: Blog): number {
    return blog.id;
  }

  createBlog(): void {
    this.router.navigate(['/blogs/new']);
  }
}
