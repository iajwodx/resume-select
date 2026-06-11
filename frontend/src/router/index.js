import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/upload'
  },
  {
    path: '/upload',
    name: 'Upload',
    component: () => import('../views/ResumeUpload.vue')
  },
  {
    path: '/list',
    name: 'List',
    component: () => import('../views/ResumeList.vue')
  },
  {
    path: '/favorites',
    name: 'Favorites',
    component: () => import('../views/ResumeFavorites.vue')
  },
  {
    path: '/edit/:id',
    name: 'Edit',
    component: () => import('../views/ResumeEdit.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
