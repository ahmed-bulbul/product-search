import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-contact-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './contact-component.html',
  styleUrl: './contact-component.css'
})
export class ContactComponent {

  


contact = {
    name: '',
    email: '',
    subject: '',
    message: '',
  };

  submitted = false;
  submissionSuccess = false;

  submitContactForm() {
    this.submitted = true;

    // Basic front-end validation check
    if (
      !this.contact.name.trim() ||
      !this.validateEmail(this.contact.email) ||
      !this.contact.subject.trim() ||
      !this.contact.message.trim()
    ) {
      return; // Stop submission if invalid
    }

    // Here you would call your backend API or service
    // For demo: simulate successful submission with timeout
    setTimeout(() => {
      this.submissionSuccess = true;
      this.resetForm();
    }, 1000);
  }

  resetForm() {
    this.contact = {
      name: '',
      email: '',
      subject: '',
      message: '',
    };
    this.submitted = false;

    // Optionally reset success message after some time
    setTimeout(() => {
      this.submissionSuccess = false;
    }, 5000);
  }

  validateEmail(email: string): boolean {
    // Simple email regex validation
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email.toLowerCase());
  }
}
