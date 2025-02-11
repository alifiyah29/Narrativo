import { Blog } from '../blog/blog.model';

export interface UserAnalytics {
  totalBlogs: number;
  totalViews: number;
  lastLogin: string;
}

export interface AdminAnalytics {
  totalUsers: number;
  totalBlogs: number;
  totalViews: number;
  recentActivity: Blog[];
}
