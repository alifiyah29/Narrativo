import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { BlogService } from '../../../services/blog/blog.service';
import { AuthService } from '../../../services/auth/auth.service';
import { Blog, Visibility } from '../../../models/blog/blog.model';

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
  ],
  templateUrl: './blog-list.component.html',
  styleUrls: ['./blog-list.component.css'],
})
export class BlogListComponent implements OnInit {
  blogs: Blog[] = [];
  currentVisibilityFilter: Visibility | 'ALL' = 'ALL'; // Use enum
  isLoading: boolean = true;
  username: string = '';

  // Expose the Visibility enum to the template
  Visibility = Visibility;

  constructor(
    private blogService: BlogService,
    private authService: AuthService
  ) {}

  ngOnInit() {
    const currentUser = this.authService.getCurrentUser();
    this.username = currentUser ? currentUser.username : 'Guest';
    this.loadBlogs();
  }

  loadBlogs() {
    this.isLoading = true;
    const request =
      this.currentVisibilityFilter === 'ALL'
        ? this.blogService.getAllBlogs()
        : this.blogService.getBlogsByVisibility(this.currentVisibilityFilter);

    request.subscribe({
      next: (blogs) => {
        this.blogs = blogs;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading blogs:', error);
        this.isLoading = false;
      },
    });
  }

  onVisibilityFilterChange(visibility: Visibility | 'ALL') {
    this.currentVisibilityFilter = visibility;
    this.loadBlogs();
  }

  deleteBlog(id: number) {
    if (confirm('Delete this blog?')) {
      this.blogService.deleteBlog(id).subscribe({
        next: () => {
          this.blogs = this.blogs.filter((blog) => blog.id !== id);
        },
        error: (error) => console.error('Error deleting blog:', error),
      });
    }
  }

  viewBlog(id: number) {
    this.blogService.incrementViews(id).subscribe({
      next: (blog) => {
        console.log('Blog views incremented:', blog.views);
      },
      error: (error) => console.error('Error incrementing views:', error),
    });
  }

  trackByBlogId(index: number, blog: Blog): number {
    return blog.id;
  }
}