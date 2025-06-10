import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ADMIN_ROUTES } from './admin.routes';
// Components will be standalone, so they don't need to be declared here.

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forChild(ADMIN_ROUTES) // Import routes for this lazy-loaded module
  ],
  // No declarations or exports needed if all child components are standalone
})
export class AdminModule { }
