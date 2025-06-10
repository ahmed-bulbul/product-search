import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZonelessChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors, withInterceptorsFromDi } from '@angular/common/http'; // Updated import
import { authInterceptor } from './interceptors/auth.interceptor'; // Import the interceptor

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    // provideHttpClient(withInterceptorsFromDi()) // Remove or comment out if not using class-based interceptors elsewhere
    provideHttpClient(withInterceptors([authInterceptor])) // Provide functional interceptor
  ]
};
