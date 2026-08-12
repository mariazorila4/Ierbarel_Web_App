<template>
  <div class="page-wrapper">
    <div class="glass-card full-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi la Dashboard</button>
        <h2 class="titlu">🌍 Ierbarul Global</h2>
      </div>

      <p class="descriere">Explorează enciclopedia noastră. Caută o plantă după numele ei comun sau denumirea științifică.</p>

      <div class="zona-cautare">
        <span class="iconita-cautare">🔍</span>
        <input 
          type="text" 
          v-model="termenCautare" 
          placeholder="Caută o plantă (ex: Păpădie, Taraxacum...)" 
          class="input-cautare"
        />
      </div>

      <div v-if="planteFiltrate.length === 0 && !seIncarca" class="mesaj-gol">
        <p>Nu am găsit nicio plantă care să se potrivească cu căutarea: "<strong>{{ termenCautare }}</strong>".</p>
      </div>
      
      <p v-if="seIncarca" style="text-align:center;">Se aduc plantele din baza de date... 🌿</p>

      <div class="grila-plante">
        <div 
          v-for="planta in planteFiltrate" 
          :key="planta.id" 
          class="card-planta"
          @click="deschideDetalii(planta)"
        >
          <!-- Folosim o imagine default dacă planta din baza de date nu are încă una -->
          <img :src="planta.imagineUrl || 'https://images.unsplash.com/photo-1628808168235-96bece30fc6e?w=500&q=80'" :alt="planta.nume_uzual" class="poza-planta" />
          
          <div class="info-planta">
            <h3>{{ planta.nume_uzual }}</h3>
            <p class="nume-stiintific">{{ planta.denumire_stiintifica }}</p>
            
            <!-- BUTONUL DE SALVARE (Atenție la .stop) -->
            <button class="btn-favorite" @click.stop="adaugaLaFavorite(planta.id)" title="Salvează în Ierbarul Meu">
              ❤️ Salvează
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Fereastra Modală -->
    <div v-if="plantaSelectata" class="modal-overlay" @click.self="inchideDetalii">
      <div class="modal-content">
        <button class="btn-inchide" @click="inchideDetalii">✖</button>
        <div class="modal-layout">
          <img :src="plantaSelectata.imagineUrl || 'https://images.unsplash.com/photo-1628808168235-96bece30fc6e?w=500&q=80'" class="poza-detaliu" />
          <div class="detalii-text">
            <h2 class="nume-mare">{{ plantaSelectata.nume_uzual }}</h2>
            <p class="nume-stiintific-mare">{{ plantaSelectata.denumire_stiintifica }}</p>
            <hr class="separator-modal" />
            <div class="info-grid">
              <p><strong>🌿 Familie:</strong> {{ plantaSelectata.familie }}</p>
              <p><strong>🌸 Înflorire:</strong> {{ plantaSelectata.perioada_inflorire }}</p>
              <p><strong>🌱 Ciclu de viață:</strong> {{ plantaSelectata.ciclu_de_viata }}</p>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')

const termenCautare = ref('')
const plantaSelectata = ref(null)
const bazaDeDateGlobala = ref([])
const seIncarca = ref(true)

const deschideDetalii = (planta) => {
  plantaSelectata.value = planta
  document.body.style.overflow = 'hidden'
}

const inchideDetalii = () => {
  plantaSelectata.value = null
  document.body.style.overflow = 'auto'
}

// 1. PRELUĂM PLANTELE DIN JAVA LA DESCHIDEREA PAGINII
onMounted(async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const raspuns = await axios.get('http://localhost:8080/api/plante', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    bazaDeDateGlobala.value = raspuns.data
  } catch (eroare) {
    console.error("Eroare la preluarea plantelor:", eroare)
  } finally {
    seIncarca.value = false
  }
})

// 2. FUNCȚIA DE SALVARE ÎN IERBAR
const adaugaLaFavorite = async (plantaId) => {
  try {
    const token = localStorage.getItem('jwt_token')
    const raspuns = await axios.post(`http://localhost:8080/api/plante/ierbar-personal/${plantaId}`, {}, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    alert("🌿 " + raspuns.data.mesaj)
  } catch (eroare) {
    console.error("Eroare la salvare:", eroare)
    alert("A apărut o eroare la salvarea plantei.")
  }
}

// Magia căutării (adaptată pentru variabilele din baza de date ex: nume_uzual)
const planteFiltrate = computed(() => {
  if (!termenCautare.value) return bazaDeDateGlobala.value
  const textCautat = termenCautare.value.toLowerCase()
  return bazaDeDateGlobala.value.filter(planta => {
    const nume = planta.nume_uzual ? planta.nume_uzual.toLowerCase() : ''
    const stiintific = planta.denumire_stiintifica ? planta.denumire_stiintifica.toLowerCase() : ''
    return nume.includes(textCautat) || stiintific.includes(textCautat)
  })
})
</script>

<style scoped>
/* PĂSTREAZĂ TOT CSS-UL TĂU DE AICI, MAI ADAUGĂ DOAR BUTONUL */
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--verde-inchis); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.full-card { max-width: 1000px; } 
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 10px; }
.titlu { color: var(--verde-inchis); margin: 0; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }
.descriere { color: #666; margin-bottom: 20px; }
.zona-cautare { display: flex; align-items: center; background: white; border: 2px solid var(--verde-deschis); border-radius: 12px; padding: 5px 15px; margin-bottom: 30px; box-shadow: 0 4px 10px rgba(0,0,0,0.02); transition: 0.3s; }
.zona-cautare:focus-within { border-color: var(--verde-inchis); box-shadow: 0 4px 15px rgba(143, 175, 15, 0.2); }
.iconita-cautare { font-size: 1.2rem; margin-right: 10px; opacity: 0.6; }
.input-cautare { flex: 1; border: none; padding: 12px 5px; font-size: 1.1rem; outline: none; background: transparent; color: #333; }
.mesaj-gol { text-align: center; padding: 30px; background: #fff5f5; border-radius: 12px; color: #e74c3c; font-weight: bold; margin-bottom: 20px;}
.grila-plante { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }
.card-planta { background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 2px solid var(--crem-fundal); transition: transform 0.3s ease; cursor: pointer; }
.card-planta:hover { transform: translateY(-5px); border-color: var(--verde-deschis); }
.poza-planta { width: 100%; height: 180px; object-fit: cover; }
.info-planta { padding: 15px; text-align: center; }
.info-planta h3 { margin: 0; color: var(--verde-inchis); }
.nume-stiintific { color: #888; font-style: italic; font-size: 0.9rem; margin: 5px 0 10px 0; }

/* STIL NOU PENTRU BUTONUL DE SALVARE */
.btn-favorite { background: #ffebeb; color: #e74c3c; border: 1px solid #ffb3b3; padding: 8px 15px; border-radius: 20px; font-size: 0.9rem; font-weight: bold; cursor: pointer; transition: 0.3s; margin-top: 10px; }
.btn-favorite:hover { background: #e74c3c; color: white; }

/* STILURI MODAL */
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
@media (max-width: 768px) { .modal-layout { flex-direction: column; } .poza-detaliu { width: 100%; height: 250px; min-height: auto; } .detalii-text { width: 100%; border-radius: 0; padding: 20px; } }
</style>