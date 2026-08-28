import { createRouter, createWebHistory } from 'vue-router'
import IntroView from '../views/IntroView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import AdminDashboardView from '../views/AdminDashboardView.vue'
import AdminUtilizatoriView from '../views/AdminUtilizatoriView.vue'
import AdminPlanteView from '../views/AdminPlanteView.vue'
import AdminStatisticiView from '../views/AdminStatisticiView.vue'
import IerbarGlobalView from '../views/IerbarGlobalView.vue'
import ChatView from '../views/ChatView.vue'
import ProfilView from '../views/ProfilView.vue'

// Cele 3 pagini noi
import IdentificareView from '../views/IdentificareView.vue'
import IerbarView from '../views/IerbarView.vue'
import CuriozitatiView from '../views/CuriozitatiView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', component: IntroView },
    { path: '/login', component: LoginView },
    { path: '/register', component: RegisterView },
    { path: '/dashboard', component: DashboardView },
    { path: '/admin-dashboard', component: AdminDashboardView },
    
    // Rutele noi
    { path: '/identificare', component: IdentificareView },
    { path: '/ierbar', component: IerbarView },
    { path: '/curiozitati', component: CuriozitatiView },
    { path: '/ierbar-global', component: IerbarGlobalView },
    { path: '/admin-utilizatori', component: AdminUtilizatoriView },
    { path: '/admin-plante', component: AdminPlanteView },
    { path: '/admin-statistici', component: AdminStatisticiView },
    { path: '/chat', component: ChatView },
    {path:'/profil', component: ProfilView}
  ]
})

export default router