import { createRouter, createWebHistory } from 'vue-router'
import SignInView from '../views/SignInView.vue'
import { useSessionStore } from '../stores/session.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/signin',
      name: 'signin',
      component: SignInView
    },
    {
      path: '/',
      redirect: to => {
        return '/dashboard';
      }
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: () => import('../views/DashboardView.vue'),
    },
    {
      path: '/:pathMatch(.*)',
      component: () => import('../views/NotFoundView.vue')
    }
  ]
});

router.beforeEach( (to, from, next) => {
  const sessionStore = useSessionStore();

  if (to.name === 'signin' && sessionStore.isLoggedIn()) {
    next({ name: 'dashboard' });
  } else if (to.name !== 'signin' && !(sessionStore.isLoggedIn())) {
    next({ name: 'signin' });
  } else {
    next();
  }
});

export default router
