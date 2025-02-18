import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  constructor(private authService: AuthService, private router: Router) {}

  get username(): string {
    const user = this.authService.getCurrentUser();
    return user ? user.username : 'Guest';
  }

  goToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  goToBlogs(): void {
    this.router.navigate(['/blogs']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}