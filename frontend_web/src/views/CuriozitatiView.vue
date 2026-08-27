<template>
  <div class="page-wrapper">
    <div class="glass-card wide-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <h2 class="titlu">📅 Calendarul Curiozităților</h2>
      </div>

      <p class="subtitlu">Apasă pe o zi din calendar pentru a descoperi secretul botanic ascuns!</p>

      <div v-if="seIncarca" class="stare-incarcare">
        <p>Se încarcă calendarul... ⏳</p>
      </div>

      <div v-else class="container-luni">
        <!-- GRUPARE PE FIECARE LUNĂ (ex: August 2026, Iulie 2026) -->
        <div v-for="(grup, numeLuna) in curiozitatiGrupatePeLuni" :key="numeLuna" class="sectiune-luna">
          <h3 class="titlu-luna">🗓️ {{ numeLuna }}</h3>
          
          <div class="grid-calendar">
            <div 
              v-for="item in grup" 
              :key="item.id"
              class="zi-card"
              :class="{ 'zi-selectata': curiozitateSelectata?.id === item.id, 'zi-azi': esteAzi(item.dataGenerare) }"
              @click="selecteazaCuriozitate(item)"
            >
              <span class="badge-data">{{ formateazaZiua(item.dataGenerare) }}</span>
              <div class="icon-indiciu">{{ item.iconita }}</div>
              <small class="nume-indiciu">{{ item.numePlanta }}</small>
            </div>
          </div>
        </div>
      </div>

      <!-- DETALIUL CURIOZITĂȚII SELECTATE -->
      <div v-if="curiozitateSelectata" class="detaliu-card card-roz-deschis">
        <div class="header-detaliu">
          <span class="badge-planta">🌿 {{ curiozitateSelectata.numePlanta }}</span>
          <span class="data-completa">📅 {{ curiozitateSelectata.dataGenerare }}</span>
        </div>
        <h3>{{ curiozitateSelectata.titlu }}</h3>
        <p>{{ curiozitateSelectata.curiozitate }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const istoriculCuriozitati = ref([])
const curiozitateSelectata = ref(null)
const seIncarca = ref(true)

const mergiInapoi = () => router.push('/dashboard')

// Extrage doar ziua din data (ex: din "2026-08-27" extrage "27")
const formateazaZiua = (dataStr) => {
  if (!dataStr) return ''
  return dataStr.split('-')[2]
}

const esteAzi = (dataStr) => {
  const azi = new Date().toISOString().split('T')[0]
  return dataStr === azi
}

const selecteazaCuriozitate = (item) => {
  curiozitateSelectata.value = item
}

// 💡 GRUPARE DINAMICĂ DUPĂ LUNĂ ȘI AN
const curiozitatiGrupatePeLuni = computed(() => {
  const grupuri = {}

  istoriculCuriozitati.value.forEach(item => {
    if (!item.dataGenerare) return

    const dataObj = new Date(item.dataGenerare)
    // Formatează numele lunii în limba română (ex: "August 2026")
    const numeLuna = dataObj.toLocaleDateString('ro-RO', { month: 'long', year: 'numeric' })
    const numeLunaCapitalizat = numeLuna.charAt(0).toUpperCase() + numeLuna.slice(1)

    if (!grupuri[numeLunaCapitalizat]) {
      grupuri[numeLunaCapitalizat] = []
    }
    grupuri[numeLunaCapitalizat].push(item)
  })

  return grupuri
})

const incarcaIstoric = async () => {
  seIncarca.value = true
  try {
    const token = localStorage.getItem('jwt_token')
    const raspuns = await axios.get('http://localhost:8080/api/plante/curiozitatea-zilei', {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    if (Array.isArray(raspuns.data)) {
      istoriculCuriozitati.value = raspuns.data
      if (raspuns.data.length > 0) {
        curiozitateSelectata.value = raspuns.data[0]
      }
    }
  } catch (e) {
    console.error("Eroare la încărcare:", e)
  } finally {
    seIncarca.value = false
  }
}

onMounted(() => {
  incarcaIstoric()
})
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.95); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--roz-deschis); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.wide-card { max-width: 750px; }
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 15px; }
.titlu { color: var(--roz-inchis); margin: 0; }
.subtitlu { text-align: center; color: #666; margin-bottom: 25px; font-size: 0.95rem; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }

/* Stiluri Grupuri Luni */
.container-luni { display: flex; flex-direction: column; gap: 25px; margin-bottom: 20px; }
.sectiune-luna { background: #fafafa; padding: 15px; border-radius: 16px; border: 1px solid #eee; }
.titlu-luna { margin: 0 0 15px 5px; color: var(--roz-inchis); font-size: 1.1rem; }

/* Grid Calendar */
.grid-calendar { display: grid; grid-template-columns: repeat(auto-fill, minmax(90px, 1fr)); gap: 12px; }
.zi-card { background: white; border: 2px solid #eee; border-radius: 14px; padding: 10px; text-align: center; cursor: pointer; transition: all 0.3s ease; }
.zi-card:hover { transform: translateY(-3px); box-shadow: 0 5px 12px rgba(0,0,0,0.08); border-color: var(--roz-deschis); }
.zi-selectata { border-color: var(--roz-inchis) !important; background: #fff5f7 !important; transform: scale(1.03); }
.zi-azi { border-color: #10b981; background: #ecfdf5; }

.badge-data { font-size: 0.8rem; font-weight: bold; color: #555; background: #f3f4f6; padding: 2px 8px; border-radius: 8px; }
.icon-indiciu { font-size: 2rem; margin: 6px 0 2px 0; }
.nume-indiciu { display: block; font-size: 0.7rem; color: #555; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

/* Detaliu Card */
.detaliu-card { background: #fff0f3; border-left: 5px solid var(--roz-inchis); padding: 22px; border-radius: 15px; margin-top: 15px; text-align: left; }
.header-detaliu { display: flex; justify-content: space-between; margin-bottom: 12px; }
.badge-planta { background: white; color: var(--roz-inchis); padding: 4px 10px; border-radius: 12px; font-weight: bold; font-size: 0.85rem; }
.data-completa { color: #888; font-size: 0.85rem; }
.detaliu-card h3 { color: #333; margin: 0 0 10px 0; font-size: 1.2rem; }
.detaliu-card p { color: #444; line-height: 1.6; margin: 0; font-size: 1rem; }
</style>