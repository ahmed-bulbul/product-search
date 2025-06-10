import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  loginData = {
    username: '',
    password: ''
  };
  errorMessage: string | null = null;

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.authService.login(this.loginData).subscribe({
      next: () => {
        this.router.navigate(['/']); // Navigate to home or dashboard
      },
      error: (err) => {
        this.errorMessage = err.error?.message || err.message || 'Login failed';
        if (err.status === 0) {
            this.errorMessage = 'Could not connect to server. Please try again later.';
        } else if (err.status === 401) {
            this.errorMessage = 'Invalid username or password.';
        } else {
            this.errorMessage = `Login failed: ${err.error?.message || 'Unknown error'}`;
        }
        console.error(err);
      }
    });
  }
}
