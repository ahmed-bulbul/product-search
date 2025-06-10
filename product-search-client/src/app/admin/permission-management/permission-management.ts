import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { AdminService } from '../services/admin.service';
import { Permission } from '../types/user.model';

@Component({
  selector: 'app-permission-management',
  standalone: true,
  imports: [CommonModule, FormsModule], // Add FormsModule
  templateUrl: './permission-management.html',
  styleUrls: ['./permission-management.css']
})
export class PermissionManagement implements OnInit {
  permissions: Permission[] = [];
  showCreateForm = false;
  newPermissionData: { name: string } = { name: '' };

  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadPermissions();
  }

  loadPermissions(): void {
    this.adminService.getPermissions().subscribe({
      next: (data) => this.permissions = data,
      error: (err) => this.errorMessage = `Failed to load permissions: ${err.message}`
    });
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    this.newPermissionData = { name: '' }; // Reset form
    this.errorMessage = null;
    this.successMessage = null;
  }

  createPermission(): void {
    if (!this.newPermissionData.name) {
      this.errorMessage = "Permission name is required.";
      return;
    }
    this.adminService.createPermission(this.newPermissionData).subscribe({
      next: (permission) => {
        this.successMessage = `Permission "${permission.name}" created successfully.`;
        this.loadPermissions();
        this.toggleCreateForm();
      },
      error: (err) => this.errorMessage = `Failed to create permission: ${err.message}`
    });
  }

  deletePermission(permissionId: number): void {
    if (confirm('Are you sure you want to delete this permission? This might affect roles that use this permission.')) {
      this.adminService.deletePermission(permissionId).subscribe({
        next: () => {
          this.successMessage = 'Permission deleted successfully.';
          this.loadPermissions();
        },
        error: (err) => this.errorMessage = `Failed to delete permission: ${err.message}`
      });
    }
  }
}
