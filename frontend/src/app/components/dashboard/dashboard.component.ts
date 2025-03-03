import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import {
  MatCardModule,
  MatCardContent,
  MatCardHeader,
  MatCardTitle,
} from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatButtonModule } from '@angular/material/button';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { AnalyticsService } from '../../services/analytics/analytics.service';
import { AuthService } from '../../services/auth/auth.service';
import {
  UserAnalytics,
  AdminAnalytics,
} from '../../models/analytics/analytics.model';
import { MatToolbar } from '@angular/material/toolbar';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatIconModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatButtonModule,
    NgxChartsModule,
    NavbarComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  userAnalytics: UserAnalytics | null = null;
  adminAnalytics: AdminAnalytics | null = null;
  isAdmin = false;
  errorMessage: string | null = null;
  monthlyBlogTrends: any[] = [];

  private analyticsService = inject(AnalyticsService);
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    const role = this.authService.getCurrentUserRole();
    this.isAdmin = role === 'ADMIN';

    this.loadUserAnalytics();

    if (this.isAdmin) {
      this.loadAdminAnalytics();
      this.loadMonthlyBlogTrends();
    }
  }

  private loadUserAnalytics(): void {
    this.analyticsService.getUserAnalytics().subscribe({
      next: (data) => {
        this.userAnalytics = data;
      },
      error: (error) => {
        console.error('Failed to load user analytics:', error);
        this.handleError(error);
      },
    });
  }

  private loadAdminAnalytics(): void {
    this.analyticsService.getAdminAnalytics().subscribe({
      next: (data) => {
        this.adminAnalytics = data;
      },
      error: (error) => {
        console.error('Failed to load admin analytics:', error);
        this.errorMessage = 'Failed to load admin statistics';
      },
    });
  }

  goToBlogs(): void {
    this.router.navigate(['/blogs']);
  }

  goToFriends(): void {
    this.router.navigate(['/friends']);
  }
  private loadMonthlyBlogTrends(): void {
    this.analyticsService.getMonthlyBlogTrends().subscribe({
      next: (data) => {
        this.monthlyBlogTrends = data.map((trend: any) => ({
          name: trend.month,
          value: trend.count,
        }));
      },
      error: (error) => {
        console.error('Failed to load monthly blog trends:', error);
      },
    });
  }

  private handleError(error: any): void {
    if (error.status === 401) {
      this.authService.logout();
      this.router.navigate(['/login']);
    }
    this.errorMessage = 'Failed to load dashboard data';
  }
}
