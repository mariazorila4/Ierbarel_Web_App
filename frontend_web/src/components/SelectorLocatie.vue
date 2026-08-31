<template>
  <div class="selector-locatie-wrapper">
    <div class="header-locatie">
      <h3>📍 Setează locația descoperirii</h3>
      <p class="adresa-text">{{ seIncarcaAdresa ? 'Se caută adresa...' : adresaCurenta }}</p>
    </div>

    <div class="map-container-relative">
      <!-- 🔍 BARA DE CĂUTARE PESTE HARTĂ -->
      <div class="bara-cautare-harta">
        <input 
          type="text" 
          v-model="textCautat" 
          @keyup.enter="cautaAdresaPeHarta"
          placeholder="Caută o adresă sau un loc (ex: Parcul Herăstrău)..." 
          class="input-cautare-harta"
        />
        <button type="button" @click="cautaAdresaPeHarta" class="btn-cauta-harta" :disabled="seCauta">
          {{ seCauta ? '⏳' : '🔍' }}
        </button>
      </div>

      <div ref="mapContainer" class="harta"></div>

      <!-- Pin FIX stil Bolt -->
      <div class="pin-centru">
        <div class="pin-icon">🍃</div>
        <div class="pin-umbra"></div>
      </div>

      <button type="button" @click="obtineLocatiaGPS" class="btn-gps" title="Mergi la locația mea">
        🎯 Locația Mea
      </button>
    </div>

    <button type="button" @click="confirmaLocatia" class="btn-confirma" :disabled="seIncarcaAdresa">
      Confirmă Această Locație
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'

const emit = defineEmits(['locatie-selectata'])

const mapContainer = ref(null)
let mapInstance = null

const textCautat = ref('')
const seCauta = ref(false)

const adresaCurenta = ref('București, România')
const seIncarcaAdresa = ref(false)
const coordonateCurente = ref({ lat: 44.4268, lng: 26.1025 })

onMounted(async () => {
  await initHarta()
  obtineLocatiaGPS()
})

const initHarta = async () => {
  await nextTick()
  if (!window.L) {
    const link = document.createElement('link')
    link.rel = 'stylesheet'
    link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'
    document.head.appendChild(link)

    await new Promise((resolve) => {
      const script = document.createElement('script')
      script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'
      script.onload = resolve
      document.head.appendChild(script)
    })
  }

  const L = window.L
  mapInstance = L.map(mapContainer.value, {
    zoomControl: false
  }).setView([coordonateCurente.value.lat, coordonateCurente.value.lng], 15)

  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap'
  }).addTo(mapInstance)

  mapInstance.on('moveend', () => {
    const center = mapInstance.getCenter()
    coordonateCurente.value = { lat: center.lat, lng: center.lng }
    transformaCoordonateInAdresa(center.lat, center.lng)
  })
}

// 🔍 Căutare pe hartă în limba Română cu Fallback inteligent
const cautaAdresaPeHarta = async () => {
  if (!textCautat.value.trim() || !mapInstance) return
  
  seCauta.value = true
  const queryCurat = textCautat.value.trim()
  try {
    // 💡 1. Încercăm căutarea exactă
    let url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(queryCurat)}&addressdetails=1&limit=1&accept-language=ro`
    let res = await fetch(url)
    let data = await res.json()

    // 💡 2. FALLBACK: Dacă nu găsește nimic, și utilizatorul a scris o adresă cu virgulă (ex: Strada X, Orasul Y)
    // tăiem detaliile prea specifice (cum ar fi nr. blocului sau primele cuvinte care încurcă)
    if ((!data || data.length === 0) && queryCurat.includes(',')) {
      const piese = queryCurat.split(',')
      const adresaSimplificata = piese.slice(-2).join(',') // Păstrăm doar ultimele 2 (de obicei stradă și oraș)
      url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(adresaSimplificata)}&addressdetails=1&limit=1&accept-language=ro`
      res = await fetch(url)
      data = await res.json()
    }

    if (data && data.length > 0) {
      const lat = parseFloat(data[0].lat)
      const lon = parseFloat(data[0].lon)
      mapInstance.flyTo([lat, lon], 17, { animate: true, duration: 1.5 })
      textCautat.value = ''
    } else {
      alert("Nu am găsit nicio locație cu acest nume. Încearcă să scrii doar strada și orașul (ex: Bulevardul Magheru, București).")
    }
  } catch (e) {
    console.error("Eroare la căutarea adresei:", e)
  } finally {
    seCauta.value = false
  }
}

