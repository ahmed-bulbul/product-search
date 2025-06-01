import { Injectable } from '@angular/core';
import { BehaviorSubject, map } from 'rxjs';
import { Product } from '../models/product';


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


  // Add this derived observable
  cartCount$ = this.cart$.pipe(
    map(items => items.reduce((total, item) => total + item.quantity, 0))
  );

  addToCart(item: CartItem) {
    const existing = this.items.find(i => i.id === item.id);
    if (existing) {
      existing.quantity++;
    } else {
      this.items.push({ ...item, quantity: 1 });
    }
    this.cartSubject.next(this.items);
  }

  removeFromCart(id: string) {
    this.items = this.items.filter(i => i.id !== id);
    this.cartSubject.next(this.items);
  }

  updateQuantity(id: string, change: number) {
    const item = this.items.find(i => i.id === id);
    if (item) {
      item.quantity += change;
      if (item.quantity <= 0) this.removeFromCart(id);
    }
    this.cartSubject.next(this.items);
  }

  clearCart() {
    this.items = [];
    this.cartSubject.next(this.items);
  }

  
}
