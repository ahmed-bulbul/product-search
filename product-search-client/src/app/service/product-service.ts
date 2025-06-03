import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Category, PageResponse, Product } from '../models/product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private http = inject(HttpClient);
  private baseUrl = 'http://localhost:8080/api/v1/productIndex';
  private categoryUrl = 'http://localhost:8080/api/v1/categories';

  search(query: string): Observable<Product[]> {
    if (!query?.trim()) {
      return of([]);
    }

    const params = new HttpParams().set('name', query.trim());
    return this.http.get<Product[]>(`${this.baseUrl}/search`, { params });
  }

  getAll(page: number = 0, size: number = 10): Observable<PageResponse<Product>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Product>>(this.baseUrl, { params });
  }

  findById(id: string): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  /**
   * Mock brand list
   */
  getBrands(): Observable<string[]> {
    return of(['Apple', 'Samsung', 'Google']);
  }

  /**
   * Mock category list
   */
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryUrl);
  }
}
