import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Dev server proxies API + SSE to the Spring Boot backend on :9192,
// so the SPA runs same-origin in the browser and needs no backend CORS config.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5174,
    strictPort: true,
    proxy: {
      // Only API calls are proxied to the backend. SPA routes like /design/:id are
      // served by Vite (index.html) so full-page loads/refreshes render the app.
      '/api': { target: 'http://localhost:9192', changeOrigin: true },
    },
  },
});
