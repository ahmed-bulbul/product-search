import { Component, OnInit, ViewChild } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLinkActive, RouterModule, RouterOutlet } from '@angular/router';
import { CartService } from './service/cart-service';
import { CartBoxComponent } from './cart/cart-box-component/cart-box-component';

@Component({
  selector: 'app-root',
    imports: [RouterOutlet, ReactiveFormsModule,RouterModule,CartBoxComponent ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{
  protected title = 'product-search-client';

  @ViewChild('cartBox') cartBox?: CartBoxComponent;

  toggleCart() {
    this.cartBox?.toggleCart();
  }


  cartCount = 0;

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.cartService.cartCount$.subscribe(count => {
      this.cartCount = count;
    });
  }

}
