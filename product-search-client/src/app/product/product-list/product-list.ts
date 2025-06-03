import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject, Subject } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';
import { ProductService } from '../../service/product-service';
import { CartService } from '../../service/cart-service';
import { Product, PageResponse } from '../../models/product';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.css']
})
export class ProductList implements OnInit {
  private productsSubject = new BehaviorSubject<Product[]>([]);
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private pageInfoSubject = new BehaviorSubject<{
    currentPage: number;
    totalPages: number;
    pageSize: number;
  }>({ currentPage: 0, totalPages: 0, pageSize: 10 });
  
  // Expose as observables
  products$ = this.productsSubject.asObservable();
  loading$ = this.loadingSubject.asObservable();
  pageInfo$ = this.pageInfoSubject.asObservable();
  
  // Other properties
  searchText = '';
  results: Product[] = [];
  selectedIndex = -1;
  private searchSubject = new Subject<string>();
  addedProductIds = new Set<string>();

  

  constructor(
    private productIndexService: ProductService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadPage(0); // Start with page 0 (API uses 0-based indexing)

    this.searchSubject.pipe(
      debounceTime(300),
      switchMap(query => {
        this.loadingSubject.next(true);
        return this.productIndexService.search(query);
      })
    ).subscribe(products => {
      this.results = products;
      this.loadingSubject.next(false);
      this.selectedIndex = -1;
    });
  }

  loadPage(pageNumber: number, size: number = 100) {
    console.log('Loading page ' + pageNumber);
    this.loadingSubject.next(true);
    
    this.productIndexService.getAll(pageNumber, size).subscribe((response: PageResponse<Product>) => {
      this.productsSubject.next(response.content);
      
      this.pageInfoSubject.next({
        currentPage: response.page,
        totalPages: response.totalPages,
        pageSize: response.size
      });
      
      this.loadingSubject.next(false);
    }, error => {
      console.error('Error loading products:', error);
      this.loadingSubject.next(false);
    });
  }

  nextPage() {
    const currentPage = this.pageInfoSubject.getValue().currentPage;
    const totalPages = this.pageInfoSubject.getValue().totalPages;
    
    if (currentPage < totalPages - 1) {
      this.loadPage(currentPage + 1);
    }
  }

  prevPage() {
    const currentPage = this.pageInfoSubject.getValue().currentPage;
    
    if (currentPage > 0) {
      this.loadPage(currentPage - 1);
    }
  }

  setPage(page: number) {
    this.loadPage(page - 1); // API uses zero-based indexing, but UI typically uses 1-based
  }

  getPageArray(): number[] {
    const totalPages = this.pageInfoSubject.getValue().totalPages;
    return Array.from({ length: totalPages }, (_, i) => i + 1);
  }

  trackById(index: number, product: Product) {
    return product.id;
  }

  onSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchText = input.value;
    if (!this.searchText) this.results = [];
    this.searchSubject.next(this.searchText);
  }

  productDetails(id: string) {
    this.router.navigate(['/products/', id]);
  }

  performSearch() {
    alert(this.searchText);
  }

  addToCart(product: Product) {

  const imageUrl = product.images?.length ? product.images[0] : 'default-image.jpg';
  this.cartService.addToCart({
    id: product.id,
    name: product.name,
    price: product.price,
    imageUrl: imageUrl, 
    quantity: 1,
  });

  this.addedProductIds.add(product.id);
}

  
  // Helper method to convert string rating to number
  getRatingAsNumber(rating: string | null): number {
    return rating ? parseFloat(rating) : 0;
  }

  navigateToOffers(): void {
  //this.router.navigate(['/offers']); // or your desired route
}

getProduct(id:string){
  this.router.navigate(['/products/', id]);
}


}
