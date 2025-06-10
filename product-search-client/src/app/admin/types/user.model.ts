export interface Permission {
  id: number;
  name: string;
}

export interface Role {
  id: number;
  name: string;
  permissions?: Permission[]; // Optional: depending on whether you fetch roles with their permissions
}

export interface User {
  id: number;
  username: string;
  email: string;
  enabled: boolean;
  roles: Role[];
  // Add other user properties as returned by the backend
}
