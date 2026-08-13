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
          <img :src="planta.imagine_url || 'https://images.unsplash.com/photo-1628808168235-96bece30fc6e?w=500'" :alt="planta.nume_uzual" class="poza-planta" />
          
          <div class="info-planta">
            <h3>{{ planta.nume_uzual }}</h3>
            <p class="nume-stiintific">{{ planta.denumire_stiintifica }}</p>
            
            <button class="btn-favorite" @click.stop="adaugaLaFavorite(planta.id)" title="Salvează în Ierbarul Meu">
              ❤️ Salvează
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Fereastra Modală cu Scroll Global -->
    <div v-if="plantaSelectata" class="modal-overlay" @click.self="inchideDetalii">
      <div class="modal-content">
        <button class="btn-inchide" @click="inchideDetalii">✖</button>
        
        <div class="header-imagine">
          <img :src="plantaSelectata.imagine_url || 'https://images.unsplash.com/photo-1628808168235-96bece30fc6e?w=500'" class="poza-banner" />
        </div>

        <div class="detalii-text">
          <h2 class="nume-mare">{{ plantaSelectata.nume_uzual }}</h2>
          <p class="nume-stiintific-mare">{{ plantaSelectata.denumire_stiintifica }}</p>
          <hr class="separator-modal" />
          
          <div class="info-grid">
            <p><strong>🌿 Familie:</strong> {{ plantaSelectata.familie }}</p>
            <p><strong>📂 Categorie:</strong> {{ plantaSelectata.categorie_planta }}</p>
            <p><strong>🌸 Înflorire:</strong> {{ plantaSelectata.perioada_inflorire }}</p>
            <p><strong>🌱 Ciclu de viață:</strong> {{ plantaSelectata.ciclu_de_viata }}</p>
            <p><strong>🏷️ Tip plantă:</strong> {{ plantaSelectata.tip_planta }}</p>
            <p><strong>📏 Înălțime max:</strong> {{ plantaSelectata.inaltime_maxima }} metri</p>
            <p><strong>🍂 Poate fi uscată:</strong> {{ plantaSelectata.poate_fi_uscata ? 'Da' : 'Nu' }}</p>

            <template v-if="plantaSelectata.categorie_planta === 'FLOARE'">
              <p><strong>🌸 Nr. Petale:</strong> {{ plantaSelectata.numar_petale }}</p>
              <p><strong>🎨 Culoare:</strong> {{ plantaSelectata.culoare }}</p>
            </template>
            <template v-if="plantaSelectata.categorie_planta === 'ARBORE'">
              <p><strong>🌳 Coroană:</strong> {{ plantaSelectata.tip_coroana }}</p>
              <p><strong>🍃 Frunză:</strong> {{ plantaSelectata.tip_frunza }}</p>
              <p><strong>🍎 Pom fructifer:</strong> {{ plantaSelectata.pom_fructifer ? 'Da' : 'Nu' }}</p>
            </template>
            <template v-if="plantaSelectata.categorie_planta === 'ARBUST'">
              <p><strong>🍒 Produce fructe:</strong> {{ plantaSelectata.produce_fructe ? 'Da' : 'Nu' }}</p>
            </template>
            <template v-if="plantaSelectata.categorie_planta === 'IERBURI'">
              <p><strong>🌾 Tip tulpină:</strong> {{ plantaSelectata.tip_tulpina }}</p>
            </template>
          </div>

          <div class="sectiune-descriere">
            <h4>Descriere:</h4>
            <p>{{ plantaSelectata.descriere }}</p>
          </div>

          <div v-if="plantaSelectata.locatie" class="sectiune-habitat-harta">
            <hr class="separator-modal" />
            <h4>🌍 Habitat Natural:</h4>
            <p class="text-habitat">{{ plantaSelectata.locatie }}</p>

            <h4 class="titlu-harta">📍 Locații pe Hartă:</h4>
            <iframe 
              width="100%" 
              height="250" 
              frameborder="0" 
              class="harta-iframe"
              :src="'https://maps.google.com/maps?q=' + encodeURIComponent(plantaSelectata.locatie) + '&output=embed'" 
              allowfullscreen>
            </iframe>
            <p class="nota-comunitate"><em>* În curând: Aici vor apărea locațiile exacte în care alți pasionați au descoperit această plantă! 🌿</em></p>
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
/* CSS PENTRU PAGINĂ */
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
.btn-favorite { background: #ffebeb; color: #e74c3c; border: 1px solid #ffb3b3; padding: 8px 15px; border-radius: 20px; font-size: 0.9rem; font-weight: bold; cursor: pointer; transition: 0.3s; margin-top: 10px; }
.btn-favorite:hover { background: #e74c3c; color: white; }

/* =========================================
   NOUL CSS PENTRU MODAL (CU SCROLL GLOBAL)
   ========================================= */
.modal-overlay { 
  position: fixed; 
  top: 0; left: 0; 
  width: 100vw; height: 100vh; 
  background: rgba(0, 0, 0, 0.7); 
  backdrop-filter: blur(5px); 
  display: flex; 
  justify-content: center; 
  align-items: center; 
  z-index: 9999; 
  padding: 20px; 
  box-sizing: border-box;
}

.modal-content { 
  background: #ffffff; 
  width: 100%; 
  max-width: 550px; 
  border-radius: 16px; 
  position: relative; 
  display: flex; 
  flex-direction: column; 
  max-height: 85vh; 
  overflow-y: auto; /* SCROLL-UL ESTE ACUM PE TOATĂ FEREASTRA */
  overflow-x: hidden;
  box-shadow: 0 20px 50px rgba(0,0,0,0.3); 
  animation: popUp 0.3s ease-out forwards; 
}
@keyframes popUp { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }

/* Scrollbar personalizat aplicat întregului modal */
.modal-content::-webkit-scrollbar { width: 6px; }
.modal-content::-webkit-scrollbar-thumb { background-color: var(--verde-deschis); border-radius: 10px; }
.modal-content::-webkit-scrollbar-track { background: transparent; }

.btn-inchide { 
  position: absolute; top: 15px; right: 15px; 
  background: rgba(255, 255, 255, 0.9); border: none; border-radius: 50%; 
  width: 36px; height: 36px; font-size: 1.2rem; color: #333; 
  cursor: pointer; box-shadow: 0 2px 10px rgba(0,0,0,0.2); 
  z-index: 10; display: flex; justify-content: center; align-items: center; 
  transition: 0.2s; 
}
.btn-inchide:hover { background: #fee; color: #e74c3c; transform: scale(1.1); }

.header-imagine { 
  width: 100%; 
  height: 500px; /* Înălțime mai bună și echilibrată */
  flex-shrink: 0; 
  background: #f0f0f0; 
  border-radius: 16px 16px 0 0; /* Colțuri rotunjite sus */
}
.poza-banner { 
  width: 100%; 
  height: 100%; 
  object-fit: cover; 
  border-radius: 16px 16px 0 0; /* Aplicăm rotunjirea și direct pe imagine */
}

.detalii-text { 
  padding: 25px; 
  flex: 1; 
  /* Am scos overflow-y de aici */
}

.nume-mare { margin: 0; color: var(--verde-inchis); font-size: 2rem; }
.nume-stiintific-mare { color: #888; font-style: italic; font-size: 1.05rem; margin-top: 5px; }
.separator-modal { border: none; border-top: 1px solid #eee; margin: 15px 0; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.info-grid p { margin: 5px 0; color: #444; font-size: 0.9rem; }
.info-grid strong { color: #333; }

.sectiune-descriere h4 { margin: 20px 0 10px 0; color: var(--verde-inchis); }
.sectiune-descriere p { color: #555; line-height: 1.6; font-size: 0.95rem; margin: 0; }

.sectiune-habitat-harta h4 { margin: 20px 0 10px 0; color: var(--verde-inchis); }
.text-habitat { color: #555; line-height: 1.5; font-size: 0.9rem; font-style: italic; margin: 0; background: #f4faeb; padding: 12px; border-left: 4px solid var(--verde-deschis); border-radius: 0 8px 8px 0; }
.harta-iframe { border: 0; border-radius: 12px; margin-top: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }
.nota-comunitate { font-size: 0.8rem; color: #888; text-align: center; margin-top: 10px; }

@media (max-width: 500px) { .info-grid { grid-template-columns: 1fr; } .header-imagine { height: 250px; } }
</style>