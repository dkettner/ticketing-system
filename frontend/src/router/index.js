import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import NotFoundComponent from '../views/NotFoundView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/signin',
      name: 'signin',
      component: () => import('../views/SignInView.vue')
    },
    {
      path: '/:pathMatch(.*)',
      component: () => NotFoundComponent
    }
  ]
})

export default router
