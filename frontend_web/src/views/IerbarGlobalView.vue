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
            
            <button class="btn-favorite" @click.stop="adaugaLaFavorite(planta)" title="Salvează în Ierbarul Meu">
              ❤️ Salvează
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL PRINCIPAL: DETALII BOTANICE PLANTA -->
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
            <h4>Descriere Botanică:</h4>
            <p>{{ plantaSelectata.descriere }}</p>
          </div>

          <!-- HABITAT NATURAL (PUS DE ADMIN) -->
          <div v-if="plantaSelectata.locatie && plantaSelectata.locatie !== 'Nespecificată'" class="sectiune-habitat">
            <hr class="separator-modal" />
            <h4>🌍 Habitat Natural:</h4>
            <p class="text-habitat">{{ plantaSelectata.locatie }}</p>
          </div>

          <!-- ICON/BUTON PENTRU DESCHIDEREA GALERIEI -->
          <div class="sectiune-galerie-buton">
            <hr class="separator-modal" />
            <button class="btn-deschide-galerie" @click="arataPopUpGalerie = true">
              🖼️ Vezi Galeria Foto a Comunității ({{ galerieComunitate.length }} poze)
            </button>
          </div>

          <!-- HARTA CUMULATIVĂ A COMUNITĂȚII -->
          <div class="sectiune-habitat-harta">
            <hr class="separator-modal" />
            <h4>📍 Locațiile Descoperirilor pe Hartă:</h4>
            
            <iframe 
              v-if="googleMapsUrl"
              width="100%" 
              height="260" 
              frameborder="0" 
              class="harta-iframe"
              :src="googleMapsUrl" 
              allowfullscreen>
            </iframe>
            <p v-else class="text-habitat">Nu există încă locații înregistrate pe hartă pentru această specie.</p>
          </div>

        </div>
      </div>
    </div>

    <!-- POP-UP DEDICAT GALERIEI FOTO (LIGHTBOX) -->
    <div v-if="arataPopUpGalerie" class="modal-overlay z-top" @click.self="arataPopUpGalerie = false">
      <div class="popup-galerie-container">
        <button class="btn-inchide-galerie" @click="arataPopUpGalerie = false">✖</button>
        <h3 class="titlu-galerie-popup">📸 Galerie Foto Comunitate - {{ plantaSelectata?.nume_uzual }}</h3>

        <p v-if="galerieComunitate.length === 0" class="subtext-gol-popup">
          Nu există încă fotografii adăugate de alți utilizatori pentru această floare. 🌿
        </p>

        <div v-else class="lista-fotografii-popup">
          <div v-for="item in galerieComunitate" :key="item.id" class="card-poza-mare">
            <img :src="item.imagineUrl" class="poza-full" alt="Captură comunitate" />
            
            <!-- BADGE SUPRAPUS ÎN COLȚUL DIN DREAPTA SUS -->
            <div class="badge-autor">
              <span class="autor-name">👤 @{{ item.numeUtilizator || 'Anonim' }}</span>
              <span class="autor-locatie">📍 {{ item.locatie || 'Nespecificată' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const notificare = inject('notificare')
const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')

const termenCautare = ref('')
const plantaSelectata = ref(null)
const bazaDeDateGlobala = ref([])
const seIncarca = ref(true)

const galerieComunitate = ref([])
const locatiiSpecie = ref([])
const arataPopUpGalerie = ref(false)

const deschideDetalii = async (planta) => {
  plantaSelectata.value = planta
  document.body.style.overflow = 'hidden'
  
  galerieComunitate.value = []
  locatiiSpecie.value = []

  const token = localStorage.getItem('jwt_token')
  const config = { headers: { 'Authorization': `Bearer ${token}` } }

  try {
    const resGalerie = await axios.get(`http://localhost:8080/api/plante/${planta.id}/galerie`, config)
    galerieComunitate.value = resGalerie.data || []
  } catch (err) {
    console.warn("Nu s-a putut încărca galeria:", err)
    galerieComunitate.value = []
  }

  try {
    const resLocatii = await axios.get(`http://localhost:8080/api/plante/${planta.id}/locatii`, config)
    locatiiSpecie.value = resLocatii.data || []
  } catch (err) {
    console.warn("Nu s-au putut încărca locațiile:", err)
    locatiiSpecie.value = []
  }
}

const inchideDetalii = () => {
  plantaSelectata.value = null
  arataPopUpGalerie.value = false
  galerieComunitate.value = []
  locatiiSpecie.value = []
  document.body.style.overflow = 'auto'
}

const googleMapsUrl = computed(() => {
  const locatiiToate = [...locatiiSpecie.value]
  if (plantaSelectata.value?.locatie && plantaSelectata.value.locatie !== 'Nespecificată') {
    locatiiToate.push(plantaSelectata.value.locatie)
  }
  
  const unice = [...new Set(locatiiToate)].filter(l => l && l.trim() !== '')
  if (unice.length === 0) return ''
  
  const query = unice.join(' | ')
  return `https://maps.google.com/maps?q=${encodeURIComponent(query)}&output=embed`
})

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

const adaugaLaFavorite = async (planta) => {
  try {
    const token = localStorage.getItem('jwt_token')
    const raspuns = await axios.post(`http://localhost:8080/api/plante/ierbar-personal/${planta.id}`, {}, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    
    await notificare({
      titlu: "Plantă Salvată! 🌸",
      mesaj: raspuns.data.mesaj || `"${planta.nume_uzual}" a fost adăugată în Ierbarul tău personal!`,
      tip: "success"
    })

  } catch (eroare) {
    console.error("Eroare la salvare:", eroare)
    let mesajEroare = "A apărut o eroare la salvarea plantei."
    if (eroare.response && eroare.response.data) {
      mesajEroare = typeof eroare.response.data === 'object' 
        ? JSON.stringify(eroare.response.data, null, 2) 
        : eroare.response.data
    }

    await notificare({
      titlu: "Eroare la Salvare",
      mesaj: mesajEroare,
      tip: "error"
    })
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

.modal-overlay { 
  position: fixed; top: 0; left: 0; 
  width: 100vw; height: 100vh; 
  background: rgba(0, 0, 0, 0.7); 
  backdrop-filter: blur(5px); 
  display: flex; justify-content: center; align-items: center; 
  z-index: 9999; padding: 20px; box-sizing: border-box;
}

.modal-content { 
  background: #ffffff; width: 100%; max-width: 600px; 
  border-radius: 16px; position: relative; 
  display: flex; flex-direction: column; 
  max-height: 85vh; overflow-y: auto; overflow-x: hidden;
  box-shadow: 0 20px 50px rgba(0,0,0,0.3); 
  animation: popUp 0.3s ease-out forwards; 
}
@keyframes popUp { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }

.modal-content::-webkit-scrollbar { width: 6px; }
.modal-content::-webkit-scrollbar-thumb { background-color: var(--verde-deschis); border-radius: 10px; }

.btn-inchide { 
  position: absolute; top: 15px; right: 15px; 
  background: rgba(255, 255, 255, 0.9); border: none; border-radius: 50%; 
  width: 36px; height: 36px; font-size: 1.2rem; color: #333; 
  cursor: pointer; box-shadow: 0 2px 10px rgba(0,0,0,0.2); 
  z-index: 10; display: flex; justify-content: center; align-items: center; 
  transition: 0.2s; 
}
.btn-inchide:hover { background: #fee; color: #e74c3c; transform: scale(1.1); }

.header-imagine { width: 100%; height: 600px; flex-shrink: 0; background: #f0f0f0; }
.poza-banner { width: 100%; height: 100%; object-fit: cover; }

.detalii-text { padding: 25px; flex: 1; }
.nume-mare { margin: 0; color: var(--verde-inchis); font-size: 2rem; }
.nume-stiintific-mare { color: #888; font-style: italic; font-size: 1.05rem; margin-top: 5px; }
.separator-modal { border: none; border-top: 1px solid #eee; margin: 15px 0; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.info-grid p { margin: 5px 0; color: #444; font-size: 0.9rem; }

.sectiune-descriere h4, .sectiune-habitat h4, .sectiune-habitat-harta h4 { margin: 20px 0 10px 0; color: var(--verde-inchis); }
.sectiune-descriere p { color: #555; line-height: 1.6; font-size: 0.95rem; margin: 0; }

.text-habitat { color: #555; font-size: 0.9rem; font-style: italic; background: #f4faeb; padding: 10px; border-left: 4px solid var(--verde-deschis); }

.btn-deschide-galerie { 
  width: 100%; 
  padding: 14px; 
  background: var(--verde-inchis); 
  color: white; 
  border: none; 
  border-radius: 12px; 
  font-weight: bold; 
  font-size: 1rem; 
  cursor: pointer; 
  transition: 0.3s;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.btn-deschide-galerie:hover { 
  background: var(--verde-deschis); 
  color: #222; 
}

.z-top { z-index: 10000 !important; }
.popup-galerie-container { 
  background: #181818; 
  width: 90%; 
  max-width: 750px; 
  height: 85vh; 
  border-radius: 20px; 
  padding: 25px; 
  position: relative; 
  display: flex; 
  flex-direction: column; 
  box-shadow: 0 25px 60px rgba(0,0,0,0.8); 
  color: white; 
}
.btn-inchide-galerie { 
  position: absolute; top: 15px; right: 15px; 
  background: rgba(255,255,255,0.2); color: white; border: none; 
  border-radius: 50%; width: 36px; height: 36px; cursor: pointer; 
  z-index: 10; font-size: 1.2rem; transition: 0.2s;
}
.btn-inchide-galerie:hover { background: #e74c3c; }

.titlu-galerie-popup { margin: 0 0 20px 0; text-align: center; color: #eef7d2; font-size: 1.25rem; }
.subtext-gol-popup { text-align: center; color: #aaa; margin-top: 50px; font-style: italic; }

.lista-fotografii-popup { 
  display: flex; 
  flex-direction: column; 
  gap: 25px; 
  overflow-y: auto; 
  flex: 1; 
  padding-right: 10px; 
}
.lista-fotografii-popup::-webkit-scrollbar { width: 6px; }
.lista-fotografii-popup::-webkit-scrollbar-thumb { background: #444; border-radius: 10px; }

.card-poza-mare { 
  position: relative; 
  width: 100%; 
  border-radius: 14px; 
  overflow: hidden; 
  background: #000; 
  box-shadow: 0 8px 20px rgba(0,0,0,0.5);
}
.poza-full { 
  width: 100%; 
  max-height: 480px; 
  object-fit: contain; 
  display: block; 
}

.badge-autor { 
  position: absolute; 
  top: 15px; 
  right: 15px; 
  background: rgba(0, 0, 0, 0.75); 
  backdrop-filter: blur(6px); 
  color: white; 
  padding: 8px 14px; 
  border-radius: 20px; 
  display: flex; 
  flex-direction: column; 
  align-items: flex-end; 
  font-size: 0.85rem; 
  border: 1px solid rgba(255, 255, 255, 0.2); 
  box-shadow: 0 4px 15px rgba(0,0,0,0.3);
}
.autor-name { font-weight: bold; color: #eef7d2; }
.autor-locatie { font-size: 0.75rem; color: #ddd; opacity: 0.9; margin-top: 2px; }

.harta-iframe { border: 0; border-radius: 12px; margin-top: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }

@media (max-width: 500px) { .info-grid { grid-template-columns: 1fr; } .header-imagine { height: 220px; } }
</style>