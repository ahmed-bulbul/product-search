import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../service/auth.service';
import { map, take } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode'; // Corrected import

interface DecodedToken {
  sub: string;
  roles: string[]; // Assuming 'roles' is an array of strings in your JWT
  iat: number;
  exp: number;
}

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isLoggedIn().pipe(
    take(1),
    map(isLoggedIn => {
      if (isLoggedIn) {
        const token = authService.getToken();
        if (token) {
          try {
            const decodedToken: DecodedToken = jwtDecode<DecodedToken>(token);
            // Check if roles exist and include ROLE_ADMIN
            if (decodedToken.roles && decodedToken.roles.includes('ROLE_ADMIN')) {
              return true;
            } else {
              console.warn('Admin guard: User does not have ROLE_ADMIN');
              router.navigate(['/']); // Or an unauthorized page
              return false;
            }
          } catch (error) {
            console.error('Admin guard: Error decoding token', error);
            router.navigate(['/login']); // Invalid token, force login
            return false;
          }
        } else {
          // Should not happen if isLoggedIn is true, but as a safeguard
          router.navigate(['/login']);
          return false;
        }
      } else {
        router.navigate(['/login'], { queryParams: { returnUrl: state.url } });
        return false;
      }
    })
  );
};
