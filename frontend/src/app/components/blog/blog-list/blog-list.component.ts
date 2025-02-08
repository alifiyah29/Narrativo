import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BlogService } from '../../../services/blog/blog.service';
import { Blog } from '../../../models/blog/blog.model';

@Component({
  selector: 'app-blog-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './blog-list.component.html',
})
export class BlogListComponent implements OnInit {
  blogs: Blog[] = [];
  visibilityFilters: ('ALL' | 'PUBLIC' | 'PRIVATE')[] = [
    'ALL',
    'PUBLIC',
    'PRIVATE',
  ];
  currentVisibilityFilter: 'ALL' | 'PUBLIC' | 'PRIVATE' = 'ALL';

  constructor(private blogService: BlogService) {}

  ngOnInit() {
    this.loadBlogs();
  }

  loadBlogs() {
    if (this.currentVisibilityFilter === 'ALL') {
      this.blogService.getAllBlogs().subscribe({
        next: (blogs) => (this.blogs = blogs),
        error: (error) => console.error('Error loading blogs:', error),
      });
    } else {
      this.blogService
        .getBlogsByVisibility(this.currentVisibilityFilter)
        .subscribe({
          next: (blogs) => (this.blogs = blogs),
          error: (error) => console.error('Error loading blogs:', error),
        });
    }
  }

  onVisibilityFilterChange(visibility: 'ALL' | 'PUBLIC' | 'PRIVATE') {
    this.currentVisibilityFilter = visibility;
    this.loadBlogs();
  }

  deleteBlog(id: number) {
    if (confirm('Are you sure you want to delete this blog?')) {
      this.blogService.deleteBlog(id).subscribe({
        next: () => {
          this.blogs = this.blogs.filter((blog) => blog.id !== id);
        },
        error: (error) => console.error('Error deleting blog:', error),
      });
    }
  }
}