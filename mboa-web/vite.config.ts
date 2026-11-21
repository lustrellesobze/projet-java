/// <reference types="vitest/config" />
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    port: 5173,
    // En développement, /api est redirigé vers l'orchestrateur Spring Boot
    proxy: { '/api': 'http://localhost:8090' },
  },
  test: {
    environment: 'node',
  },
})
