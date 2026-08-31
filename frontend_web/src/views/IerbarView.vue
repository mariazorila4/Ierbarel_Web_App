<template>
  <div class="page-wrapper">
    <div class="glass-card full-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi la Dashboard</button>
        <h2 class="titlu">📚 Ierbarul Meu</h2>
      </div>

      <p class="descriere">Aici sunt toate plantele pe care le-ai identificat personal sau le-ai salvat din catalog. Apasă pe oricare pentru detalii.</p>
      
      <p v-if="planteleMele.length === 0 && !seIncarca" class="mesaj-gol">
        Ierbarul tău este gol! Scanează o floare sau explorează Ierbarul Global pentru a adăuga plante. 🌱
      </p>

      <p v-if="seIncarca" style="text-align:center;">Se aduc plantele din colecția ta... 🌿</p>

      <div class="grila-plante">
        <div 
          v-for="planta in planteleMele" 
          :key="planta.esteScanata ? 'scan_' + planta.id : 'fav_' + planta.id" 
          class="card-planta"
          @click="deschideDetalii(planta)"
        >
          <img :src="planta.imagineUrl || planta.imagine_url || (planta.planta && planta.planta.imagine_url) || 'https://images.unsplash.com/photo-1550949826-38d77d121c2c?w=500'" :alt="planta.nume_uzual" class="poza-planta" />
          
          <div class="info-planta">
            <h3>{{ planta.nume_uzual || (planta.planta && planta.planta.nume_uzual) || 'Plantă Scanată' }}</h3>
            <p class="nume-stiintific">{{ planta.denumire_stiintifica || (planta.planta && planta.planta.denumire_stiintifica) }}</p>
            
            <span v-if="planta.esteScanata" class="tag-tip tag-scanat">📸 Scanare Personală</span>
            <span v-else class="tag-tip tag-favorit">❤️ Salvat din Catalog</span>

            <button class="btn-stergere" @click.stop="stergePlanta(planta)" title="Elimină din colecție">
              🗑️ Șterge
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 1. MODAL DETALII PLANTA -->
    <div v-if="plantaSelectata" class="modal-overlay" @click.self="inchideDetalii">
      <div class="modal-content">
        <button class="btn-inchide" @click="inchideDetalii">✖</button>
        
        <div class="header-imagine">
          <img :src="plantaSelectata.imagineUrl || plantaSelectata.imagine_url || (plantaSelectata.planta && plantaSelectata.planta.imagine_url) || 'https://images.unsplash.com/photo-1550949826-38d77d121c2c?w=500'" class="poza-banner" />
        </div>

        <div class="detalii-text">
          <h2 class="nume-mare">{{ plantaSelectata.nume_uzual || (plantaSelectata.planta && plantaSelectata.planta.nume_uzual) }}</h2>
          <p class="nume-stiintific-mare">{{ plantaSelectata.denumire_stiintifica || (plantaSelectata.planta && plantaSelectata.planta.denumire_stiintifica) }}</p>
          <hr class="separator-modal" />
          
          <div class="info-grid">
            <p><strong>🌿 Familie:</strong> {{ plantaSelectata.familie || (plantaSelectata.planta && plantaSelectata.planta.familie) || 'Botanică' }}</p>
            <p><strong>📂 Categorie:</strong> {{ plantaSelectata.categorie_planta || (plantaSelectata.planta && plantaSelectata.planta.categorie_planta) || 'FLOARE' }}</p>
            <p><strong>🌸 Înflorire:</strong> {{ plantaSelectata.perioada_inflorire || (plantaSelectata.planta && plantaSelectata.planta.perioada_inflorire) || 'Primăvară-Vară' }}</p>
            <p><strong>🌱 Ciclu de viață:</strong> {{ plantaSelectata.ciclu_de_viata || (plantaSelectata.planta && plantaSelectata.planta.ciclu_de_viata) || 'PEREN' }}</p>
            <p><strong>🏷️ Tip plantă:</strong> {{ plantaSelectata.tip_planta || (plantaSelectata.planta && plantaSelectata.planta.tip_planta) || 'ORNAMENTALA' }}</p>
            <p><strong>📏 Înălțime max:</strong> {{ plantaSelectata.inaltime_maxima || (plantaSelectata.planta && plantaSelectata.planta.inaltime_maxima) || 30 }} metri</p>
          </div>

          <div class="sectiune-descriere">
            <h4>Descriere Botanică:</h4>
            <p>{{ plantaSelectata.descriere || (plantaSelectata.planta && plantaSelectata.planta.descriere) || 'Exemplar din colecția ta personală.' }}</p>
          </div>

          <!-- DETALII SCANARE PERSONALĂ & BUTON DE PUBLICARE GLOBALĂ -->
          <div v-if="plantaSelectata.esteScanata" class="sectiune-personal-info">
            <hr class="separator-modal" />
            <h4>📍 Detaliile Capturii Tale:</h4>
            <p><strong>Locație:</strong> {{ plantaSelectata.locatie || 'Nespecificată' }}</p>
            <p><strong>Data scanării:</strong> {{ formateazaData(plantaSelectata.dataAdaugarii) }}</p>

            <div class="actiuni-captura-modal">
              <button 
                v-if="!plantaSelectata.este_publica && !plantaSelectata.estePublica" 
                @click="incepeProcesPublicare(plantaSelectata)" 
                class="btn-publica-global"
              >
                🌐 Publică în Galeria Comunității
              </button>
              <div v-else class="badge-publicat-global">
                ✅ Această fotografie este publică în Galeria Globală
              </div>
            </div>
          </div>

          <!-- HABITAT NATURAL (PENTRU PLANTE SALVATE DIN CATALOG) -->
          <div v-if="!plantaSelectata.esteScanata && (plantaSelectata.locatie || (plantaSelectata.planta && plantaSelectata.planta.locatie))" class="sectiune-habitat">
            <hr class="separator-modal" />
            <h4>🌍 Habitat Natural:</h4>
            <p class="text-habitat">{{ plantaSelectata.locatie || plantaSelectata.planta.locatie }}</p>
          </div>

          <!-- BUTON POP-UP GALERIE (PENTRU PLANTELE SALVATE DIN CATALOG) -->
          <div v-if="!plantaSelectata.esteScanata" class="sectiune-galerie-buton">
            <hr class="separator-modal" />
            <button class="btn-deschide-galerie" @click="arataPopUpGalerie = true">
              🖼️ Vezi Galeria Foto a Comunității ({{ galerieComunitate.length }} poze)
            </button>
          </div>

        </div>
      </div>
    </div>

    <!-- 2. MODAL HARTĂ PENTRU CONFIRMARE/MODIFICARE LOCAȚIE LA PUBLICARE -->
    <div v-if="arataHartaPublicare" class="modal-overlay z-top" @click.self="arataHartaPublicare = false">
      <div class="modal-content-locatie">
        <button class="btn-inchide" @click="arataHartaPublicare = false">✖</button>
        <!-- Aici am integrat componenta ta SelectorLocatie care centrează fix ca la Bolt -->
        <SelectorLocatie @locatie-selectata="confirmaPublicareCuHarta" />
      </div>
    </div>

    <!-- 3. POP-UP DEDICAT GALERIEI FOTO -->
    <div v-if="arataPopUpGalerie" class="modal-overlay z-top" @click.self="arataPopUpGalerie = false">
      <div class="popup-galerie-container">
        <button class="btn-inchide-galerie" @click="arataPopUpGalerie = false">✖</button>
        <h3 class="titlu-galerie-popup">📸 Galerie Foto Comunitate - {{ plantaSelectata?.nume_uzual || plantaSelectata?.planta?.nume_uzual }}</h3>

        <p v-if="galerieComunitate.length === 0" class="subtext-gol-popup">
          Nu există încă fotografii adăugate de alți utilizatori pentru această floare. 🌿
        </p>

        <div v-else class="lista-fotografii-popup">
          <div v-for="item in galerieComunitate" :key="item.id" class="card-poza-mare">
            <img :src="item.imagineUrl" class="poza-full" alt="Captură comunitate" />
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
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import SelectorLocatie from '../components/SelectorLocatie.vue'

