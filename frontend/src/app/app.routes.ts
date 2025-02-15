import { Routes } from '@angular/router';
import { authGuard } from './guards/auth/auth.guard';
import { NavbarComponent } from './components/navbar/navbar.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  {
    path: 'register',
    loadComponent: () =>
      import('./components/register/register.component').then(
        (m) => m.RegisterComponent
      ),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./components/login/login.component').then(
        (m) => m.LoginComponent
      ),
  },
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./components/dashboard/dashboard.component').then(
        (m) => m.DashboardComponent
      ),
    canActivate: [authGuard],
  },
  {
    path: 'blogs',
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./components/blog/blog-list/blog-list.component').then(
            (m) => m.BlogListComponent
          ),
      },
      {
        path: 'new',
        loadComponent: () =>
          import('./components/blog/blog-editor/blog-editor.component').then(
            (m) => m.BlogEditorComponent
          ),
      },
      {
        path: ':id/edit',
        loadComponent: () =>
          import('./components/blog/blog-editor/blog-editor.component').then(
            (m) => m.BlogEditorComponent
          ),
      },
    ],
    canActivate: [authGuard],
  },
];