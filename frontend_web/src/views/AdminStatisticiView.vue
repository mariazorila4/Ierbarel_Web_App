<template>
  <div class="page-wrapper">
    <div class="glass-card wide-card admin-mode">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <h2 class="titlu">📊 Statistici Sistem</h2>
      </div>

      <div class="statistici-grid">
        <div class="stat-card">
          <div class="stat-numar">1,204</div>
          <div class="stat-label">Utilizatori Activi</div>
        </div>
        <div class="stat-card">
          <div class="stat-numar">8,432</div>
          <div class="stat-label">Plante Identificate</div>
        </div>
        <div class="stat-card">
          <div class="stat-numar">45</div>
          <div class="stat-label">Specii în Baza de Date</div>
        </div>
        <div class="stat-card atentionare">
          <div class="stat-numar">0</div>
          <div class="stat-label">Erori Server Astăzi</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const mergiInapoi = () => router.push('/admin-dashboard')
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.wide-card { max-width: 900px; }
.admin-mode { border: 3px solid var(--albastru-pastel); }
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 20px; }
.titlu { color: var(--albastru-pastel); margin: 0; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }

.statistici-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin-top: 20px; }
.stat-card { background: white; border: 2px solid var(--crem-fundal); padding: 30px 20px; border-radius: 15px; text-align: center; box-shadow: 0 4px 10px rgba(0,0,0,0.03); }
.stat-numar { font-size: 2.5rem; font-weight: bold; color: var(--albastru-pastel); margin-bottom: 10px; }
.stat-label { color: #666; font-size: 1.1rem; }
.atentionare { border-color: #d4edda; }
.atentionare .stat-numar { color: #28a745; }
</style>