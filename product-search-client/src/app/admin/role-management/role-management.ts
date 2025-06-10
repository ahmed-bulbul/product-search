import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../services/admin.service';
import { Role, Permission } from '../types/user.model';

@Component({
  selector: 'app-role-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './role-management.html',
  styleUrls: ['./role-management.css']
})
export class RoleManagement implements OnInit {
  roles: Role[] = [];
  allPermissions: Permission[] = [];

  showCreateForm = false;
  newRoleData: { name: string; permissionIds: number[] } = { name: '', permissionIds: [] };

  roleToEdit: Role | null = null;
  editRoleData: { name: string; permissionIds: number[] } = { name: '', permissionIds: [] };

  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private adminService: AdminService) { }

  ngOnInit(): void {
    this.loadRoles();
    this.loadPermissions();
  }

  loadRoles(): void {
    this.adminService.getRoles().subscribe({
      next: (data) => this.roles = data,
      error: (err) => this.errorMessage = `Failed to load roles: ${err.message}`
    });
  }

  loadPermissions(): void {
    this.adminService.getPermissions().subscribe({
      next: (data) => this.allPermissions = data,
      error: (err) => this.errorMessage = `Failed to load permissions: ${err.message}`
    });
  }

  toggleCreateForm(): void {
    this.showCreateForm = !this.showCreateForm;
    this.newRoleData = { name: '', permissionIds: [] }; // Reset form
    this.roleToEdit = null; // Ensure edit form is hidden
    this.errorMessage = null;
    this.successMessage = null;
  }

  createRole(): void {
    if (!this.newRoleData.name) {
      this.errorMessage = "Role name is required.";
      return;
    }
    this.adminService.createRole(this.newRoleData).subscribe({
      next: (role) => {
        this.successMessage = `Role "${role.name}" created successfully.`;
        this.loadRoles();
        this.toggleCreateForm();
      },
      error: (err) => this.errorMessage = `Failed to create role: ${err.message}`
    });
  }

  editRole(role: Role): void {
    this.roleToEdit = { ...role };
    this.editRoleData = {
      name: role.name,
      permissionIds: role.permissions ? role.permissions.map(p => p.id) : []
    };
    this.showCreateForm = false; // Ensure create form is hidden
    this.errorMessage = null;
    this.successMessage = null;
  }

  cancelEdit(): void {
    this.roleToEdit = null;
  }

  updateRole(): void {
    if (this.roleToEdit && this.editRoleData.name) {
      this.adminService.updateRole(this.roleToEdit.id, this.editRoleData).subscribe({
        next: (updatedRole) => {
          this.successMessage = `Role "${updatedRole.name}" updated successfully.`;
          this.loadRoles();
          this.roleToEdit = null;
        },
        error: (err) => this.errorMessage = `Failed to update role: ${err.message}`
      });
    } else {
      this.errorMessage = "Role name cannot be empty for update.";
    }
  }

  deleteRole(roleId: number): void {
    if (confirm('Are you sure you want to delete this role? This might affect users assigned to this role.')) {
      this.adminService.deleteRole(roleId).subscribe({
        next: () => {
          this.successMessage = 'Role deleted successfully.';
          this.loadRoles();
          if (this.roleToEdit && this.roleToEdit.id === roleId) {
            this.roleToEdit = null;
          }
        },
        error: (err) => this.errorMessage = `Failed to delete role: ${err.message}`
      });
    }
  }

  getPermissionNames(permissions: Permission[] | undefined): string {
    if (!permissions || permissions.length === 0) {
      return 'No permissions';
    }
    return permissions.map(p => p.name).join(', ');
  }

  onPermissionChange(permissionId: number, event: Event, formType: 'new' | 'edit'): void {
    const inputElement = event.target as HTMLInputElement;
    const isChecked = inputElement.checked;

    let targetPermissionIds: number[];

    if (formType === 'new') {
      targetPermissionIds = this.newRoleData.permissionIds;
    } else if (formType === 'edit' && this.roleToEdit) {
      targetPermissionIds = this.editRoleData.permissionIds;
    } else {
      return; // Should not happen
    }

    if (isChecked) {
      if (!targetPermissionIds.includes(permissionId)) {
        targetPermissionIds.push(permissionId);
      }
    } else {
      const index = targetPermissionIds.indexOf(permissionId);
      if (index > -1) {
        targetPermissionIds.splice(index, 1);
      }
    }
  }
}
