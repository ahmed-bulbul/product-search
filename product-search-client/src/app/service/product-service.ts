import { Injectable } from '@angular/core';
import { Product } from '../models/product';
import { Observable, of, delay } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private mockProducts: Product[] = [
    { id: '1', name: 'iPhone 15', description: 'Apple smartphone', price: 999, rating: 4.8, brand: { id: 'b1', name: 'Apple' } },
    { id: '2', name: 'Galaxy S24', description: 'Samsung flagship', price: 899, rating: 4.6, brand: { id: 'b2', name: 'Samsung' } },
    { id: '3', name: 'Pixel 8 Pro', description: 'Google Pixel', price: 799, rating: 4.5, brand: { id: 'b3', name: 'Google' } },
    { id: '4', name: 'iPad Air', description: 'Apple tablet', price: 699, rating: 4.4, brand: { id: 'b1', name: 'Apple' } },
  ];

  constructor() { }


  search(query: string): Observable<Product[]> {
    const result = this.mockProducts.filter(p =>
      p.name.toLowerCase().includes(query.toLowerCase())
    );
    return of(result).pipe(delay(300)); // Simulate network delay
  }

  getBrands(): Observable<string[]> {
    return of(['Apple', 'Samsung', 'Google']);
  }
}
