import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { BlogService } from '../../../services/blog/blog.service';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { NavbarComponent } from '../../navbar/navbar.component';

@Component({
  selector: 'app-blog-editor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './blog-editor.component.html',
  styleUrls: ['./blog-editor.component.css'],
})
export class BlogEditorComponent implements OnInit {
  blogForm: FormGroup;
  isEditing = false;
  blogId?: number;
  username: string = '';

  constructor(
    private authService: AuthService,
    private fb: FormBuilder,
    private blogService: BlogService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.blogForm = this.fb.group({
      title: ['', Validators.required],
      content: ['', Validators.required],
      visibility: ['PUBLIC', Validators.required],
    });
  }

  ngOnInit() {
    const currentUser = this.authService.getCurrentUser();
    this.username = currentUser ? currentUser.username : 'Guest';
    this.blogId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.blogId) {
      this.isEditing = true;
      this.loadBlog();
    }
  }

  loadBlog() {
    if (this.blogId) {
      this.blogService.getBlogById(this.blogId).subscribe({
        next: (blog) => {
          this.blogForm.patchValue({
            title: blog.title,
            content: blog.content,
            visibility: blog.visibility,
            user: blog.user, 
          });
        },
        error: (error) => {
          console.error('Error loading blog:', error);
          alert('Failed to load blog. Check the backend logs.');
        },
      });
    }
  }

  onSubmit() {
    if (this.blogForm.valid) {
      const blogData = this.blogForm.value;
      const request$ = this.isEditing
        ? this.blogService.updateBlog(this.blogId!, blogData)
        : this.blogService.createBlog(blogData);

      request$.subscribe({
        next: () => this.router.navigate(['/blogs']),
        error: (error) => {
          console.error('Error saving blog:', error);
          alert('Failed to save blog. Check the backend logs.');
        },
      });
    }
  }

  goToBlogs(): void {
    this.router.navigate(['/blogs']);
  }

  navigateToDashboard() {
    this.router.navigate(['/dashboard']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
