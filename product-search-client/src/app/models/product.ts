export interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  sku: string | null;
  quantity: number | null;
  size: string | null;
  color: string | null;
  model: string | null;
  rating: string | null;
  images: string[] | null;
  brand: string; // API shows brand is a string, not an object
  category: string | null;
  createdAt?: string;
  updatedAt?: string;
  active?: boolean;
  imageUrl?: string; // Keep for compatibility with cart service
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface Category{
  id: string;
  name: string;
  description: string;
  createdAt?: string;
  updatedAt?: string;
  active?: boolean;
}