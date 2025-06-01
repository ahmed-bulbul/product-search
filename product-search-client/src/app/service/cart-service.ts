import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';

export interface CartItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private items: CartItem[] = [];
  private cartSubject = new BehaviorSubject<CartItem[]>([]);
  cart$ = this.cartSubject.asObservable();

  cartCount$ = this.cart$.pipe(
    map(items => items.reduce((total, item) => total + item.quantity, 0))
  );

  constructor() {
    const savedCart = localStorage.getItem('cartItems');
    if (savedCart) {
      this.items = JSON.parse(savedCart);
      this.cartSubject.next(this.items);
    }
  }

  private updateCartState() {
    this.cartSubject.next(this.items);
    localStorage.setItem('cartItems', JSON.stringify(this.items));
  }

  addToCart(item: CartItem) {
    const existing = this.items.find(i => i.id === item.id);
    if (existing) {
      existing.quantity++;
    } else {
      this.items.push({ ...item, quantity: 1 });
    }
    this.updateCartState();
  }

  removeFromCart(id: string) {
    this.items = this.items.filter(i => i.id !== id);
    this.updateCartState();
  }

  updateQuantity(id: string, change: number) {
    const item = this.items.find(i => i.id === id);
    if (item) {
      item.quantity += change;
      if (item.quantity <= 0) {
        this.removeFromCart(id);
      } else {
        this.updateCartState();
      }
    }
  }

  clearCart() {
    this.items = [];
    this.updateCartState();
  }
}