const notificare = inject('notificare')
const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')

const plantaSelectata = ref(null)
const planteleMele = ref([])
const seIncarca = ref(true)

const galerieComunitate = ref([])
const locatiiSpecie = ref([])
const arataPopUpGalerie = ref(false)

const arataHartaPublicare = ref(false)
const capturaDePublicat = ref(null)

onMounted(async () => {
  await incarcaIerbarulPersonal()
})

const incarcaIerbarulPersonal = async () => {
  seIncarca.value = true
  try {
    const token = localStorage.getItem('jwt_token')
    const userId = localStorage.getItem('user_id')
    const config = { headers: { 'Authorization': `Bearer ${token}` } }

    const [resUser, resCapturi] = await Promise.allSettled([
      axios.get(`/api/users/general/${userId}`, config),
      axios.get(`/api/plante/ierbar-personal`, config)
    ])

    const favoriteGlobal = (resUser.status === 'fulfilled' && resUser.value.data.planteFavorite) 
      ? resUser.value.data.planteFavorite.map(p => ({ ...p, esteScanata: false })) 
      : []

    const capturiScanate = (resCapturi.status === 'fulfilled' && resCapturi.value.data) 
      ? resCapturi.value.data.map(c => ({
          ...c,
          id: c.id, 
          nume_uzual: c.planta?.nume_uzual || 'Plantă Scanată',
          denumire_stiintifica: c.planta?.denumire_stiintifica || '',
          familie: c.planta?.familie || 'Asteraceae',
          descriere: c.planta?.descriere || 'Proaspăt scanată',
          perioada_inflorire: c.planta?.perioada_inflorire,
          ciclu_de_viata: c.planta?.ciclu_de_viata,
          tip_planta: c.planta?.tip_planta,
          inaltime_maxima: c.planta?.inaltime_maxima,
          esteScanata: true 
        })) 
      : []

    planteleMele.value = [...capturiScanate, ...favoriteGlobal]
  } catch (eroare) {
    console.error("Eroare la preluarea ierbarului:", eroare)
  } finally {
    seIncarca.value = false
  }
}

