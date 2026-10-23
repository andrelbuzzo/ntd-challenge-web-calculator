import React from 'react';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import App from './App';

jest.mock('./pages/Home', () => () => <div data-testid="home-page" />);

jest.mock('axios', () => ({
  create: jest.fn(() => ({
    get: jest.fn(() => Promise.resolve({ data: { balance: 10 } })),
    post: jest.fn(),
    delete: jest.fn(),
    interceptors: { request: { use: jest.fn() } }
  })),
  post: jest.fn(),
  get: jest.fn()
}));

it('renders without crashing', () => {
  render(<App />);
});

it('renders larger navigation labels with icons for an authenticated user', () => {
  localStorage.setItem('user', JSON.stringify({ token: 'test-token' }));
  window.history.pushState({}, '', '/home');

  render(<App />);

  expect(screen.getByRole('link', { name: 'Calculator', exact: true })).toBeInTheDocument();
  expect(screen.getByRole('link', { name: 'History', exact: true })).toBeInTheDocument();
  expect(screen.getByRole('button', { name: /Sign out/ })).toBeInTheDocument();
  expect(document.querySelector('.brand-mark .fa-calculator')).toBeInTheDocument();
  expect(document.querySelector('.app-nav .fa-clock-rotate-left')).toBeInTheDocument();
  expect(document.querySelector('.header-logout .fa-right-from-bracket')).toBeInTheDocument();
  expect(document.querySelector('.app-nav a.active')).toHaveAttribute('href', '/home');
  expect(screen.getByRole('link', { name: 'LinkedIn' })).toHaveAttribute('href', 'https://www.linkedin.com/in/andre-buzzo');
  expect(screen.getByRole('link', { name: 'GitHub' })).toHaveAttribute('href', 'https://github.com/andrelbuzzo');

  localStorage.clear();
  window.history.pushState({}, '', '/');
});
