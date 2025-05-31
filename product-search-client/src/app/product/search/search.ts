import { Component } from '@angular/core';
import { Subject, debounceTime, switchMap } from 'rxjs';
import { Product } from '../../models/product';
import { ProductService } from '../../service/product-service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search',
  imports: [CommonModule, FormsModule],
  templateUrl: './search.html',
  styleUrl: './search.css'
})
export class Search {

  searchText = '';
  results: Product[] = [];
  loading = false;
  selectedIndex = -1; // for keyboard nav if you want

  private searchSubject = new Subject<string>();

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.searchSubject.pipe(
      debounceTime(300),
      switchMap(query => {
        this.loading = true;
        return this.productService.search(query);
      })
    ).subscribe(products => {
      this.results = products;
      this.loading = false;
      this.selectedIndex = -1;
    });
  }

  onSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchText = input.value;
    this.searchSubject.next(this.searchText);
  }


}
