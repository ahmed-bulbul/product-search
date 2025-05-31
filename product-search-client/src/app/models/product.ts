export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  rating: number;
  brand: Brand;
}

export interface Brand {
  id: string;
  name: string;
}
