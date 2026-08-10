<template>
  <div class="page-wrapper">
    <div class="glass-card text-center">
      
      <!-- 2. FOLOSIM COMPONENTA AICI -->
      <!-- Am adăugat o clasă pentru a o centra frumos deasupra titlului -->
      <MascotaGhiocel class="mascota-centrata" />

      <h1 class="titlu-mare">Bun venit în Ierbărel!</h1>
      <p class="descriere">Exploratorul tău botanic personal. Descoperă, identifică și colecționează plantele din jurul tău.</p>
      
      <button @click="mergiLaLogin" class="btn-mare">Începe Aventura</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

// 1. IMPORTĂM COMPONENTA GHIOCEL
// Ne asigurăm că drumul duce corect către folderul components
import MascotaGhiocel from '../components/MascotaGhiocel.vue'

const router = useRouter()

const mergiLaLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
/* Păstrăm stilurile tale existente, plus adăugăm centrarea pentru mascotă */
.page-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
}

.glass-card {
  background: rgba(255, 255, 255, 0.9);
  padding: 3rem;
  border-radius: 20px;
  border: 3px solid var(--verde-deschis);
  max-width: 500px;
  width: 100%;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.text-center {
  text-align: center;
}

/* Clasa pentru a așeza mascota perfect pe centru */
.mascota-centrata {
  margin: 0 auto 20px auto; /* 0 sus, auto stânga/dreapta, 20px jos */
  width: 180px; /* O facem un pic mai micuță pentru pagina de intro */
}

.titlu-mare {
  color: var(--verde-inchis);
  font-size: 2.2rem;
  margin-bottom: 15px;
  margin-top: 0;
}

.descriere {
  color: #666;
  font-size: 1.1rem;
  line-height: 1.5;
  margin-bottom: 30px;
}

.btn-mare {
  background-color: var(--verde-inchis);
  color: white;
  border: none;
  padding: 15px 30px;
  border-radius: 12px;
  font-size: 1.2rem;
  font-weight: bold;
  cursor: pointer;
  transition: 0.3s;
  width: 100%;
}

.btn-mare:hover {
  background-color: var(--verde-deschis);
  color: #333;
  transform: translateY(-2px);
}
</style>