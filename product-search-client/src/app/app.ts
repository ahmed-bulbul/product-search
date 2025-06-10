import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NavigationEnd, Router, RouterLinkActive, RouterModule, RouterOutlet } from '@angular/router';
import { CartService } from './service/cart-service';
import { CartBoxComponent } from './cart/cart-box-component/cart-box-component';
import { CommonModule } from '@angular/common';
import { filter, Observable } from 'rxjs';
import { AuthService } from './service/auth.service'; // Import AuthService

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ReactiveFormsModule,RouterModule,CartBoxComponent,CommonModule,FormsModule ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{
  protected title = 'product-search-client';
  isLoggedIn$: Observable<boolean>; // Observable for login status

  @ViewChild('cartBox') cartBox?: CartBoxComponent;

  isAdminPanel = false;

  toggleCart() {
    this.cartBox?.toggleCart();
  }


  cartCount = 0;

  constructor(
    private cartService: CartService,
    private router: Router,
    private authService: AuthService // Inject AuthService
  ) {
    this.isLoggedIn$ = this.authService.isLoggedIn(); // Initialize isLoggedIn$
  }

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
    this.authService.logout(); // Use AuthService for logout
    this.router.navigate(['/login']); // Navigate to login page
  }

}
