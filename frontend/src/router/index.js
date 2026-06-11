import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/upload',
    name: 'Upload',
    component: () => import('../views/ResumeUpload.vue'),
    meta: { role: 'admin' }
  },
  {
    path: '/list',
    name: 'List',
    component: () => import('../views/ResumeList.vue'),
    meta: { role: 'user' }
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('../views/ResumeFavorites.vue'),
    meta: { role: 'user' }
  },
  {
    path: '/edit/:id',
    name: 'Edit',
    component: () => import('../views/ResumeEdit.vue'),
    meta: { role: 'admin' }
  },
  {
    path: '/',
    redirect: () => {
      const role = localStorage.getItem('role')
      return role === 'admin' ? '/upload' : '/list'
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const role = localStorage.getItem('role')

  // Login page: if already logged in, redirect to home
  if (to.meta.public) {
    if (role) {
      next(role === 'admin' ? '/upload' : '/list')
    } else {
      next()
    }
    return
  }

  // Not logged in → login page
  if (!role) {
    next('/login')
    return
  }

  // Role-based access
  const requiredRole = to.meta.role
  if (requiredRole && role !== requiredRole) {
    next(role === 'admin' ? '/upload' : '/list')
    return
  }

  next()
})

export default router
