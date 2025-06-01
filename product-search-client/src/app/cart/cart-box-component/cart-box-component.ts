import { Component } from '@angular/core';
import { CartItem, CartService } from '../../service/cart-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cart-box-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './cart-box-component.html',
  styleUrl: './cart-box-component.css'
})
export class CartBoxComponent {

   cartItems: CartItem[] = [];
  show = false;

  constructor(private cartService: CartService) {
    this.cartService.cart$.subscribe(items => this.cartItems = items);
  }

  changeQty(id: string, change: number) {
    this.cartService.updateQuantity(id, change);
  }

  toggleCart() {
    this.show = !this.show;
  }

  get totalPrice(): string {
    const total = this.cartItems.reduce((acc, i) => acc + i.price * i.quantity, 0);
    return total.toFixed(2);
  }

  checkout(){}

  clearCart() {
    this.cartService.clearCart();
  }
}
