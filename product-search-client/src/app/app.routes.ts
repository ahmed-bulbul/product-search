import { Routes } from '@angular/router';
import { Routes } from '@angular/router';
import { Search } from './product/search/search';
import { ProductList } from './product/product-list/product-list';
import { CheckoutComponent } from './cart/checkout-component/checkout-component';
import { ContactComponent } from './contact/contact-component/contact-component';
// import { DashboardComponent } from './admin/dashboard-component/dashboard-component'; // No longer directly imported
import { SchedulerComponent } from './admin/scheduler-component/scheduler-component'; // Assuming this is NOT part of the new AdminModule or needs separate guarding
import { SchedulerListComponent } from './admin/scheduler-list-component/scheduler-list-component'; // Assuming this is NOT part of the new AdminModule or needs separate guarding
import { ProductDetailsComponent } from './product/product-details-component/product-details-component';
import { LoginComponent } from './login/login';
import { RegisterComponent } from './register/register';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
      { path: '', component: Search },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'products', component: ProductList },
      { path: 'checkout', component: CheckoutComponent, canActivate: [authGuard] }, // Protected
      { path: 'contact', component: ContactComponent },
      {
        path: 'admin',
        loadChildren: () => import('./admin/admin.routes').then(m => m.ADMIN_ROUTES),
        canActivate: [adminGuard] // Protect the whole admin module
      },
      // TODO: Review if these scheduler routes should be part of the admin module or stay separate.
      // If they are admin features, they should move into ADMIN_ROUTES and be covered by its adminGuard.
      // If they need different guards, they can stay here. For now, assuming they might need admin access too.
      { path: 'admin/jobs/create', component: SchedulerComponent, canActivate: [adminGuard] },
      { path: 'admin/jobs', component: SchedulerListComponent, canActivate: [adminGuard] },
      { path: 'products/:id', component: ProductDetailsComponent }
];

