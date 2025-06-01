import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterLinkActive, RouterModule, RouterOutlet } from '@angular/router';
import { CartService } from './service/cart-service';
import { CartBoxComponent } from './cart/cart-box-component/cart-box-component';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ReactiveFormsModule,RouterModule,CartBoxComponent,CommonModule,FormsModule ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{
  protected title = 'product-search-client';

  @ViewChild('cartBox') cartBox?: CartBoxComponent;

  isAdminPanel = false;

  toggleCart() {
    this.cartBox?.toggleCart();
  }


  cartCount = 0;

  constructor(private cartService: CartService,private router: Router) {}

  ngOnInit(): void {
    this.cartService.cartCount$.subscribe(count => {
      this.cartCount = count;
    });

    // Track URL changes
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        this.isAdminPanel = event.urlAfterRedirects.includes('admin');
      });
  
  }

  isSidebarCollapsed = false;

  toggleSidebar() {
    this.isSidebarCollapsed = !this.isSidebarCollapsed;
  }

  logout() {
    // Clear token and redirect to login
    localStorage.clear();
    location.href = '/login';
  }

}
