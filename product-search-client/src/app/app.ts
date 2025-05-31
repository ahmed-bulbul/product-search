import { Component } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterLinkActive, RouterModule, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
    imports: [RouterOutlet, ReactiveFormsModule,RouterModule ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'product-search-client';
}
