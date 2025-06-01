import { Routes } from '@angular/router';
import { Search } from './product/search/search';
import { ProductList } from './product/product-list/product-list';
import { CheckoutComponent } from './cart/checkout-component/checkout-component';
import { ContactComponent } from './contact/contact-component/contact-component';
import { DashboardComponent } from './admin/dashboard-component/dashboard-component';
import { SchedulerComponent } from './admin/scheduler-component/scheduler-component';

export const routes: Routes = [

      { path: '', component: Search },
      {path :'products',component:ProductList},
      {path:'checkout',component:CheckoutComponent},
      {path:'contact',component:ContactComponent},
      {path:'admin/dashboard',component:DashboardComponent},
      {path:'admin/jobs',component:SchedulerComponent},
];

