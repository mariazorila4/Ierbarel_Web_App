<template>
  <div class="page-wrapper">
    <div class="glass-card wide-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <h2 class="titlu">📸 Identifică o Plantă</h2>
      </div>

      <div class="continut">
        <p class="descriere">
          {{ esteDesktop ? 'Selectează sau trage o poză cu floarea din calculator, iar asistentul nostru o va analiza.' : 'Fotografiază floarea pe loc sau încarcă o imagine din galerie, iar asistentul nostru o va analiza.' }}
        </p>
        
        <input type="file" ref="inputGalerie" accept="image/png, image/jpeg, image/jpg" class="input-ascuns" @change="previzualizeazaImagine" />
        <input type="file" ref="inputCamera" accept="image/*" capture="environment" class="input-ascuns" @change="previzualizeazaImagine" />

        <!-- UPLOAD DESKTOP / MOBIL -->
        <div v-if="!imagineSelectata && esteDesktop" class="zona-upload zona-desktop" @click="deschideGalerie" @dragover.prevent @drop.prevent="trageFisier">
          <div class="iconita-upload">💻</div>
          <p><strong>Apasă pentru a alege o poză din calculator</strong></p>
          <small>sau trage imaginea direct aici (Drag & Drop)</small>
        </div>

        <div v-else-if="!imagineSelectata && !esteDesktop" class="zona-selectie-dubla">
          <div class="zona-upload" @click="deschideCamera">
            <div class="iconita-upload">📷</div>
            <p><strong>Fă o poză pe loc</strong></p>
          </div>
          <div class="zona-upload" @click="deschideGalerie">
            <div class="iconita-upload">🖼️</div>
            <p><strong>Alege din Galerie</strong></p>
          </div>
        </div>

        <div v-else class="previzualizare">
          <p>Imagine selectată:</p>
          <img :src="previewUrl" alt="Previzualizare" class="poza-mica" />
          <button @click="schimbaPoza" class="btn-schimba">✖ Schimbă Poza</button>
        </div>

        <button @click="trimiteSpreAnaliza" class="btn-mare btn-verde" :disabled="!imagineSelectata || seProceseaza">
          {{ seProceseaza ? 'Analizăm imaginea... ⏳' : 'Analizează Planta' }}
        </button>

        <!-- CARD REZULTAT -->
        <div v-if="plantaDetectata" class="card-rezultat">
          <div class="rezultat-header">
            <span class="icon-rezultat">🌿</span>
            <div>
              <h3>{{ plantaDetectata.nume_uzual || plantaDetectata.numeUzual }}</h3>
              <p class="stiintific"><em>{{ plantaDetectata.denumire_stiintifica || plantaDetectata.denumireStiintifica }}</em></p>
              <span v-if="plantaDetectata.familie" class="badge-familie">🌿 {{ plantaDetectata.familie }}</span>
            </div>
          </div>

          <p class="descriere-rezultat">{{ plantaDetectata.descriere || 'Specie identificată automat prin scanare foto.' }}</p>

          <!-- SECTIUNEA DE SALVARE ÎN IERBAR (CU LOCAȚIE PE HARTĂ) -->
          <div v-if="!esteSalvataInIerbar" class="grup-salvare">
            <label for="input-locatie" class="label-locatie">📍 Locația unde ai găsit planta (opțional):</label>
            
            <div class="rand-locatie-harta">
              <input 
                id="input-locatie"
                v-model="locatieGasita" 
                type="text" 
                placeholder="ex: Parcul Herăstrău, București" 
                class="input-text"
              />
              <button type="button" @click="arataHartaModal = true" class="btn-deschide-harta" title="Alege pe hartă stil Bolt">
                🗺️ Harta
              </button>
            </div>
            
            <button @click="salveazaInIerbar" class="btn-salveaza" :disabled="seSalveaza">
              {{ seSalveaza ? 'Se salvează... ⏳' : '❤️ Salvează în Ierbarul Personal' }}
            </button>
          </div>

          <!-- MESAJ SUCCES DUPĂ SALVARE -->
          <div v-else class="sectiune-publicare">
            <p class="text-succes-salvare">✅ Planta a fost salvată în Ierbarul tău Personal!</p>
            <p class="text-info-secundar">Dacă dorești să o faci publică, mergi în Ierbarul Personal și apasă "Publică în Galerie".</p>
          </div>
        </div>
      </div>
    </div>

    <!-- MODAL HARTĂ POP-UP STIL BOLT -->
    <div v-if="arataHartaModal" class="modal-overlay z-top" @click.self="arataHartaModal = false">
      <div class="modal-content-locatie">
        <button class="btn-inchide" @click="arataHartaModal = false">✖</button>
        <SelectorLocatie @locatie-selectata="preiaLocatieDinHarta" />
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import SelectorLocatie from '../components/SelectorLocatie.vue'

const notificareInjectat = inject('notificare', null)
const router = useRouter()

const inputGalerie = ref(null)
const inputCamera = ref(null)

