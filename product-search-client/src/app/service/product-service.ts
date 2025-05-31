import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Product } from '../models/product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private http = inject(HttpClient); // ✅ Use Angular's inject()

  private baseUrl = 'http://localhost:8080/api/v1/products';

  /**
   * Real-time product search (auto-suggest)
   */
  search(query: string): Observable<Product[]> {
    if (!query?.trim()) {
      return of([]);
    }

    const params = new HttpParams().set('name', query.trim());
    return this.http.get<Product[]>(`${this.baseUrl}/search`, { params });
  }

  /**
   * Paginated fetch of all products
   */
  getAll(page: number = 0, size: number = 10): Observable<{ content: Product[] }> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<{ content: Product[] }>(this.baseUrl, { params });
  }

  /**
   * Mock brand list
   */
  getBrands(): Observable<string[]> {
    return of(['Apple', 'Samsung', 'Google']);
  }
}
