export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  rating: number;
  imageUrl: string;
  brand: Brand;
}

export interface Brand {
  id: string;
  name: string;
}