const deschideDetalii = async (planta) => {
  plantaSelectata.value = planta
  document.body.style.overflow = 'hidden'

  if (!planta.esteScanata) {
    galerieComunitate.value = []
    locatiiSpecie.value = []

    const token = localStorage.getItem('jwt_token')
    const config = { headers: { 'Authorization': `Bearer ${token}` } }

    try {
      const resGalerie = await axios.get(`/api/plante/${planta.id}/galerie`, config)
      galerieComunitate.value = resGalerie.data || []
    } catch (err) {
      console.warn("Eroare galerie:", err)
    }

    try {
      const resLocatii = await axios.get(`/api/plante/${planta.id}/locatii`, config)
      locatiiSpecie.value = resLocatii.data || []
    } catch (err) {
      console.warn("Eroare locații:", err)
    }
  }
}

const inchideDetalii = () => {
  plantaSelectata.value = null
  arataPopUpGalerie.value = false
  galerieComunitate.value = []
  locatiiSpecie.value = []
  document.body.style.overflow = 'auto'
}

// ==========================================
// 💡 LOGICA INTELIGENTĂ DE PUBLICARE
// ==========================================

const incepeProcesPublicare = async (captura) => {
  capturaDePublicat.value = captura
  const areLocatieCurenta = captura.locatie && captura.locatie !== 'Nespecificată' && captura.locatie.trim() !== ''

  if (areLocatieCurenta) {
    // 1. Dacă ARE locație -> Întrebăm userul ce vrea să facă
    const vreaSaPubliceDirect = await notificare({
      titlu: "Confirmare Locație",
      mesaj: `Planta are deja setată locația: "${captura.locatie}".\nDorești să o publici așa? (Dacă apeși Anulează, vei putea alege altă locație pe hartă).`,
      tip: "info",
      esteConfirmare: true // Returnează true dacă dă "OK", false dacă dă "Anulează"
    })

    if (vreaSaPubliceDirect) {
      // 1.a. Apasă OK -> Publică direct fără hartă
      await executaPublicarea(captura.locatie)
    } else {
      // 1.b. Apasă Anulează -> Deschide Harta să modifice
      arataHartaPublicare.value = true
    }
  } else {
    // 2. Dacă NU ARE locație -> Trimis direct la hartă
    arataHartaPublicare.value = true
  }
}

