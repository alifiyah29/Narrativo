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

@Component({
  selector: 'app-blog-editor',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './blog-editor.component.html',
})
export class BlogEditorComponent implements OnInit {
  blogForm: FormGroup;
  isEditing = false;
  blogId?: number;

  constructor(
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
    // Check if we're editing an existing blog
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
          // Populate the form with the blog data
          this.blogForm.patchValue({
            title: blog.title,
            content: blog.content,
            visibility: blog.visibility,
          });
        },
        error: (error) => {
          // Log the error with details
          console.error('Error loading blog:', error);
          console.error('Error status:', error.status); // HTTP status
          console.error('Error message:', error.message); // Error message
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
        next: () => {
          this.router.navigate(['/blogs']);
        },
        error: (error) => console.error('Error saving blog:', error),
      });
    }
  }
}
