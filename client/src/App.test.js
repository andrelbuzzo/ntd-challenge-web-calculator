import React from 'react';
import { createRoot } from 'react-dom/client';
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
  const div = document.createElement('div');
  const root = createRoot(div);
  root.render(<App />);
  root.unmount();
});
