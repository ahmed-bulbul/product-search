import { Component, OnInit } from '@angular/core';
import { CartItem, CartService } from '../../service/cart-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-checkout-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './checkout-component.html',
  styleUrl: './checkout-component.css'
})
export class CheckoutComponent {

  cartItems: CartItem[] = [];
  shipping = {
    fullName: '',
    email: '',
    address: '',
    city: '',
    postalCode: '',
    country: ''
  };

  constructor(private cartService: CartService, private router: Router) {
    this.cartService.cart$.subscribe(items => this.cartItems = items);
  }

  get totalPrice(): string {
    const total = this.cartItems.reduce((acc, i) => acc + i.price * i.quantity, 0);
    return total.toFixed(2);
  }

  submitOrder() {
    // Here you can handle the form submission,
    // e.g., send data to backend API or show confirmation

    if (this.cartItems.length === 0) {
      alert('Your cart is empty!');
      return;
    }

    // Basic example: just log the info
    console.log('Order Submitted:');
    console.log('Shipping Info:', this.shipping);
    console.log('Cart Items:', this.cartItems);

    alert('Order placed successfully! Thank you for your purchase.');

    // Optionally clear the cart after order
    this.cartService.clearCart();

    // Reset shipping form (optional)
    this.shipping = {
      fullName: '',
      email: '',
      address: '',
      city: '',
      postalCode: '',
      country: ''
    };

    this.router.navigate(['/']);  // navigate to checkout page


  }
}