import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  build: {
    // 代码分割优化
    rollupOptions: {
      output: {
        manualChunks: {
          // Vue 核心库单独打包
          'vue-core': ['vue', 'vue-router', 'pinia'],
          // UI 组件库单独打包
          'element-plus': ['element-plus'],
          // HTTP 客户端单独打包
          'http-client': ['axios']
        }
      }
    },
    // 代码分割大小限制
    chunkSizeWarningLimit: 500,
    // 压缩优化
    minify: 'terser',
    terserOptions: {
      compress: {
        // 移除 console.log
        drop_console: true,
        // 移除 debugger
        drop_debugger: true,
        // 移除纯函数调用
        pure_funcs: ['console.log', 'console.info']
      }
    },
    // 生成 source map（生产环境可选）
    sourcemap: false,
    // 资源内联限制
    assetsInlineLimit: 4096,
    // CSS 代码分割
    cssCodeSplit: true,
    // 预加载模块
    modulePreload: {
      polyfill: true
    }
  },
  // 优化依赖预构建
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'axios', 'element-plus'],
    exclude: ['@/components']
  }
})