// 💡 Reverse Geocoding în Limba Română + Formatat curat
const transformaCoordonateInAdresa = async (lat, lng) => {
  seIncarcaAdresa.value = true
  try {
    // 💡 Parametrul accept-language=ro forțează răspunsul în Română
    const res = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1&accept-language=ro`)
    const data = await res.json()
    
    if (data && data.address) {
      const addr = data.address

      const poi = addr.building || addr.amenity || addr.leisure || addr.tourism || addr.historic || addr.shop || ''
      const strada = addr.road || addr.pedestrian || addr.footway || ''
      const numar = addr.house_number ? `Nr. ${addr.house_number}` : ''
      const oras = addr.city || addr.town || addr.village || addr.suburb || addr.city_district || ''

      const piese = []
      if (poi && typeof poi === 'string') piese.push(poi)
      if (strada) piese.push(numar ? `${strada} ${numar}` : strada)
      if (oras) piese.push(oras)

      if (piese.length > 0) {
        adresaCurenta.value = piese.join(', ')
      } else {
        adresaCurenta.value = data.display_name.split(',').slice(0, 3).join(',')
      }
    } else {
      adresaCurenta.value = "Locație necunoscută"
    }
  } catch (e) {
    adresaCurenta.value = "Nu s-a putut obține adresa."
  } finally {
    seIncarcaAdresa.value = false
  }
}

const obtineLocatiaGPS = () => {
  if (navigator.geolocation) {
    seIncarcaAdresa.value = true
    adresaCurenta.value = "Se caută semnal GPS..."
    
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude
        const lng = position.coords.longitude
        mapInstance.flyTo([lat, lng], 17, { animate: true, duration: 1.5 })
      },
      (error) => {
        console.warn("Eroare GPS:", error.message)
        seIncarcaAdresa.value = false
        adresaCurenta.value = "Acces GPS respins. Poți muta harta manual."
      },
      { enableHighAccuracy: true, timeout: 5000 }
    )
  }
}

const confirmaLocatia = () => {
  emit('locatie-selectata', {
    adresa: adresaCurenta.value,
    latitudine: coordonateCurente.value.lat,
    longitudine: coordonateCurente.value.lng
  })
}
</script>

<style scoped>
.selector-locatie-wrapper { display: flex; flex-direction: column; gap: 15px; background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 20px rgba(0,0,0,0.1); border: 2px solid var(--verde-deschis); }
.header-locatie { padding: 15px 20px 5px; text-align: center; }
.header-locatie h3 { margin: 0 0 5px 0; color: var(--verde-inchis); font-size: 1.1rem; }
.adresa-text { margin: 0; font-weight: bold; color: #555; font-size: 0.95rem; background: #f4f9ed; padding: 8px 12px; border-radius: 8px; display: inline-block; }
.map-container-relative { position: relative; width: 100%; height: 320px; }

.bara-cautare-harta { position: absolute; top: 15px; left: 50%; transform: translateX(-50%); z-index: 20; width: 90%; max-width: 420px; display: flex; background: white; border-radius: 25px; padding: 4px 6px; box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2); border: 1px solid #ddd; }
.input-cautare-harta { flex: 1; border: none; padding: 8px 15px; border-radius: 20px; font-size: 0.9rem; outline: none; }
.btn-cauta-harta { background: var(--verde-inchis); color: white; border: none; width: 34px; height: 34px; border-radius: 50%; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 0.9rem; transition: 0.2s; }
.btn-cauta-harta:hover:not(:disabled) { background: var(--verde-deschis); color: #333; }

.harta { width: 100%; height: 100%; z-index: 1; }
.pin-centru { position: absolute; top: 50%; left: 50%; transform: translate(-50%, -100%); z-index: 10; pointer-events: none; display: flex; flex-direction: column; align-items: center; }
.pin-icon { font-size: 2.5rem; filter: drop-shadow(0 4px 5px rgba(0,0,0,0.4)); animation: bouncePin 1s infinite alternate ease-in-out; }
.pin-umbra { width: 12px; height: 6px; background: rgba(0, 0, 0, 0.4); border-radius: 50%; margin-top: -5px; }

@keyframes bouncePin { from { transform: translateY(0); } to { transform: translateY(-8px); } }

.btn-gps { position: absolute; bottom: 20px; right: 20px; z-index: 10; background: white; border: 2px solid var(--verde-inchis); color: var(--verde-inchis); padding: 8px 14px; border-radius: 30px; font-weight: bold; font-size: 0.85rem; cursor: pointer; box-shadow: 0 4px 10px rgba(0,0,0,0.15); transition: 0.2s; }
.btn-gps:hover { background: var(--verde-inchis); color: white; }
.btn-confirma { margin: 0 15px 15px; background: var(--verde-inchis); color: white; border: none; padding: 14px; border-radius: 12px; font-size: 1rem; font-weight: bold; cursor: pointer; transition: background 0.3s; }
.btn-confirma:hover:not(:disabled) { background: var(--verde-deschis); color: #333; }
.btn-confirma:disabled { background: #ccc; cursor: not-allowed; }
</style>