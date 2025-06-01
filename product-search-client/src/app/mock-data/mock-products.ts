import { Product } from "../models/product";


export const MOCK_PRODUCTS: Product[] = [
  {
    id: 'p1',
    name: 'Wireless Bluetooth Headphones',
    description: 'High-quality wireless headphones with noise cancellation.',
    price: 99.99,
    rating: 4.5,
    imageUrl: 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg',
    brand: { id: 'b1', name: 'SoundMax' }
  },
  {
    id: 'p2',
    name: 'Smart Fitness Watch',
    description: 'Track your fitness and health metrics with this smartwatch.',
    price: 149.99,
    rating: 4.7,
    imageUrl: 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg',
    brand: { id: 'b2', name: 'FitLife' }
  },
  {
    id: 'p3',
    name: '4K Ultra HD Smart TV',
    description: 'Experience stunning visuals with this 55-inch 4K Smart TV.',
    price: 799.99,
    rating: 4.8,
    imageUrl: 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg',
    brand: { id: 'b3', name: 'VisionTech' }
  },
  {
    id: 'p4',
    name: 'Portable Bluetooth Speaker',
    description: 'Compact and powerful speaker for music on the go.',
    price: 39.99,
    rating: 4.3,
    imageUrl: 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg',
    brand: { id: 'b1', name: 'SoundMax' }
  },
  {
    id: 'p5',
    name: 'Wireless Charging Pad',
    description: 'Fast wireless charger compatible with most smartphones.',
    price: 25.99,
    rating: 4.1,
 imageUrl: 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg',
    brand: { id: 'b4', name: 'ChargeIt' }
  },
  {
    id: 'p6',
    name: 'Gaming Laptop',
    description: 'Powerful gaming laptop with latest graphics card.',
    price: 1199.99,
    rating: 4.6,
   imageUrl: 'https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg',
    brand: { id: 'b5', name: 'GamePro' }
  },
  {
    id: 'p7',
    name: 'Noise Cancelling Earbuds',
    description: 'Small earbuds with active noise cancellation and great sound.',
    price: 79.99,
    rating: 4.4,
    imageUrl: 'https://via.placeholder.com/200x150?text=Earbuds',
    brand: { id: 'b1', name: 'SoundMax' }
  },
  {
    id: 'p8',
    name: 'Smart Home Assistant',
    description: 'Voice-controlled smart assistant for your home.',
    price: 49.99,
    rating: 4.2,
    imageUrl: 'https://via.placeholder.com/200x150?text=Smart+Assistant',
    brand: { id: 'b6', name: 'HomeGenie' }
  },
  {
    id: 'p9',
    name: 'Action Camera',
    description: 'Record your adventures in 4K with this rugged camera.',
    price: 199.99,
    rating: 4.5,
    imageUrl: 'https://via.placeholder.com/200x150?text=Action+Camera',
    brand: { id: 'b7', name: 'AdventureCam' }
  },
  {
    id: 'p10',
    name: 'Wireless Keyboard and Mouse Combo',
    description: 'Comfortable wireless keyboard and mouse for your PC.',
    price: 59.99,
    rating: 4.3,
    imageUrl: 'https://via.placeholder.com/200x150?text=Keyboard+%26+Mouse',
    brand: { id: 'b8', name: 'KeyMice' }
  }
];
