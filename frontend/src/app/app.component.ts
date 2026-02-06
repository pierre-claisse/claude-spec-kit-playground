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
import { User } from './user.model';

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
    MatInputModule
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

  users: User[] = [];
  displayedColumns: string[] = ['id', 'name', 'email', 'actions'];
  errorMessage: string | null = null;

  // Form state
  formName = '';
  formEmail = '';
  formNameError = '';
  formEmailError = '';
  editingUserId: number | null = null;
  isSubmitting = false;

  private dialogRef: MatDialogRef<unknown> | null = null;

  ngOnInit(): void {
    this.loadUsers();
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
