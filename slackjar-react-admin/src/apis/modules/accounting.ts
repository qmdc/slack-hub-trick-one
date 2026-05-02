import request from '../request';

export const getAccounts = () => {
  return request.get('/api/accounting/account');
};

export const getDefaultAccount = () => {
  return request.get('/api/accounting/account/default');
};

export const createAccount = (data: Account) => {
  return request.post('/api/accounting/account', data);
};

export const updateAccount = (id: number, data: Account) => {
  return request.put(`/api/accounting/account/${id}`, data);
};

export const deleteAccount = (id: number) => {
  return request.delete(`/api/accounting/account/${id}`);
};

export const getCategories = (type?: string) => {
  return request.get('/api/accounting/category', { params: { type } });
};

export const getIncomeCategories = () => {
  return request.get('/api/accounting/category/income');
};

export const getExpenseCategories = () => {
  return request.get('/api/accounting/category/expense');
};

export const createCategory = (data: Category) => {
  return request.post('/api/accounting/category', data);
};

export const updateCategory = (id: number, data: Category) => {
  return request.put(`/api/accounting/category/${id}`, data);
};

export const deleteCategory = (id: number) => {
  return request.delete(`/api/accounting/category/${id}`);
};

export const createBill = (data: Bill) => {
  return request.post('/api/accounting/bill', data);
};

export const getBill = (id: number) => {
  return request.get(`/api/accounting/bill/${id}`);
};

export const updateBill = (id: number, data: Bill) => {
  return request.put(`/api/accounting/bill/${id}`, data);
};

export const deleteBill = (id: number) => {
  return request.delete(`/api/accounting/bill/${id}`);
};

export const getBillStatistics = (startTime?: number, endTime?: number) => {
  return request.get('/api/accounting/bill/statistics', { params: { startTime, endTime } });
};

export const getBudgets = (periodType: string, year: number, month?: number) => {
  return request.get('/api/accounting/budget', { params: { periodType, year, month } });
};

export const checkBudgetExceed = (categoryId?: number, amount: number = 0, year: number = 0, month: number = 0) => {
  return request.get('/api/accounting/budget/check', { params: { categoryId, amount, year, month } });
};

export const createBudget = (data: Budget) => {
  return request.post('/api/accounting/budget', data);
};

export const updateBudget = (id: number, data: Budget) => {
  return request.put(`/api/accounting/budget/${id}`, data);
};

export const deleteBudget = (id: number) => {
  return request.delete(`/api/accounting/budget/${id}`);
};

export const transfer = (data: TransferRequest) => {
  return request.post('/api/accounting/transfer', data);
};

export const getTransfers = () => {
  return request.get('/api/accounting/transfer');
};

export const getTransfer = (id: number) => {
  return request.get(`/api/accounting/transfer/${id}`);
};

export const getStatistics = (startTime?: number, endTime?: number) => {
  return request.get('/api/accounting/report/statistics', { params: { startTime, endTime } });
};

export const getMonthlyReport = (year: number, month: number) => {
  return request.get(`/api/accounting/report/monthly/${year}/${month}`);
};

export const generateMonthlyReport = (year: number, month: number) => {
  return request.post(`/api/accounting/report/monthly/${year}/${month}`);
};

export const getYearlyReport = (year: number) => {
  return request.get(`/api/accounting/report/yearly/${year}`);
};

export const generateYearlyReport = (year: number) => {
  return request.post(`/api/accounting/report/yearly/${year}`);
};

export interface Account {
  id?: number;
  name: string;
  type: string;
  balance: number;
  bankName?: string;
  cardNumber?: string;
  icon?: string;
  color?: string;
  isDefault?: number;
  isActive?: number;
  remark?: string;
}

export interface Category {
  id?: number;
  name: string;
  type: string;
  icon?: string;
  color?: string;
  parentId?: number;
  sortOrder?: number;
  isSystem?: number;
  isActive?: number;
  remark?: string;
}

export interface Bill {
  id?: number;
  amount: number;
  type: string;
  categoryId: number;
  accountId: number;
  payee?: string;
  remark?: string;
  billDate: number;
  location?: string;
  receiptImage?: string;
}

export interface Budget {
  id?: number;
  categoryId?: number;
  amount: number;
  periodType: string;
  year: number;
  month?: number;
  spentAmount?: number;
  isActive?: number;
  remark?: string;
}

export interface TransferRequest {
  fromAccountId: number;
  toAccountId: number;
  amount: number;
  remark?: string;
}

export interface Transfer {
  id?: number;
  fromAccountId: number;
  toAccountId: number;
  amount: number;
  transferDate?: number;
  remark?: string;
}

export interface Report {
  id?: number;
  reportType: string;
  year: number;
  month?: number;
  totalIncome: number;
  totalExpense: number;
  savingsRate: number;
  dataJson?: string;
  isPushed?: number;
  pushTime?: number;
}

export interface StatisticsResult {
  totalIncome: number;
  totalExpense: number;
  balance: number;
  categoryBreakdown: Record<string, number>;
  trendData: TrendData;
}

export interface TrendData {
  labels: string[];
  income: number[];
  expense: number[];
}