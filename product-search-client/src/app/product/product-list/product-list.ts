import { Component, OnInit } from '@angular/core';
import { MOCK_PRODUCTS } from '../../mock-data/mock-products';
import { Product } from '../../models/product';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs/internal/Subject';
import { debounceTime, switchMap } from 'rxjs';
import { ProductService } from '../../service/product-service';
import { CartService } from '../../service/cart-service';

@Component({
  selector: 'app-product-list',
  imports: [CommonModule, FormsModule],
   templateUrl: './product-list.html',
  styleUrl: './product-list.css' // ✅ fix typo: styleUrl → styleUrls
})
export class ProductList  implements OnInit{
  products: Product[] = [];
  displayedProducts: Product[] = [];
  cartItems: any[] = [];

  currentPage = 1;
  pageSize = 6;
  totalPages = 1;

  loading = false;

  searchText = '';
  results: Product[] = [];
  selectedIndex = -1; // for keyboard nav if you want

  private searchSubject = new Subject<string>();

  constructor(private productIndexService: ProductService,private cartService: CartService) {}

  ngOnInit() {
    this.loadProducts();

    this.searchSubject.pipe(
          debounceTime(300),
          switchMap(query => {
            this.loading = true;
            return this.productIndexService.search(query);
          })
        ).subscribe(products => {
          this.results = products;
          this.loading = false;
          this.selectedIndex = -1;
        });
  }

  loadProducts() {
  this.loading = true;

  console.log('is loading'+this.loading)


    this.products = MOCK_PRODUCTS;
    console.log('Loaded products:', this.products);

    this.totalPages = Math.ceil(this.products.length / this.pageSize);

    this.setPage(1); // ✅ Call after products are set
    this.loading = false;

}

  setPage(page: number) {
  if (page < 1) page = 1;
  if (page > this.totalPages) page = this.totalPages;

  this.currentPage = page;

  const startIndex = (page - 1) * this.pageSize;
  const endIndex = startIndex + this.pageSize;

  this.displayedProducts = this.products.slice(startIndex, endIndex);

  console.log('Current Page:', this.currentPage);
  console.log('Displayed Products:', this.displayedProducts);
}


  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.setPage(this.currentPage + 1);
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.setPage(this.currentPage - 1);
    }
  }

  trackById(index: number, product: Product) {
    return product.id;
  }

  getPageArray(): number[] {
    return Array(this.totalPages).fill(0).map((_, i) => i + 1);
  }


    onSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchText = input.value;
    if(this.searchText==''){
      this.results=[];
    }
    this.searchSubject.next(this.searchText);
  }

  productDetails(id:string){
    alert("product details "+id)
  }

  performSearch(){
    alert(this.searchText)
  }



addedProductIds = new Set<string>();

addToCart(product: Product) {
  this.cartService.addToCart({
    id: product.id,
    name: product.name,
    price: product.price,
    imageUrl: product.imageUrl,
    quantity: 1,
  });

  // Add product id to the set of added products
  this.addedProductIds.add(product.id);
}


}
