<template>
  <div class="page-wrapper">
    <div class="glass-card wide-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <h2 class="titlu">📸 Identifică o Plantă</h2>
      </div>

      <div class="continut">
        <p class="descriere">Încarcă o fotografie clară cu frunza sau floarea plantei, iar asistentul nostru o va analiza.</p>
        
        <!-- Zona de upload -->
        <div class="zona-upload">
          <div class="iconita-upload">📥</div>
          <p>Apasă aici pentru a alege o imagine</p>
          <input type="file" accept="image/*" class="input-fisier" @change="previzualizeazaImagine" />
        </div>

        <!-- Previzualizarea imaginii (apare doar dacă utilizatorul alege o poză) -->
        <div v-if="imagineSelectata" class="previzualizare">
          <p>Imagine selectată:</p>
          <img :src="imagineSelectata" alt="Previzualizare" class="poza-mica" />
        </div>

        <button @click="trimiteSpreAnaliza" class="btn-mare btn-verde" :disabled="!imagineSelectata">
          Analizează Planta
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const imagineSelectata = ref(null)

const mergiInapoi = () => router.push('/dashboard')

const previzualizeazaImagine = (event) => {
  const fisier = event.target.files[0]
  if (fisier) {
    // Creăm un URL temporar în browser pentru a arăta poza imediat
    imagineSelectata.value = URL.createObjectURL(fisier)
  }
}

const trimiteSpreAnaliza = () => {
  console.log('Trimit poza spre backend (Java) pentru analiză AI...')
  alert('În curând! Aici backend-ul va procesa imaginea.')
}
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--verde-deschis); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.wide-card { max-width: 600px; }
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 20px; }
.titlu { color: var(--verde-inchis); margin: 0; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }
.descriere { color: #666; margin-bottom: 20px; text-align: center; }

.zona-upload {
  border: 2px dashed var(--verde-deschis);
  background: var(--crem-fundal);
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  position: relative;
  cursor: pointer;
  transition: 0.3s;
}
.zona-upload:hover { background: #eef7d2; }
.iconita-upload { font-size: 3rem; margin-bottom: 10px; }
.input-fisier {
  position: absolute; top: 0; left: 0; width: 100%; height: 100%; opacity: 0; cursor: pointer;
}

.previzualizare { margin-top: 20px; text-align: center; }
.poza-mica { max-width: 200px; border-radius: 10px; border: 2px solid var(--verde-inchis); margin-top: 10px; }
.btn-mare { width: 100%; padding: 15px; border: none; border-radius: 10px; font-weight: bold; font-size: 1.1rem; color: white; margin-top: 20px; cursor: pointer; transition: 0.3s; }
.btn-verde { background-color: var(--verde-inchis); }
.btn-verde:hover:not(:disabled) { background-color: var(--verde-deschis); color: #333; }
.btn-verde:disabled { background-color: #ccc; cursor: not-allowed; }
</style>