import request from '../request';

export interface QuoteCategory {
  id: number;
  name: string;
  icon: string;
  sortOrder: number;
  description: string;
}

export interface Quote {
  id: number;
  content: string;
  author: string;
  source: string;
  categoryId: number;
  isFavorite: number;
  viewCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface FavoriteStatus {
  isFavorite: boolean;
  favoriteCount: number;
}

export const quoteCategoryApi = {
  list: () => request.get<QuoteCategory[]>('/quote-categories'),
  
  getById: (id: number) => request.get<QuoteCategory>(`/quote-categories/${id}`),
  
  create: (data: Omit<QuoteCategory, 'id'>) => 
    request.post<QuoteCategory>('/quote-categories', data),
  
  update: (id: number, data: Partial<QuoteCategory>) => 
    request.put<QuoteCategory>(`/quote-categories/${id}`, data),
  
  delete: (id: number) => request.delete(`/quote-categories/${id}`),
};

export const quoteApi = {
  list: (params: { page: number; size: number; categoryId?: number; keyword?: string }) => 
    request.get<{ records: Quote[]; total: number; size: number; current: number }>('/quotes', { params }),
  
  getById: (id: number) => request.get<Quote>(`/quotes/${id}`),
  
  create: (data: Omit<Quote, 'id' | 'isFavorite' | 'viewCount' | 'createdAt' | 'updatedAt'>) => 
    request.post<Quote>('/quotes', data),
  
  update: (id: number, data: Partial<Quote>) => 
    request.put<Quote>(`/quotes/${id}`, data),
  
  delete: (id: number) => request.delete(`/quotes/${id}`),
  
  getRandom: (categoryId?: number) => 
    request.get<Quote>('/quotes/random', { params: categoryId ? { categoryId } : {} }),
  
  getFavorites: () => request.get<Quote[]>('/quotes/favorites'),
  
  toggleFavorite: (id: number) => 
    request.post<FavoriteStatus>(`/quotes/${id}/favorite`),
  
  getFavoriteStatus: (id: number) => 
    request.get<FavoriteStatus>(`/quotes/${id}/favorite-status`),
};