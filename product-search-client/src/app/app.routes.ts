import { Routes } from '@angular/router';
import { Search } from './product/search/search';
import { ProductList } from './product/product-list/product-list';

export const routes: Routes = [

      { path: '', component: Search },
      {path :'products',component:ProductList}
];

