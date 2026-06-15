import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { DesignPage } from './pages/DesignPage';
import { HomePage } from './pages/HomePage';

const queryClient = new QueryClient();

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/design/:id" element={<DesignPage />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
