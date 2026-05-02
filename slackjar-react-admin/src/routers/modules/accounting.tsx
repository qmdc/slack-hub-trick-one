import lazyLoad from '../lazyLoad';
import React, { lazy } from 'react';
import { WalletOutlined } from '@ant-design/icons';
import { MenuRouteObject } from '../router';

export const accounting: MenuRouteObject = {
  path: 'accounting',
  label: 'menu.accounting',
  icon: <WalletOutlined />,
  children: [
    {
      path: 'quick-bill',
      label: 'menu.quick bill',
      element: lazyLoad(lazy(() => import('../../pages/Accounting/QuickBill'))),
    },
    {
      path: 'account-list',
      label: 'menu.account list',
      element: lazyLoad(lazy(() => import('../../pages/Accounting/AccountList'))),
    },
    {
      path: 'transfer',
      label: 'menu.transfer',
      element: lazyLoad(lazy(() => import('../../pages/Accounting/Transfer'))),
    },
    {
      path: 'budget',
      label: 'menu.budget',
      element: lazyLoad(lazy(() => import('../../pages/Accounting/BudgetManagement'))),
    },
    {
      path: 'statistics',
      label: 'menu.statistics',
      element: lazyLoad(lazy(() => import('../../pages/Accounting/Statistics'))),
    },
  ],
};