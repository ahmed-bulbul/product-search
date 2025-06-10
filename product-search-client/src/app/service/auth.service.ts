import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';

const API_URL = '/api/auth/'; // Adjust if your backend URL is different

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedIn = new BehaviorSubject<boolean>(this.hasToken());

  constructor(private http: HttpClient) { }

  private hasToken(): boolean {
    return !!localStorage.getItem('authToken');
  }

  login(credentials: any): Observable<any> {
    return this.http.post<any>(API_URL + 'login', credentials).pipe(
      tap(response => {
        if (response && response.token) {
          localStorage.setItem('authToken', response.token);
          this.loggedIn.next(true);
        }
      })
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post<any>(API_URL + 'register', userData);
  }

  logout(): void {
    localStorage.removeItem('authToken');
    this.loggedIn.next(false);
    // Optionally, navigate to login or home page
    // this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }

  isLoggedIn(): Observable<boolean> {
    return this.loggedIn.asObservable();
  }

  getLoggedInSubject(): BehaviorSubject<boolean> {
    return this.loggedIn;
  }
}
