import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { User, Role, Permission } from '../types/user.model'; // Added Permission

const API_ADMIN_URL = '/api/admin/';

@Injectable({
  providedIn: 'root' // Provide in root if used across admin module, or in AdminModule if specific
})
export class AdminService {

  constructor(private http: HttpClient) { }

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(API_ADMIN_URL + 'users')
      .pipe(catchError(this.handleError));
  }

  getUser(id: number): Observable<User> { // Changed id to number to match User interface
    return this.http.get<User>(API_ADMIN_URL + 'users/' + id)
      .pipe(catchError(this.handleError));
  }

  updateUser(id: number, userData: Partial<User>): Observable<User> { // Changed id to number
    return this.http.put<User>(API_ADMIN_URL + 'users/' + id, userData)
      .pipe(catchError(this.handleError));
  }

  deleteUser(id: number): Observable<any> { // Changed id to number
    return this.http.delete(API_ADMIN_URL + 'users/' + id)
      .pipe(catchError(this.handleError));
  }

  getRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(API_ADMIN_URL + 'roles')
      .pipe(catchError(this.handleError));
  }

  createRole(roleData: { name: string; permissionIds: number[] }): Observable<Role> {
    return this.http.post<Role>(API_ADMIN_URL + 'roles', roleData)
      .pipe(catchError(this.handleError));
  }

  updateRole(id: number, roleData: { name: string; permissionIds: number[] }): Observable<Role> {
    return this.http.put<Role>(API_ADMIN_URL + 'roles/' + id, roleData)
      .pipe(catchError(this.handleError));
  }

  deleteRole(id: number): Observable<any> {
    return this.http.delete(API_ADMIN_URL + 'roles/' + id)
      .pipe(catchError(this.handleError));
  }

  getPermissions(): Observable<Permission[]> {
    return this.http.get<Permission[]>(API_ADMIN_URL + 'permissions')
      .pipe(catchError(this.handleError));
  }

  createPermission(permissionData: { name: string }): Observable<Permission> {
    return this.http.post<Permission>(API_ADMIN_URL + 'permissions', permissionData)
      .pipe(catchError(this.handleError));
  }

  deletePermission(id: number): Observable<any> {
    return this.http.delete(API_ADMIN_URL + 'permissions/' + id)
      .pipe(catchError(this.handleError));
  }

  // Basic error handler
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Unknown error!';
    if (error.error instanceof ErrorEvent) {
      // Client-side errors
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Server-side errors
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      if (error.error && typeof error.error === 'string') {
        errorMessage += `\nServer Error: ${error.error}`;
      } else if (error.error && error.error.message) {
        errorMessage += `\nServer Error: ${error.error.message}`;
      }
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}
