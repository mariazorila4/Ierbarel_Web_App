<template>
  <div class="page-wrapper">
    <div class="glass-card wide-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <h2 class="titlu">🌸 Curiozități Botanice</h2>
      </div>

      <div class="lista-articole">
        
        <div class="articol card-roz-deschis">
          <h3>Știai că bambusul...</h3>
          <p>Este planta care crește cel mai repede de pe planetă? Anumite specii pot crește cu până la 91 cm într-o singură zi!</p>
        </div>

        <div class="articol card-verde-deschis">
          <h3>Mirosul ierbii proaspăt tăiate...</h3>
          <p>Este de fapt un strigăt de ajutor! Plantele eliberează acești compuși chimici pentru a avertiza alte plante din jur că există un pericol (mașina de tuns iarba).</p>
        </div>

        <div class="articol card-albastru-pastel">
          <h3>Copacii comunică între ei</h3>
          <p>Prin intermediul unei rețele subterane de ciuperci (numită rețeaua micorizală), copacii își pot transfera nutrienți și își pot trimite semnale de stres.</p>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--roz-deschis); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.wide-card { max-width: 700px; }
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 25px; }
.titlu { color: var(--roz-inchis); margin: 0; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }

.lista-articole { display: flex; flex-direction: column; gap: 20px; }
.articol { padding: 20px; border-radius: 12px; border-left: 5px solid transparent; background: white; box-shadow: 0 4px 10px rgba(0,0,0,0.03); }
.articol h3 { margin: 0 0 10px 0; color: #333; }
.articol p { margin: 0; color: #555; line-height: 1.5; }

/* Borduri colorate specifice */
.card-roz-deschis { border-left-color: var(--roz-inchis); }
.card-verde-deschis { border-left-color: var(--verde-inchis); }
.card-albastru-pastel { border-left-color: var(--albastru-pastel); }
</style>