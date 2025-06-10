import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../services/admin.service';
import { User, Role } from '../types/user.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './user-management.html',
  styleUrls: ['./user-management.css']
})
export class UserManagement implements OnInit {
  users: User[] = [];
  allRoles: Role[] = [];
  selectedUser: User | null = null;
  userToEdit: User | null = null;
  userRolesMap: { [userId: number]: number[] } = {}; // For ngModel in multiselect

  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadUsers();
    this.loadRoles();
  }

  loadUsers(): void {
    this.adminService.getUsers().subscribe({
      next: (data) => {
        this.users = data;
        this.users.forEach(user => {
          this.userRolesMap[user.id] = user.roles.map(role => role.id);
        });
      },
      error: (err) => this.errorMessage = `Failed to load users: ${err.message}`
    });
  }

  loadRoles(): void {
    this.adminService.getRoles().subscribe({
      next: (data) => this.allRoles = data,
      error: (err) => this.errorMessage = `Failed to load roles: ${err.message}`
    });
  }

  viewUser(user: User): void {
    this.selectedUser = user;
    this.userToEdit = null; // Close edit form if open
  }

  closeViewUser(): void {
    this.selectedUser = null;
  }

  editUser(user: User): void {
    this.userToEdit = { ...user, roles: [...user.roles] }; // Create a copy for editing
     // Ensure userRolesMap is initialized for the user being edited
    if (!this.userRolesMap[user.id]) {
        this.userRolesMap[user.id] = user.roles.map(role => role.id);
    }
    this.selectedUser = null; // Close view modal if open
  }

  cancelEdit(): void {
    this.userToEdit = null;
  }

  saveUserChanges(): void {
    if (this.userToEdit) {
      const roleIds = this.userRolesMap[this.userToEdit.id];
      const updatedRoles = this.allRoles.filter(role => roleIds.includes(role.id));

      const updatePayload: Partial<User> = {
        // Include other fields that can be updated by admin if any
        // For now, focusing on roles
        roles: updatedRoles,
        // If username/email/enabled can be changed by admin, add them here:
        // username: this.userToEdit.username,
        // email: this.userToEdit.email,
        // enabled: this.userToEdit.enabled,
      };

      this.adminService.updateUser(this.userToEdit.id, updatePayload).subscribe({
        next: (updatedUser) => {
          this.successMessage = `User ${updatedUser.username} updated successfully.`;
          this.loadUsers(); // Refresh the list
          this.userToEdit = null;
        },
        error: (err) => this.errorMessage = `Failed to update user: ${err.message}`
      });
    }
  }

  deleteUser(userId: number): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.adminService.deleteUser(userId).subscribe({
        next: () => {
          this.successMessage = 'User deleted successfully.';
          this.loadUsers(); // Refresh the list
          if (this.selectedUser && this.selectedUser.id === userId) {
            this.selectedUser = null;
          }
          if (this.userToEdit && this.userToEdit.id === userId) {
            this.userToEdit = null;
          }
        },
        error: (err) => this.errorMessage = `Failed to delete user: ${err.message}`
      });
    }
  }

  getRoleNames(roles: Role[]): string {
    return roles.map(role => role.name.replace('ROLE_', '')).join(', ');
  }
}
