export type UserRole =
  | 'SUPER_ADMIN'
  | 'ADMIN'
  | 'DISTRICT_AUTHORITY'
  | 'LOGISTICS_OPERATOR'
  | 'FIELD_OFFICER'
  | 'DRIVER';

export interface User {
  id: string;
  fullName: string;
  email: string;
  phone?: string;
  role: UserRole;
  organization?: string;
  district?: string;
  is_active?: boolean;
  created_at?: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  sessionId: string;
  expiresIn?: string;
}

export interface LoginResponse extends AuthTokens {
  user: User;
}

export interface LoginPayload {
  email?: string;
  phone?: string;
  identifier?: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterPayload {
  fullName: string;
  email: string;
  phone?: string;
  password: string;
  role: UserRole;
  organization?: string;
  district?: string;
}

export interface UpdateProfilePayload {
  fullName?: string;
  phone?: string;
  organization?: string;
  district?: string;
}

export interface ChangePasswordPayload {
  currentPassword: string;
  newPassword: string;
}

export interface ForgotPasswordPayload {
  email?: string;
  phone?: string;
  identifier?: string;
}

export interface ResetPasswordPayload {
  token: string;
  newPassword: string;
}

export const NER_DISTRICTS = [
  // Assam
  'Guwahati (Kamrup Metro)',
  'Dibrugarh',
  'Silchar (Cachar)',
  'Jorhat',
  'Tezpur (Sonitpur)',
  // Meghalaya
  'Shillong (East Khasi Hills)',
  'Tura (West Garo Hills)',
  // Manipur
  'Imphal East',
  'Imphal West',
  'Churachandpur',
  // Nagaland
  'Kohima',
  'Dimapur',
  // Mizoram
  'Aizawl',
  'Lunglei',
  // Tripura
  'Agartala (West Tripura)',
  // Arunachal Pradesh
  'Itanagar (Papum Pare)',
  'Tawang',
  // Sikkim
  'Gangtok'
] as const;
