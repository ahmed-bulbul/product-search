import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard';
import { UserManagementComponent } from './user-management/user-management';
import { RoleManagementComponent } from './role-management/role-management';
import { PermissionManagementComponent } from './permission-management/permission-management';

export const ADMIN_ROUTES: Routes = [
  { path: '', component: DashboardComponent, pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'users', component: UserManagementComponent },
  { path: 'roles', component: RoleManagementComponent },
  { path: 'permissions', component: PermissionManagementComponent },
  // Add more admin routes here
];
