import { Component, OnInit, inject, TemplateRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { User } from './user.model';
import { Role } from './role.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatSnackBarModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonToggleModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly snackBar = inject(MatSnackBar);
  private readonly dialog = inject(MatDialog);
  private readonly cdr = inject(ChangeDetectorRef);

  @ViewChild('userFormDialog') userFormDialog!: TemplateRef<unknown>;
  @ViewChild('roleFormDialog') roleFormDialog!: TemplateRef<unknown>;

  // Navigation state
  activeView: 'users' | 'roles' = 'users';

  // User state
  users: User[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'actions'];
  errorMessage: string | null = null;

  // User form state
  formName = '';
  formEmail = '';
  formNameError = '';
  formEmailError = '';
  editingUserId: number | null = null;
  isSubmitting = false;

  private dialogRef: MatDialogRef<unknown> | null = null;

  // Role state
  roles: Role[] = [];
  roleDisplayedColumns: string[] = ['id', 'name', 'actions'];

  // Role form state
  roleFormName = '';
  roleFormNameError = '';
  editingRoleId: number | null = null;
  isRoleSubmitting = false;

  private roleDialogRef: MatDialogRef<unknown> | null = null;

  ngOnInit(): void {
    this.loadUsers();
  }

  switchView(view: 'users' | 'roles'): void {
    this.activeView = view;
    if (view === 'users') {
      this.loadUsers();
    } else {
      this.loadRoles();
    }
  }

  loadUsers(): void {
    this.errorMessage = null;
    this.http.get<User[]>('/users').subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Failed to load users';
        this.showError(this.errorMessage);
      }
    });
  }

  loadRoles(): void {
    this.errorMessage = null;
    this.http.get<Role[]>('/roles').subscribe({
      next: (roles) => {
        this.roles = roles;
      },
      error: (err: HttpErrorResponse) => {
        this.errorMessage = err.message || 'Failed to load roles';
        this.showError(this.errorMessage);
      }
    });
  }

  // Role CRUD methods
  openRoleCreateDialog(): void {
    this.resetRoleForm();
    this.editingRoleId = null;
    this.roleDialogRef = this.dialog.open(this.roleFormDialog, {
      width: '400px',
      disableClose: true
    });
  }

  openRoleEditDialog(role: Role): void {
    this.resetRoleForm();
    this.editingRoleId = role.id;
    this.roleFormName = role.name;
    this.roleDialogRef = this.dialog.open(this.roleFormDialog, {
      width: '400px',
      disableClose: true
    });
  }

  onRoleSubmit(): void {
    if (!this.validateRoleForm()) {
      return;
    }

    if (this.isRoleSubmitting) {
      return;
    }

    this.isRoleSubmitting = true;
    this.cdr.detectChanges();
    const roleData = { name: this.roleFormName.trim() };

    if (this.editingRoleId !== null) {
      this.updateRole(this.editingRoleId, roleData);
    } else {
      this.createRole(roleData);
    }
  }

  private createRole(roleData: { name: string }): void {
    this.http.post<Role>('/roles', roleData).subscribe({
      next: () => {
        this.closeRoleDialog();
        this.showSuccess('Role created');
        this.loadRoles();
        this.isRoleSubmitting = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isRoleSubmitting = false;
        const message = err.error?.error || err.error?.message || err.message || 'Failed to create role';
        this.showError(message);
      }
    });
  }

  private updateRole(id: number, roleData: { name: string }): void {
    this.http.put<Role>(`/roles/${id}`, roleData).subscribe({
      next: () => {
        this.closeRoleDialog();
        this.showSuccess('Role updated');
        this.loadRoles();
        this.isRoleSubmitting = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isRoleSubmitting = false;
        if (err.status === 404) {
          this.showError('Role not found');
          this.closeRoleDialog();
          this.loadRoles();
        } else {
          const message = err.error?.error || err.error?.message || err.message || 'Failed to update role';
          this.showError(message);
        }
      }
    });
  }

  deleteRole(roleId: number): void {
    if (!confirm('Delete this role?')) {
      return;
    }

    this.http.delete<void>(`/roles/${roleId}`).subscribe({
      next: () => {
        this.showSuccess('Role deleted');
        this.loadRoles();
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          this.showError('Role not found');
          this.loadRoles();
        } else {
          const message = err.error?.error || err.error?.message || err.message || 'Failed to delete role';
          this.showError(message);
        }
      }
    });
  }

  onRoleCancel(): void {
    this.closeRoleDialog();
  }

  private closeRoleDialog(): void {
    if (this.roleDialogRef) {
      this.roleDialogRef.close();
      this.roleDialogRef = null;
    }
    this.resetRoleForm();
  }

  private resetRoleForm(): void {
    this.roleFormName = '';
    this.roleFormNameError = '';
    this.isRoleSubmitting = false;
  }

  private validateRoleForm(): boolean {
    this.roleFormNameError = '';

    if (!this.roleFormName.trim()) {
      this.roleFormNameError = 'Name is required';
      this.cdr.detectChanges();
      return false;
    }

    return true;
  }

  openCreateDialog(): void {
    this.resetForm();
    this.editingUserId = null;
    this.dialogRef = this.dialog.open(this.userFormDialog, {
      width: '400px',
      disableClose: true
    });
  }

  openEditDialog(user: User): void {
    this.resetForm();
    this.editingUserId = user.id;
    this.formName = user.name;
    this.formEmail = user.email;
    this.dialogRef = this.dialog.open(this.userFormDialog, {
      width: '400px',
      disableClose: true
    });
  }

  onSubmit(): void {
    if (!this.validateForm()) {
      return;
    }

    if (this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.cdr.detectChanges();
    const userData = { name: this.formName.trim(), email: this.formEmail.trim() };

    if (this.editingUserId !== null) {
      this.updateUser(this.editingUserId, userData);
    } else {
      this.createUser(userData);
    }
  }

  private createUser(userData: { name: string; email: string }): void {
    this.http.post<User>('/users', userData).subscribe({
      next: () => {
        this.closeDialog();
        this.showSuccess('User created');
        this.loadUsers();
        this.isSubmitting = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting = false;
        const message = err.error?.error || err.error?.message || err.message || 'Failed to create user';
        this.showError(message);
      }
    });
  }

  private updateUser(id: number, userData: { name: string; email: string }): void {
    this.http.put<User>(`/users/${id}`, userData).subscribe({
      next: () => {
        this.closeDialog();
        this.showSuccess('User updated');
        this.loadUsers();
        this.isSubmitting = false;
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting = false;
        if (err.status === 404) {
          this.showError('User not found');
          this.closeDialog();
          this.loadUsers();
        } else {
          const message = err.error?.error || err.error?.message || err.message || 'Failed to update user';
          this.showError(message);
        }
      }
    });
  }

  deleteUser(userId: number): void {
    if (!confirm('Delete this user?')) {
      return;
    }

    this.http.delete<void>(`/users/${userId}`).subscribe({
      next: () => {
        this.showSuccess('User deleted');
        this.loadUsers();
      },
      error: (err: HttpErrorResponse) => {
        if (err.status === 404) {
          this.showError('User not found');
          this.loadUsers();
        } else {
          const message = err.error?.error || err.error?.message || err.message || 'Failed to delete user';
          this.showError(message);
        }
      }
    });
  }

  onCancel(): void {
    this.closeDialog();
  }

  private closeDialog(): void {
    if (this.dialogRef) {
      this.dialogRef.close();
      this.dialogRef = null;
    }
    this.resetForm();
  }

  private resetForm(): void {
    this.formName = '';
    this.formEmail = '';
    this.formNameError = '';
    this.formEmailError = '';
    this.isSubmitting = false;
  }

  private validateForm(): boolean {
    let isValid = true;

    this.formNameError = '';
    this.formEmailError = '';

    if (!this.formName.trim()) {
      this.formNameError = 'Name is required';
      isValid = false;
    }

    if (!this.formEmail.trim()) {
      this.formEmailError = 'Email is required';
      isValid = false;
    }

    this.cdr.detectChanges();
    return isValid;
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom'
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Close', {
      duration: 5000,
      horizontalPosition: 'center',
      verticalPosition: 'bottom',
      panelClass: ['error-snackbar']
    });
  }
}
