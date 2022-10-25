import { createRouter, createWebHistory } from 'vue-router'
import SignInView from '../views/SignInView.vue'
import NotFoundComponent from '../views/NotFoundView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/signin'
    },
    {
      path: '/signin',
      name: 'signin',
      component: SignInView
    },
    {
      path: '/:pathMatch(.*)',
      component: () => NotFoundComponent
    }
  ]
})

export default router
