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

          <!-- BUTON 1: SALVARE DOAR ÎN IERBAR PERSONAL -->
          <button v-if="!esteSalvataInIerbar" @click="salveazaInIerbar" class="btn-salveaza" :disabled="seSalveaza">
            {{ seSalveaza ? 'Se salvează... ⏳' : '❤️ Salvează în Ierbarul Personal' }}
          </button>

          <!-- SECTIUNE DUPĂ SALVAREA PERSONALĂ -->
          <div v-else class="sectiune-publicare">
            <p class="text-succes-salvare">✅ Adăugată în Ierbarul tău Personal!</p>
            
            <!-- BUTON 2: PUBLICARE SEPARATĂ ÎN GALERIE GLOBALĂ -->
            <div v-if="!estePublicataGlobal" class="grup-publicare">
              <label for="input-locatie">📍 Locația unde ai găsit planta (opțional):</label>
              <input 
                id="input-locatie"
                v-model="locatieGasita" 
                type="text" 
                placeholder="ex: Parcul Herăstrău, București" 
                class="input-text"
              />
              <button @click="publicaInIerbarGlobal" class="btn-publica" :disabled="sePublica">
                {{ sePublica ? 'Se publică... ⏳' : '🌐 Publică în Galerie Globală' }}
              </button>
            </div>

            <p v-else class="text-succes-publicare">🎉 Fotografia ta a fost adăugată în galeria comunității!</p>
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
const estePublicataGlobal = ref(false)
const locatieGasita = ref('')
const seSalveaza = ref(false)
const sePublica = ref(false)

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

const deschideCamera = () => inputCamera.value?.click()
const deschideGalerie = () => inputGalerie.value?.click()

const previzualizeazaImagine = (event) => {
  const fisier = event.target.files[0]
  if (fisier && fisier.type.startsWith('image/')) {
    imagineSelectata.value = fisier
    previewUrl.value = URL.createObjectURL(fisier)
    plantaDetectata.value = null
    esteSalvataInIerbar.value = false
    estePublicataGlobal.value = false
    locatieGasita.value = ''
  }
}

const schimbaPoza = () => {
  imagineSelectata.value = null
  previewUrl.value = null
  plantaDetectata.value = null
  esteSalvataInIerbar.value = false
  estePublicataGlobal.value = false
  locatieGasita.value = ''
}

const trimiteSpreAnaliza = async () => {
  if (!imagineSelectata.value) return

  try {
    seProceseaza.value = true
    plantaDetectata.value = null
    esteSalvataInIerbar.value = false
    estePublicataGlobal.value = false

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

// METODA 1: SALVEAZĂ EXCLUSIV ÎN IERBARUL PERSONAL (FĂRĂ PUBLICARE GLOBALĂ)
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
      locatie: 'Nespecificată'
    }

    await axios.post('http://localhost:8080/api/plante/salveaza-scanare', payload, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    esteSalvataInIerbar.value = true

    await afiseazaNotificare({
      titlu: "Plantă Salvată!",
      mesaj: "Specia a fost adăugată în Ierbarul tău personal. 🌸",
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

// METODA 2: METODĂ SEPARATĂ DE PUBLICARE GLOBALĂ
const publicaInIerbarGlobal = async () => {
  if (!plantaDetectata.value) return

  try {
    sePublica.value = true
    const token = localStorage.getItem('jwt_token')
    const imgBase64 = plantaDetectata.value.imagine_url || plantaDetectata.value.imagineUrl || previewUrl.value
    const plantaId = plantaDetectata.value.id || 0

    await axios.post(
      `http://localhost:8080/api/plante/${plantaId}/publica-galerie`,
      {
        nume_uzual: plantaDetectata.value.nume_uzual || plantaDetectata.value.numeUzual,
        denumire_stiintifica: plantaDetectata.value.denumire_stiintifica || plantaDetectata.value.denumireStiintifica,
        familie: plantaDetectata.value.familie,
        descriere: plantaDetectata.value.descriere,
        imagine_url: imgBase64,
        locatie: locatieGasita.value || 'Nespecificată'
      },
      { headers: { 'Authorization': `Bearer ${token}` } }
    )

    estePublicataGlobal.value = true

    await afiseazaNotificare({
      titlu: "Publicat în Galerie! 🌐",
      mesaj: "Fotografia ta a fost distribuită comunității în Ierbarul Global!",
      tip: "success"
    })
  } catch (eroare) {
    console.error("Eroare la publicare:", eroare)
    await afiseazaNotificare({
      titlu: "Eroare la Publicare",
      mesaj: "Nu am putut publica fotografia.",
      tip: "error"
    })
  } finally {
    sePublica.value = false
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

.btn-salveaza { background: #ffebeb; color: #e74c3c; border: 1px solid #ffb3b3; padding: 12px 20px; border-radius: 20px; font-weight: bold; cursor: pointer; transition: 0.3s; width: 100%; }
.btn-salveaza:hover:not(:disabled) { background: #e74c3c; color: white; }

/* Publicare */
.sectiune-publicare { margin-top: 15px; border-top: 1px dashed #ccc; padding-top: 15px; }
.text-succes-salvare { color: #27ae60; font-weight: bold; margin-bottom: 15px; }
.grup-publicare { display: flex; flex-direction: column; gap: 10px; }
.grup-publicare label { font-size: 0.9rem; color: #555; font-weight: 500; }
.input-text { padding: 10px; border: 1px solid #ccc; border-radius: 8px; font-size: 0.95rem; width: 100%; box-sizing: border-box; }
.btn-publica { background-color: #2980b9; color: white; border: none; padding: 10px 15px; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.3s; width: 100%; }
.btn-publica:hover:not(:disabled) { background-color: #3498db; }
.text-succes-publicare { color: #2980b9; font-weight: bold; }
</style>