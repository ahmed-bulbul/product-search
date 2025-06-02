import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProductService } from '../../service/product-service';
import { Product } from '../../models/product';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { CartItem, CartService } from '../../service/cart-service';

@Component({
  selector: 'app-product-details-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './product-details-component.html',
  styleUrl: './product-details-component.css'
})
export class ProductDetailsComponent implements OnInit {

  cartItems: CartItem[] = [];

  product= {
   id: '',
    name: '',
    description: '',
    price: 0,
    images: [] ,
    rating: ''
  }

  loading = true;
  error = '';
  quantity = 1;
  activeTab: 'desc' | 'reviews' = 'desc';

 private productSubject = new BehaviorSubject<Product | null>(null);

  // Observable for template or other consumers
  product$ = this.productSubject.asObservable();

  constructor(private productService: ProductService, private route: ActivatedRoute, private cartService: CartService,) {
     this.cartService.cart$.subscribe(items => this.cartItems = items);
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.productService.findById(id).subscribe({
        next: product => {
          this.productSubject.next(product);
          this.loading = false;
        },
        error: err => {
          this.error = 'Failed to load product details. Please try again later.';
          this.loading = false;
        }
      });
    }
  }

  changeQuantity(delta: number) {
    const id = this.route.snapshot.paramMap.get('id');
    const newQuantity = this.quantity + delta;
    if (newQuantity > 0) {
      this.quantity = newQuantity;
      this.cartService.updateQuantity(id!, this.quantity);
    }

    
  }

  setTab(tab: 'desc' | 'reviews') {
    this.activeTab = tab;
  }

  addToCart(product: Product | null) {
    console.log('quantity', this.quantity);
    if (product) {
      console.log(`Add to cart: ${product.name} x${this.quantity}`);
      // Add your cart logic here
      this.cartService.addToCart({
      id: product.id,
      name: product.name,
      price: product.price,
      imageUrl: product.images?.length ? product.images[0] : 'default-image.jpg', 
      quantity: this.quantity,
  });
    }
  }

  buyNow(product: Product | null) {
    if (product) {
      console.log(`Buy now: ${product.name} x${this.quantity}`);
      // Add your buy now logic here
    }
  }
}