// Metoda apelată când utilizatorul confirmă locația de pe harta din Modal
const confirmaPublicareCuHarta = async (dateLocatie) => {
  arataHartaPublicare.value = false
  const locatieFinala = dateLocatie.adresa.trim() !== '' ? dateLocatie.adresa.trim() : 'Nespecificată'
  await executaPublicarea(locatieFinala)
}

// Metoda finală care comunică cu Backend-ul
const executaPublicarea = async (adresaFinala) => {
  if (!capturaDePublicat.value) return
  const me = capturaDePublicat.value

  try {
    const token = localStorage.getItem('jwt_token')
    
    await axios.put(
      `/api/plante/capturi/${me.id}/publica`, 
      { locatie: adresaFinala },
      { headers: { 'Authorization': `Bearer ${token}` } }
    )

    // Actualizare vizuală în interfață pe loc
    me.este_publica = true
    me.estePublica = true
    me.locatie = adresaFinala

    await notificare({
      titlu: "Publicat în Galerie! 🌐",
      mesaj: "Fotografia ta a devenit publică și ajută comunitatea Ierbarului Global!",
      tip: "success"
    })
  } catch (eroare) {
    console.error("Eroare la publicare:", eroare)
    await notificare({
      titlu: "Eroare la Publicare",
      mesaj: "Nu am putut face fotografia publică. Încearcă din nou.",
      tip: "error"
    })
  } finally {
    capturaDePublicat.value = null
  }
}

// ==========================================

const formateazaData = (dataStr) => {
  if (!dataStr) return 'Recent'
  const d = new Date(dataStr)
  return d.toLocaleDateString('ro-RO', { day: 'numeric', month: 'long', year: 'numeric' })
}

