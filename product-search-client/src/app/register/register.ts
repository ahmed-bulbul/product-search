import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.css']
})
export class RegisterComponent {
  registerData = {
    username: '',
    email: '',
    password: ''
  };
  errorMessage: string | null = null;
  successMessage: string | null = null;

  constructor(private authService: AuthService, private router: Router) { }

  onSubmit() {
    this.errorMessage = null;
    this.successMessage = null;
    this.authService.register(this.registerData).subscribe({
      next: (response) => {
        this.successMessage = 'Registration successful! You will be redirected to login.';
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 2000);
      },
      error: (err) => {
        this.errorMessage = err.error || err.message || 'Registration failed';
         if (err.status === 0) {
            this.errorMessage = 'Could not connect to server. Please try again later.';
        } else if (err.status === 400) {
            this.errorMessage = err.error || 'Registration failed due to validation errors.';
        }
        else {
            this.errorMessage = `Registration failed: ${err.error || 'Unknown error'}`;
        }
        console.error(err);
      }
    });
  }
}
