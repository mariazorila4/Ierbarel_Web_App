<template>
  <div class="page-wrapper">
    <!-- Clasă extra "admin-mode" pentru a schimba puțin aspectul -->
    <div class="glass-dashboard admin-mode">
      
      <div class="header">
        <h2 class="titlu">⚙️ Panou Administrator (Ierbărel)</h2>
        <button @click="deconectare" class="btn-secundar">Deconectare</button>
      </div>

      <p class="subtitlu">Ai acces complet la sistem. Ce dorești să gestionezi?</p>

      <div class="grid-actiuni">
        
        <!-- Card Admin 1 -->
        <div class="actiune-card card-roz-inchis">
          <div class="iconita">👥</div>
          <h3>Utilizatori</h3>
          <p>Vezi conturile create, șterge sau blochează utilizatori.</p>
          <button @click="router.push('/admin-utilizatori')" class="btn-actiune btn-roz-inchis">Gestionează</button>
        </div>

        <!-- Card Admin 2 -->
        <div class="actiune-card card-verde">
          <div class="iconita">🌱</div>
          <h3>Baza de Date Plante</h3>
          <p>Adaugă specii noi, editează descrieri sau validează poze.</p>
          <button @click="router.push('/admin-plante')" class="btn-actiune btn-verde">Deschide DB</button>
        </div>

        <!-- Card Admin 3 -->
        <div class="actiune-card card-albastru">
          <div class="iconita">📊</div>
          <h3>Statistici Sistem</h3>
          <p>Vezi câte plante au fost identificate astăzi de utilizatori.</p>
          <button @click="router.push('/admin-statistici')" class="btn-actiune btn-albastru">Vezi Raport</button>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const deconectare = () => {
  console.log('Admin deconectat.')
  router.push('/login')
}
</script>

<style scoped>
/* Folosim aceleași stiluri de bază ca la dashboard-ul normal */
.page-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; box-sizing: border-box; }
.glass-dashboard { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; width: 100%; max-width: 900px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); }

/* Schimbăm marginea pentru a evidenția că e panou de admin */
.admin-mode { border: 3px solid var(--roz-inchis); }

.header { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 20px; }
.titlu { color: var(--roz-inchis); margin: 0; }
.subtitlu { color: #666; font-size: 1.1rem; margin-bottom: 30px; }
.btn-secundar { background-color: transparent; color: #888; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; }
.btn-secundar:hover { background-color: #fee; color: #e74c3c; border-color: #e74c3c; }
.grid-actiuni { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; }
.actiune-card { background: white; padding: 20px; border-radius: 15px; text-align: center; transition: transform 0.3s ease, box-shadow 0.3s ease; border: 2px solid transparent; }
.actiune-card:hover { transform: translateY(-5px); box-shadow: 0 8px 20px rgba(0,0,0,0.08); }
.iconita { font-size: 3rem; margin-bottom: 10px; }
.actiune-card h3 { margin: 10px 0; color: #333; }
.actiune-card p { color: #777; font-size: 0.9rem; margin-bottom: 20px; min-height: 40px; }
.btn-actiune { width: 100%; padding: 10px; border: none; border-radius: 8px; font-weight: bold; cursor: pointer; color: white; transition: 0.3s; }

/* Culorile specifice din paleta ta */
.card-roz-inchis { border-color: var(--roz-deschis); }
.btn-roz-inchis { background-color: var(--roz-inchis); }
.btn-roz-inchis:hover { background-color: var(--roz-deschis); color: #333; }

.card-verde { border-color: var(--verde-deschis); }
.btn-verde { background-color: var(--verde-inchis); }
.btn-verde:hover { background-color: var(--verde-deschis); color: #333; }

.card-albastru { border-color: var(--albastru-pastel); }
.btn-albastru { background-color: var(--albastru-pastel); color: #333; }
.btn-albastru:hover { background-color: #8daeeb; }
</style>