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

  constructor(private productIndexService: ProductService) {}

  ngOnInit() {
    this.searchSubject.pipe(
      debounceTime(300),
      switchMap(query => {
        this.loading = true;
        return this.productIndexService.search(query);
      })
    ).subscribe(products => {
      this.results = products.content;
      this.loading = false;
      this.selectedIndex = -1;
    });
  }

  onSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchText = input.value;
    if(this.searchText==''){
      this.results=[];
    }
    this.searchSubject.next(this.searchText);
  }

  getProduct(name:string){
    console.log("get product "+name)
  }


  // Add these properties & methods in your component class

features = [
  { icon: "🚚", title: "Fast Shipping", description: "Get your orders delivered quickly and reliably." },
  { icon: "💳", title: "Secure Payments", description: "Your transactions are safe with industry-grade security." },
  { icon: "🎁", title: "Great Deals", description: "Enjoy exclusive discounts and offers daily." },
  { icon: "🤝", title: "Customer Support", description: "We're here to help 24/7 with any questions." }
];

categories = ["Electronics", "Books", "Clothing", "Home & Garden", "Toys", "Sports"];

testimonials = [
  { name: "Alice J.", message: "Amazing products and top-notch service!" },
  { name: "Mark S.", message: "I always find what I need quickly here." },
  { name: "Sophia L.", message: "Customer support was super helpful with my order." }
];

newsletterEmail = "";
subscriptionSuccess = false;

filterByCategory(category: string) {
  // You can add your logic here, e.g., filter results or route to category page
  console.log("Filter products by category:", category);
}

subscribeNewsletter(event: Event) {
  event.preventDefault();
  if (this.newsletterEmail) {
    // You can add actual subscription logic here (API call)
    this.subscriptionSuccess = true;
    this.newsletterEmail = "";
    setTimeout(() => (this.subscriptionSuccess = false), 5000);
  }
}



}
