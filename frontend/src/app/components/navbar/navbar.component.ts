import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [MatToolbarModule, MatIcon, NgIf],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent implements OnInit {
  username: string | null = null;
  isDashboard: boolean = false;
  isBlogsPage: boolean = false;

  constructor(private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    // Get the current user's username
    const user = this.authService.getCurrentUser();
    this.username = user ? user.username : null;

    // Check the current route to determine which buttons to show
    this.isDashboard = this.router.url === '/dashboard';
    this.isBlogsPage = this.router.url.startsWith('/blogs');
  }

  // Navigate to the dashboard
  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  // Navigate to the blogs page
  goToBlogs(): void {
    this.router.navigate(['/blogs']);
  }

  // Navigate to the create blog page
  createBlog(): void {
    this.router.navigate(['/blogs/new']);
  }

  // Logout the user
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