const esteDesktop = ref(true)
const imagineSelectata = ref(null)
const previewUrl = ref(null)
const seProceseaza = ref(false)
const plantaDetectata = ref(null)

const esteSalvataInIerbar = ref(false)
const locatieGasita = ref('')
const seSalveaza = ref(false)
const arataHartaModal = ref(false)

const mergiInapoi = () => router.push('/dashboard')

const afiseazaNotificare = async (opts) => {
  if (notificareInjectat) {
    return await notificareInjectat(opts)
  } else {
    alert(`${opts.titlu}: ${opts.mesaj}`)
  }
}

onMounted(() => {
  const agent = navigator.userAgent || navigator.vendor || window.opera
  esteDesktop.value = !/android|ipad|iphone|ipod/i.test(agent)
})

const preiaLocatieDinHarta = (dateLocatie) => {
  locatieGasita.value = dateLocatie.adresa
  arataHartaModal.value = false
}

const deschideCamera = () => inputCamera.value?.click()
const deschideGalerie = () => inputGalerie.value?.click()

const trageFisier = (event) => {
  const fisier = event.dataTransfer.files[0]
  if (fisier) prelucreazaFisier(fisier)
}

const previzualizeazaImagine = (event) => {
  const fisier = event.target.files[0]
  if (fisier) prelucreazaFisier(fisier)
}

const prelucreazaFisier = (fisier) => {
  if (fisier && fisier.type.startsWith('image/')) {
    imagineSelectata.value = fisier
    previewUrl.value = URL.createObjectURL(fisier)
    plantaDetectata.value = null
    esteSalvataInIerbar.value = false
    locatieGasita.value = ''
  }
}

const schimbaPoza = () => {
  imagineSelectata.value = null
  previewUrl.value = null
  plantaDetectata.value = null
  esteSalvataInIerbar.value = false
  locatieGasita.value = ''
}

