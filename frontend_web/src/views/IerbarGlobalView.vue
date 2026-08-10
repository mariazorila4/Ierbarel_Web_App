<template>
  <div class="page-wrapper">
    <div class="glass-card full-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi la Dashboard</button>
        <h2 class="titlu">🌍 Ierbarul Global</h2>
      </div>

      <p class="descriere">Explorează enciclopedia noastră. Caută o plantă după numele ei comun sau denumirea științifică.</p>

      <!-- Bara de căutare -->
      <div class="zona-cautare">
        <span class="iconita-cautare">🔍</span>
        <input 
          type="text" 
          v-model="termenCautare" 
          placeholder="Caută o plantă (ex: Păpădie, Taraxacum...)" 
          class="input-cautare"
        />
      </div>

      <!-- Mesaj dacă nu găsim nimic -->
      <div v-if="planteFiltrate.length === 0" class="mesaj-gol">
        <p>Nu am găsit nicio plantă care să se potrivească cu căutarea: "<strong>{{ termenCautare }}</strong>".</p>
      </div>

      <!-- Grila de plante (afișează doar plantele filtrate) -->
      <div class="grila-plante">
        <div 
          v-for="planta in planteFiltrate" 
          :key="planta.id" 
          class="card-planta"
          @click="deschideDetalii(planta)"
        >
          <img :src="planta.imagineUrl" :alt="planta.nume" class="poza-planta" />
          <div class="info-planta">
            <h3>{{ planta.nume }}</h3>
            <p class="nume-stiintific">{{ planta.numeStiintific }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Fereastra Modală pentru Detalii -->
    <div v-if="plantaSelectata" class="modal-overlay" @click.self="inchideDetalii">
      <div class="modal-content">
        <button class="btn-inchide" @click="inchideDetalii">✖</button>
        
        <div class="modal-layout">
          <img :src="plantaSelectata.imagineUrl" class="poza-detaliu" />
          
          <div class="detalii-text">
            <h2 class="nume-mare">{{ plantaSelectata.nume }}</h2>
            <p class="nume-stiintific-mare">{{ plantaSelectata.numeStiintific }}</p>
            <hr class="separator-modal" />
            <div class="info-grid">
              <p><strong>🌿 Familie:</strong> {{ plantaSelectata.familie }}</p>
              <p><strong>🌸 Înflorire:</strong> {{ plantaSelectata.perioadaInflorire }}</p>
            </div>
            <div class="sectiune-descriere">
              <h4>Descriere:</h4>
              <p>{{ plantaSelectata.descriere }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')

const termenCautare = ref('')
const plantaSelectata = ref(null)

const deschideDetalii = (planta) => {
  plantaSelectata.value = planta
  document.body.style.overflow = 'hidden'
}
const inchideDetalii = () => {
  plantaSelectata.value = null
  document.body.style.overflow = 'auto'
}

// Baza de date "Globală" (am adăugat mai multe specii pentru a testa căutarea)
const bazaDeDateGlobala = ref([
  {
    id: 1, nume: 'Mușețel', numeStiintific: 'Matricaria chamomilla', familie: 'Asteraceae', perioadaInflorire: 'Mai - August',
    descriere: 'O plantă recunoscută mondial pentru proprietățile sale calmante.',
    imagineUrl: 'https://images.unsplash.com/photo-1621571439975-d162a74c7d0d?w=500&q=80'
  },
  {
    id: 2, nume: 'Păpădie', numeStiintific: 'Taraxacum officinale', familie: 'Asteraceae', perioadaInflorire: 'Aprilie - Septembrie',
    descriere: 'Buruiană nutritivă cu flori galbene care se transformă în puf.',
    imagineUrl: 'https://images.unsplash.com/photo-1550949826-38d77d121c2c?w=500&q=80'
  },
  {
    id: 3, nume: 'Levănțică', numeStiintific: 'Lavandula angustifolia', familie: 'Lamiaceae', perioadaInflorire: 'Iunie - August',
    descriere: 'Folosită în aromaterapie pentru reducerea stresului.',
    imagineUrl: 'https://images.unsplash.com/photo-1498940869186-b4bc5943f2a1?w=500&q=80'
  },
  {
    id: 4, nume: 'Mentă', numeStiintific: 'Mentha piperita', familie: 'Lamiaceae', perioadaInflorire: 'Iulie - Septembrie',
    descriere: 'Plantă extrem de aromată, ideală pentru ceaiuri și efecte revigorante.',
    imagineUrl: 'https://images.unsplash.com/photo-1628808168235-96bece30fc6e?w=500&q=80'
  },
  {
    id: 5, nume: 'Busuioc', numeStiintific: 'Ocimum basilicum', familie: 'Lamiaceae', perioadaInflorire: 'Iunie - Septembrie',
    descriere: 'Plantă aromatică esențială în gastronomie, cu un miros dulce-picant.',
    imagineUrl: 'https://images.unsplash.com/photo-1628156157834-08fb226cb0e9?w=500&q=80'
  }
])

// Magia căutării! Filtrăm baza de date în timp ce scrii.
const planteFiltrate = computed(() => {
  // Dacă nu ai scris nimic, arată toate plantele
  if (!termenCautare.value) return bazaDeDateGlobala.value

  // Transformăm ce ai scris în litere mici ca să nu conteze dacă scrii cu majuscule
  const textCautat = termenCautare.value.toLowerCase()

  return bazaDeDateGlobala.value.filter(planta => {
    const numePotrivire = planta.nume.toLowerCase().includes(textCautat)
    const stiintificPotrivire = planta.numeStiintific.toLowerCase().includes(textCautat)
    
    // Păstrăm planta dacă termenul se găsește MĂCAR într-unul din cele două nume
    return numePotrivire || stiintificPotrivire
  })
})
</script>

<style scoped>
/* Refolosim aceleași stiluri grozave de la Ierbarul Personal, cu o adăugire pentru Bara de Căutare */
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--verde-inchis); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.full-card { max-width: 1000px; } 
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 10px; }
.titlu { color: var(--verde-inchis); margin: 0; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }
.descriere { color: #666; margin-bottom: 20px; }

/* === STILURI NOI PENTRU BARA DE CĂUTARE === */
.zona-cautare {
  display: flex;
  align-items: center;
  background: white;
  border: 2px solid var(--verde-deschis);
  border-radius: 12px;
  padding: 5px 15px;
  margin-bottom: 30px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.02);
  transition: 0.3s;
}
.zona-cautare:focus-within { border-color: var(--verde-inchis); box-shadow: 0 4px 15px rgba(143, 175, 15, 0.2); }
.iconita-cautare { font-size: 1.2rem; margin-right: 10px; opacity: 0.6; }
.input-cautare {
  flex: 1; border: none; padding: 12px 5px; font-size: 1.1rem; outline: none; background: transparent; color: #333;
}
.mesaj-gol { text-align: center; padding: 30px; background: #fff5f5; border-radius: 12px; color: #e74c3c; font-weight: bold; margin-bottom: 20px;}

/* === STILURI CARDURI (La fel) === */
.grila-plante { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }
.card-planta { background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 2px solid var(--crem-fundal); transition: transform 0.3s ease; cursor: pointer; }
.card-planta:hover { transform: translateY(-5px); border-color: var(--verde-deschis); }
.poza-planta { width: 100%; height: 180px; object-fit: cover; }
.info-planta { padding: 15px; text-align: center; }
.info-planta h3 { margin: 0; color: var(--verde-inchis); }
.nume-stiintific { color: #888; font-style: italic; font-size: 0.9rem; margin: 5px 0 10px 0; }

/* === STILURI MODAL (La fel) === */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 1000; padding: 20px; box-sizing: border-box; }
.modal-content { background: var(--crem-fundal); width: 100%; max-width: 800px; border-radius: 20px; position: relative; overflow: hidden; box-shadow: 0 20px 50px rgba(0,0,0,0.3); animation: popUp 0.3s ease-out forwards; }
@keyframes popUp { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }
.btn-inchide { position: absolute; top: 15px; right: 15px; background: white; border: none; border-radius: 50%; width: 35px; height: 35px; font-size: 1.2rem; color: #555; cursor: pointer; box-shadow: 0 2px 5px rgba(0,0,0,0.2); z-index: 10; transition: 0.3s; }
.btn-inchide:hover { background: #fee; color: #e74c3c; transform: scale(1.1); }
.modal-layout { display: flex; flex-direction: row; }
.poza-detaliu { width: 45%; object-fit: cover; min-height: 300px; }
.detalii-text { padding: 30px; width: 55%; box-sizing: border-box; background: white; border-radius: 20px 0 0 20px; }
.nume-mare { margin: 0; color: var(--verde-inchis); font-size: 2rem; }
.nume-stiintific-mare { color: #888; font-style: italic; font-size: 1.1rem; margin-top: 5px; }
.separator-modal { border: none; border-top: 1px solid #eee; margin: 15px 0; }
.info-grid p { margin: 8px 0; color: #444; }
.info-grid strong { color: #333; }
.sectiune-descriere h4 { margin: 20px 0 5px 0; color: var(--verde-inchis); }
.sectiune-descriere p { color: #666; line-height: 1.6; font-size: 0.95rem; margin: 0; }

@media (max-width: 768px) {
  .modal-layout { flex-direction: column; }
  .poza-detaliu { width: 100%; height: 250px; min-height: auto; }
  .detalii-text { width: 100%; border-radius: 0; padding: 20px; }
}
</style>