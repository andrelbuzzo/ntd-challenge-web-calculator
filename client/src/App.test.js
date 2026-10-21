import React from 'react';
import { render } from '@testing-library/react';
import App from './App';

jest.mock('axios', () => ({
  create: jest.fn(() => ({
    get: jest.fn(),
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
