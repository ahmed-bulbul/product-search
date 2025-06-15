import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { debounceTime, switchMap } from 'rxjs/operators';
import { ProductService } from '../../service/product-service';
import { CartService } from '../../service/cart-service';
import { Product, PageResponse, Category } from '../../models/product';
import { Router } from '@angular/router';
import { HttpParams } from '@angular/common/http';

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
  
  // Mock category list
  categories: Category[] = [];

  

  constructor(
    private productIndexService: ProductService,
    private cartService: CartService,
    private router: Router,
    private productService: ProductService
  ) {}

  ngOnInit() {
    this.loadPage(0); // Start with page 0 (API uses 0-based indexing)

    this.searchSubject.pipe(
      debounceTime(300),
      switchMap(query => {
        this.loadingSubject.next(true);
        //return this.productIndexService.search(query);
        //querty set as name param
        return this.productIndexService.searchByMapParams(new HttpParams().set('name', query)); 
      })
    ).subscribe(products => {
      this.results = products.content;
      this.loadingSubject.next(false);
      this.selectedIndex = -1;
    });

    this.productService.getCategories().subscribe(categories => {
      this.categories = categories;
    });
  }

  loadPage(pageNumber: number, size: number = 50) {
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

    console.log('Current page:', currentPage);
    console.log('Number of selected categories:', this.selectedCategories.length);
    
    if (currentPage < totalPages - 1) {


      if(this.selectedCategories.length > 0) {

        const params = new HttpParams()
        .set('categoryNames', this.selectedCategories.join(','))
        .set('page', currentPage+1)
        .set('size', 50);
        this.productService.searchByMapParams(params).subscribe(products => {
          this.productsSubject.next(products.content);
          this.pageInfoSubject.next({
            currentPage: products.page,
            totalPages: products.totalPages,
            pageSize: products.size
          })
        });
      }else{
        this.loadPage(currentPage + 1);
      }
    }
  }

  prevPage() {
    const currentPage = this.pageInfoSubject.getValue().currentPage;
    
    if (currentPage > 0) {

      if(this.selectedCategories.length > 0) {

        const params = new HttpParams()
        .set('categoryNames', this.selectedCategories.join(','))
        .set('page', currentPage-1)
        .set('size', 50);
        this.productService.searchByMapParams(params).subscribe(products => {
          this.productsSubject.next(products.content);
          this.pageInfoSubject.next({
            currentPage: products.page,
            totalPages: products.totalPages,
            pageSize: products.size
          })
        });

      }else{
        this.loadPage(currentPage - 1);
      }

      
    }
  }

  setPage(page: number) {
    if(this.selectedCategories.length > 0) {

      const params = new HttpParams()
      .set('categoryNames', this.selectedCategories.join(','))
      .set('page', page-1)
      .set('size', 50);
      this.productService.searchByMapParams(params).subscribe(products => {
        this.productsSubject.next(products.content);
        this.pageInfoSubject.next({
          currentPage: products.page,
          totalPages: products.totalPages,
          pageSize: products.size
        })
      });
      
    }else{
      this.loadPage(page - 1);
    }
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
    console.log('rating', rating);
    return rating ? parseFloat(rating) : 0;
  }

  navigateToOffers(): void {
  //this.router.navigate(['/offers']); // or your desired route
}

getProduct(id:string){
  this.router.navigate(['/products/', id]);
}

loadCategories() {
  this.productService.getCategories().subscribe(categories => {
    this.categories = categories;
  });
}


selectedCategories: string[] = [];
priceFilter: number = 5000;
selectedRatings: number[] = [];

onCategoryChange(event: Event) {
  const checkbox = event.target as HTMLInputElement;
  const categoryNames = checkbox.value;
  console.log('categoryId', checkbox.value);
  if (checkbox.checked) {
    this.selectedCategories.push(categoryNames);
  } else {
    this.selectedCategories = this.selectedCategories.filter(name => name !== categoryNames);
  }
  this.applyFilters();
}

onPriceChange() {
  this.applyFilters();
}

onRatingChange(event: Event) {
  const checkbox = event.target as HTMLInputElement;
  const rating = +checkbox.value;
  if (checkbox.checked) {
    this.selectedRatings.push(rating);
  } else {
    this.selectedRatings = this.selectedRatings.filter(r => r !== rating);
  }
  this.applyFilters();
}

applyFilters() {
  // Call backend or filter products$ using selectedCategories, priceFilter, and selectedRatings
  console.log('Filters applied:', {
    categories: this.selectedCategories,
    maxPrice: this.priceFilter,
    ratings: this.selectedRatings
  });

  //call search api with categorie params
  const params = new HttpParams()
   .set('categoryNames', this.selectedCategories.join(','))
   .set('page', 0)
   .set('size', 50);

  // .set('maxPrice', this.priceFilter.toString())
  // .set('ratings', this.selectedRatings.join(','));
  
  this.productService.searchByMapParams(params).subscribe(products => {
    this.productsSubject.next(products.content);
    this.pageInfoSubject.next({
      currentPage: products.page,
      totalPages: products.totalPages,
      pageSize: products.size
    })
  });

  // Example: e

}
}