const trimiteSpreAnaliza = async () => {
  if (!imagineSelectata.value) return

  try {
    seProceseaza.value = true
    plantaDetectata.value = null
    esteSalvataInIerbar.value = false

    const token = localStorage.getItem('jwt_token')
    const formData = new FormData()
    formData.append('file', imagineSelectata.value)

    const raspuns = await axios.post('http://localhost:8080/api/plante/scaneaza', formData, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    if (raspuns.data) {
      plantaDetectata.value = raspuns.data
      await afiseazaNotificare({
        titlu: "Identificare Reușită! 🎉",
        mesaj: `Specia identificată: ${raspuns.data.nume_uzual || raspuns.data.numeUzual}!`,
        tip: "success"
      })
    }
  } catch (eroare) {
    await afiseazaNotificare({ titlu: "Eroare la Scanare", mesaj: "Nu s-a putut procesa imaginea.", tip: "error" })
  } finally {
    seProceseaza.value = false
  }
}

const salveazaInIerbar = async () => {
  if (!plantaDetectata.value) return

  try {
    seSalveaza.value = true
    const token = localStorage.getItem('jwt_token')
    const imgBase64 = plantaDetectata.value.imagine_url || plantaDetectata.value.imagineUrl || previewUrl.value

    const payload = {
      nume_uzual: plantaDetectata.value.nume_uzual || plantaDetectata.value.numeUzual || 'Plantă Scanată',
      denumire_stiintifica: plantaDetectata.value.denumire_stiintifica || plantaDetectata.value.denumireStiintifica || 'Specie Botanică',
      familie: plantaDetectata.value.familie || 'Familie Botanică',
      descriere: plantaDetectata.value.descriere || 'Identificată prin scanare foto.',
      imagine_url: imgBase64,
      locatie: locatieGasita.value || 'Nespecificată',
      categorie_planta: plantaDetectata.value.categorie_planta || plantaDetectata.value.categorie || 'FLOARE',
      tip_planta: plantaDetectata.value.tip_planta || plantaDetectata.value.tipPlanta || 'ORNAMENTALA',
      inaltime_maxima: plantaDetectata.value.inaltime_maxima || plantaDetectata.value.inaltimeMaxima || 0.5,
      perioada_inflorire: plantaDetectata.value.perioada_inflorire || plantaDetectata.value.perioadaInflorire || 'Primăvară - Vară',
      ciclu_de_viata: plantaDetectata.value.ciclu_de_viata || plantaDetectata.value.cicluDeViata || 'PEREN',
      numar_petale: plantaDetectata.value.numar_petale || plantaDetectata.value.numarPetale || 5,
      culoare: plantaDetectata.value.culoare || 'Diverse',
      tip_coroana: plantaDetectata.value.tip_coroana || plantaDetectata.value.tipCoroana || 'Nespecificată',
      tip_frunza: plantaDetectata.value.tip_frunza || plantaDetectata.value.tipFrunza || 'Simplă',
      tip_tulpina: plantaDetectata.value.tip_tulpina || plantaDetectata.value.tipTulpina || 'Erectă',
      pom_fructifer: plantaDetectata.value.pom_fructifer || plantaDetectata.value.pomFructifer || false,
      produce_fructe: plantaDetectata.value.produce_fructe || plantaDetectata.value.produceFructe || false,
      poate_fi_uscata: plantaDetectata.value.poate_fi_uscata || plantaDetectata.value.poateFiUscata || false
    }

    await axios.post('http://localhost:8080/api/plante/salveaza-scanare', payload, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    esteSalvataInIerbar.value = true

    await afiseazaNotificare({
      titlu: "Plantă Salvată!",
      mesaj: "Specia a fost adăugată în Ierbarul tău Personal. 🌸",
      tip: "success"
    })
  } catch (eroare) {
    console.error("Eroare la salvare:", eroare)
    await afiseazaNotificare({
      titlu: "Eroare la Salvare",
      mesaj: "Nu am putut adăuga planta în colecție.",
      tip: "error"
    })
  } finally {
    seSalveaza.value = false
  }
}
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--verde-deschis); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.wide-card { max-width: 600px; }
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 20px; }
.titlu { color: var(--verde-inchis); margin: 0; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.descriere { color: #666; margin-bottom: 20px; text-align: center; }

.input-ascuns { display: none; }
.zona-selectie-dubla { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 15px; }

.zona-upload { border: 2px dashed var(--verde-deschis); background: var(--crem-fundal); border-radius: 12px; padding: 30px 15px; text-align: center; cursor: pointer; transition: 0.3s; }
.zona-upload.zona-desktop { padding: 45px 20px; }
.iconita-upload { font-size: 2.8rem; }

.previzualizare { margin-top: 15px; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 10px; }
.poza-mica { max-width: 220px; max-height: 220px; object-fit: cover; border-radius: 12px; border: 3px solid var(--verde-inchis); }
.btn-schimba { background: #fee; color: #e74c3c; border: none; padding: 6px 15px; border-radius: 20px; font-weight: bold; cursor: pointer; }

.btn-mare { width: 100%; padding: 15px; border: none; border-radius: 10px; font-weight: bold; font-size: 1.1rem; color: white; margin-top: 20px; cursor: pointer; transition: 0.3s; }
.btn-verde { background-color: var(--verde-inchis); }
.btn-verde:disabled { background-color: #ccc; cursor: not-allowed; }

/* Card Rezultat */
.card-rezultat { background: #f4faeb; border-left: 5px solid var(--verde-inchis); padding: 20px; border-radius: 12px; margin-top: 25px; text-align: left; }
.rezultat-header { display: flex; align-items: center; gap: 15px; }
.icon-rezultat { font-size: 2.2rem; }
.stiintific { color: #777; margin: 3px 0 0 0; }
.badge-familie { display: inline-block; font-size: 0.8rem; background: #e0f2fe; color: #0369a1; padding: 2px 8px; border-radius: 10px; margin-top: 4px; font-weight: bold; }
.descriere-rezultat { color: #444; line-height: 1.5; margin: 15px 0; font-size: 0.95rem; }

/* Grup Salvare + Harta */
.grup-salvare { display: flex; flex-direction: column; gap: 12px; margin-top: 20px; border-top: 1px dashed #ccc; padding-top: 15px; }
.label-locatie { font-size: 0.9rem; color: #555; font-weight: 500; }
.rand-locatie-harta { display: flex; gap: 10px; }
.input-text { flex: 1; padding: 10px; border: 1px solid #ccc; border-radius: 8px; font-size: 0.95rem; box-sizing: border-box; }
.btn-deschide-harta { background: white; border: 2px solid var(--verde-inchis); color: var(--verde-inchis); border-radius: 8px; padding: 0 15px; font-weight: bold; cursor: pointer; transition: 0.2s; white-space: nowrap; }
.btn-deschide-harta:hover { background: var(--verde-inchis); color: white; }

.btn-salveaza { background: #ffebeb; color: #e74c3c; border: 1px solid #ffb3b3; padding: 12px 20px; border-radius: 20px; font-weight: bold; cursor: pointer; transition: 0.3s; width: 100%; }
.btn-salveaza:hover:not(:disabled) { background: #e74c3c; color: white; }

.sectiune-publicare { margin-top: 20px; border-top: 1px dashed #ccc; padding-top: 15px; text-align: center; }
.text-succes-salvare { color: #27ae60; font-weight: bold; margin-bottom: 5px; font-size: 1.05rem; }
.text-info-secundar { color: #666; font-size: 0.85rem; }

/* Modal Harta */
.z-top { z-index: 10000 !important; }
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.7); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; padding: 20px; box-sizing: border-box; }
.modal-content-locatie { background: white; width: 100%; max-width: 550px; border-radius: 20px; padding: 20px; position: relative; box-shadow: 0 20px 50px rgba(0,0,0,0.3); }
.btn-inchide { position: absolute; top: 15px; right: 15px; background: rgba(0,0,0,0.1); border: none; border-radius: 50%; width: 32px; height: 32px; font-size: 1.1rem; cursor: pointer; z-index: 20; }
</style>