const stergePlanta = async (planta) => {
  const numePlanta = planta.nume_uzual || (planta.planta && planta.planta.nume_uzual) || 'această plantă'
  const vreaSaSterga = await notificare({
    titlu: "Eliminare din Ierbar",
    mesaj: `Ești sigur că vrei să elimini "${numePlanta}" din colecția ta?`,
    tip: "error",
    esteConfirmare: true
  })

  if (!vreaSaSterga) return

  try {
    const token = localStorage.getItem('jwt_token')
    const config = { headers: { 'Authorization': `Bearer ${token}` } }

    if (planta.esteScanata) {
      await axios.delete(`/api/plante/captura/${planta.id}`, config)
    } else {
      await axios.delete(`/api/plante/ierbar-personal/${planta.id}`, config)
    }

    if (plantaSelectata.value && plantaSelectata.value.id === planta.id) {
      inchideDetalii()
    }

    await incarcaIerbarulPersonal()

    await notificare({
      titlu: "Plantă Eliminată 🌿",
      mesaj: "Planta a fost eliminată cu succes din Ierbarul tău.",
      tip: "success"
    })
  } catch (eroare) {
    console.error("Eroare la ștergere:", eroare)
  }
}
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--albastru-pastel); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.full-card { max-width: 1000px; } 
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 10px; }
.titlu { color: var(--albastru-pastel); margin: 0; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }
.descriere { color: #666; margin-bottom: 30px; }
.grila-plante { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }
.card-planta { background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 2px solid var(--crem-fundal); transition: transform 0.3s ease; cursor: pointer; }
.card-planta:hover { transform: translateY(-5px); border-color: var(--albastru-pastel); }
.poza-planta { width: 100%; height: 180px; object-fit: cover; }
.info-planta { padding: 15px; text-align: center; display: flex; flex-direction: column; align-items: center; }
.info-planta h3 { margin: 0; color: var(--verde-inchis); }
.nume-stiintific { color: #888; font-style: italic; font-size: 0.9rem; margin: 5px 0 10px 0; }

.tag-tip { font-size: 0.75rem; font-weight: bold; padding: 4px 10px; border-radius: 12px; margin-bottom: 10px; }
.tag-scanat { background: #eef7d2; color: var(--verde-inchis); }
.tag-favorit { background: #ffebeb; color: #e74c3c; }

.mesaj-gol { text-align: center; padding: 30px; background: #f9f9f9; border-radius: 12px; color: #666; font-style: italic; }
.btn-stergere { background: #f8d7da; color: #721c24; border: none; padding: 6px 15px; border-radius: 20px; font-size: 0.85rem; font-weight: bold; cursor: pointer; transition: 0.3s; }
.btn-stergere:hover { background: #e2aeb3; }

.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.7); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 9999; padding: 20px; box-sizing: border-box; }
.modal-content { background: #ffffff; width: 100%; max-width: 580px; border-radius: 16px; position: relative; display: flex; flex-direction: column; max-height: 85vh; overflow-y: auto; overflow-x: hidden; box-shadow: 0 20px 50px rgba(0,0,0,0.3); animation: popUp 0.3s ease-out forwards; }
@keyframes popUp { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }

.modal-content::-webkit-scrollbar { width: 6px; }
.modal-content::-webkit-scrollbar-thumb { background-color: var(--verde-deschis); border-radius: 10px; }

.btn-inchide { position: absolute; top: 15px; right: 15px; background: rgba(255, 255, 255, 0.9); border: none; border-radius: 50%; width: 36px; height: 36px; font-size: 1.2rem; cursor: pointer; z-index: 10; display: flex; justify-content: center; align-items: center; }
.header-imagine { width: 100%; height: 600px; flex-shrink: 0; background: #f0f0f0; }
.poza-banner { width: 100%; height: 100%; object-fit: cover; }
.detalii-text { padding: 25px; flex: 1; }
.nume-mare { margin: 0; color: var(--verde-inchis); font-size: 2rem; }
.nume-stiintific-mare { color: #888; font-style: italic; font-size: 1.05rem; margin-top: 5px; }
.separator-modal { border: none; border-top: 1px solid #eee; margin: 15px 0; }
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }

.sectiune-descriere h4, .sectiune-personal-info h4, .sectiune-habitat h4 { margin: 20px 0 10px 0; color: var(--verde-inchis); }
.sectiune-personal-info { background: #f4faeb; padding: 12px; border-radius: 10px; border-left: 4px solid var(--verde-deschis); }
.text-habitat { color: #555; font-size: 0.9rem; font-style: italic; background: #f4faeb; padding: 10px; border-left: 4px solid var(--verde-deschis); }

.actiuni-captura-modal { margin-top: 15px; text-align: center; }
.btn-publica-global {
  width: 100%;
  padding: 12px;
  background: #3b82f6;
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: bold;
  font-size: 0.95rem;
  cursor: pointer;
  transition: 0.3s;
  box-shadow: 0 4px 10px rgba(59, 130, 246, 0.2);
}
.btn-publica-global:hover {
  background: #2563eb;
  transform: translateY(-2px);
}
.badge-publicat-global {
  background: #ecfdf5;
  color: #10b981;
  padding: 10px;
  border-radius: 8px;
  font-weight: bold;
  font-size: 0.9rem;
  border: 1px solid #a7f3d0;
}

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

/* Modal Harta */
.modal-content-locatie { background: white; width: 100%; max-width: 550px; border-radius: 20px; padding: 20px; position: relative; box-shadow: 0 20px 50px rgba(0,0,0,0.3); }

/* POP-UP GALERIE FOTO LIGHTBOX */
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
.lista-fotografii-popup::-webkit-scrollbar-thumb { background-color: var(--verde-deschis); border-radius: 10px; }

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
</style>