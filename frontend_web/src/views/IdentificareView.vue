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
        
        <!-- Input ascuns pentru Galerie / Explorer -->
        <input 
          type="file" 
          ref="inputGalerie" 
          accept="image/png, image/jpeg, image/jpg" 
          class="input-ascuns" 
          @change="previzualizeazaImagine" 
        />

        <!-- Input ascuns pentru Cameră pe mobil -->
        <input 
          type="file" 
          ref="inputCamera" 
          accept="image/*" 
          capture="environment" 
          class="input-ascuns" 
          @change="previzualizeazaImagine" 
        />

        <!-- ZONĂ UPLOAD DESKTOP -->
        <div 
          v-if="!imagineSelectata && esteDesktop" 
          class="zona-upload zona-desktop"
          @click="deschideGalerie"
          @dragover.prevent
          @drop.prevent="trageFisier"
        >
          <div class="iconita-upload">💻</div>
          <p><strong>Apasă pentru a alege o poză din calculator</strong></p>
          <small>sau trage imaginea direct aici (Drag & Drop)</small>
        </div>

        <!-- ZONĂ UPLOAD MOBIL -->
        <div v-else-if="!imagineSelectata && !esteDesktop" class="zona-selectie-dubla">
          <div class="zona-upload" @click="deschideCamera">
            <div class="iconita-upload">📷</div>
            <p><strong>Fă o poză pe loc</strong></p>
            <small>Deschide camera foto</small>
          </div>

          <div class="zona-upload" @click="deschideGalerie">
            <div class="iconita-upload">🖼️</div>
            <p><strong>Alege din Galerie</strong></p>
            <small>Încarcă un fișier PNG sau JPG</small>
          </div>
        </div>

        <!-- PREVIZUALIZARE -->
        <div v-else class="previzualizare">
          <p>Imagine selectată:</p>
          <img :src="previewUrl" alt="Previzualizare" class="poza-mica" />
          <button @click="schimbaPoza" class="btn-schimba">✖ Schimbă Poza</button>
        </div>

        <button 
          @click="trimiteSpreAnaliza" 
          class="btn-mare btn-verde" 
          :disabled="!imagineSelectata || seProceseaza"
        >
          {{ seProceseaza ? 'Analizăm imaginea... ⏳' : 'Analizează Planta' }}
        </button>

        <!-- CARD REZULTAT -->
        <div v-if="plantaDetectata" class="card-rezultat">
          <div class="rezultat-header">
            <span class="icon-rezultat">🌿</span>
            <div>
              <h3>{{ plantaDetectata.nume_uzual }}</h3>
              <p class="stiintific"><em>{{ plantaDetectata.denumire_stiintifica }}</em></p>
            </div>
          </div>

          <p class="descriere-rezultat">{{ plantaDetectata.descriere }}</p>

          <button @click="salveazaInIerbar(plantaDetectata.id)" class="btn-salveaza">
            ❤️ Salvează în Ierbarul Meu
          </button>
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

const mergiInapoi = () => router.push('/dashboard')

const afiseazaNotificare = async (opts) => {
  if (notificareInjectat) {
    return await notificareInjectat(opts)
  } else {
    if (opts.esteConfirmare) {
      return confirm(opts.mesaj)
    } else {
      alert(`${opts.titlu}: ${opts.mesaj}`)
    }
  }
}

onMounted(() => {
  const agent = navigator.userAgent || navigator.vendor || window.opera
  esteDesktop.value = !/android|ipad|iphone|ipod/i.test(agent)
})

const deschideCamera = async () => {
  const permisiune = await afiseazaNotificare({
    titlu: "Permisiune Cameră 📸",
    mesaj: "Aplicația are nevoie de acces la camera foto pentru a fotografia planta.",
    tip: "success",
    esteConfirmare: true
  })

  if (permisiune && inputCamera.value) {
    inputCamera.value.click()
  }
}

const deschideGalerie = () => {
  if (inputGalerie.value) {
    inputGalerie.value.click()
  }
}

const trageFisier = (event) => {
  const fisier = event.dataTransfer.files[0]
  prelucreazaFisier(fisier)
}

const previzualizeazaImagine = (event) => {
  const fisier = event.target.files[0]
  prelucreazaFisier(fisier)
}

const prelucreazaFisier = (fisier) => {
  if (fisier) {
    if (!fisier.type.startsWith('image/')) {
      afiseazaNotificare({
        titlu: "Fișier Invalid",
        mesaj: "Te rugăm să alegi o imagine validă (.png, .jpg sau .jpeg).",
        tip: "error"
      })
      return
    }
    imagineSelectata.value = fisier
    previewUrl.value = URL.createObjectURL(fisier)
    plantaDetectata.value = null
  }
}

const schimbaPoza = () => {
  imagineSelectata.value = null
  previewUrl.value = null
  plantaDetectata.value = null
  if (inputGalerie.value) inputGalerie.value.value = ''
  if (inputCamera.value) inputCamera.value.value = ''
}

const trimiteSpreAnaliza = async () => {
  if (!imagineSelectata.value) return

  try {
    seProceseaza.value = true
    plantaDetectata.value = null

    const token = localStorage.getItem('jwt_token')
    const userId = localStorage.getItem('user_id') || 1

    const formData = new FormData()
    formData.append('file', imagineSelectata.value)
    formData.append('user_id', userId)

    const raspuns = await axios.post('http://localhost:8080/api/plante/scaneaza', formData, {
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'multipart/form-data'
      }
    })

    if (raspuns.data && raspuns.data.nume_uzual) {
      plantaDetectata.value = raspuns.data
      await afiseazaNotificare({
        titlu: "Identificare Reușită! 🎉",
        mesaj: `Specia identificată: ${raspuns.data.nume_uzual}!`,
        tip: "success"
      })
    }
  } catch (eroare) {
    console.error("Eroare la scanare:", eroare)
    
    let mesajEroare = "A apărut o problemă la procesarea imaginii. Verifică dacă serverul Python (YOLO) este pornit pe portul 5000."
    if (eroare.response && eroare.response.data) {
      mesajEroare = typeof eroare.response.data === 'object'
        ? JSON.stringify(eroare.response.data, null, 2)
        : eroare.response.data
    }

    await afiseazaNotificare({
      titlu: "Eroare la Scanare",
      mesaj: mesajEroare,
      tip: "error"
    })
  } finally {
    seProceseaza.value = false
  }
}

const salveazaInIerbar = async (plantaId) => {
  try {
    const token = localStorage.getItem('jwt_token')
    await axios.post(`http://localhost:8080/api/plante/ierbar-personal/${plantaId}`, {}, {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    await afiseazaNotificare({
      titlu: "Plantă Salvată!",
      mesaj: "Specia a fost adăugată în Ierbarul tău personal. 🌸",
      tip: "success"
    })
  } catch (eroare) {
    await afiseazaNotificare({
      titlu: "Eroare la Salvare",
      mesaj: "Nu am putut adăuga planta în colecție.",
      tip: "error"
    })
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
.btn-secundar:hover { background: #f0f0f0; }
.descriere { color: #666; margin-bottom: 20px; text-align: center; }

.input-ascuns { display: none; }

.zona-selectie-dubla { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin-bottom: 15px; }

.zona-upload {
  border: 2px dashed var(--verde-deschis);
  background: var(--crem-fundal);
  border-radius: 12px;
  padding: 30px 15px;
  text-align: center;
  cursor: pointer;
  transition: 0.3s;
}
.zona-upload:hover { background: #eef7d2; border-color: var(--verde-inchis); }
.zona-upload.zona-desktop { padding: 45px 20px; }
.zona-upload p { margin: 8px 0 3px 0; color: #333; }
.zona-upload small { color: #777; font-size: 0.85rem; }
.iconita-upload { font-size: 2.8rem; }

.previzualizare { margin-top: 15px; text-align: center; display: flex; flex-direction: column; align-items: center; gap: 10px; }
.poza-mica { max-width: 220px; max-height: 220px; object-fit: cover; border-radius: 12px; border: 3px solid var(--verde-inchis); }
.btn-schimba { background: #fee; color: #e74c3c; border: none; padding: 6px 15px; border-radius: 20px; font-weight: bold; cursor: pointer; }

.btn-mare { width: 100%; padding: 15px; border: none; border-radius: 10px; font-weight: bold; font-size: 1.1rem; color: white; margin-top: 20px; cursor: pointer; transition: 0.3s; }
.btn-verde { background-color: var(--verde-inchis); }
.btn-verde:hover:not(:disabled) { background-color: var(--verde-deschis); color: #333; }
.btn-verde:disabled { background-color: #ccc; cursor: not-allowed; }

/* Card Rezultat */
.card-rezultat { background: #f4faeb; border-left: 5px solid var(--verde-inchis); padding: 20px; border-radius: 12px; margin-top: 25px; text-align: left; animation: popIn 0.3s ease-out; }
@keyframes popIn { from { opacity: 0; transform: scale(0.95); } to { opacity: 1; transform: scale(1); } }
.rezultat-header { display: flex; align-items: center; gap: 15px; }
.icon-rezultat { font-size: 2.2rem; }
.rezultat-header h3 { margin: 0; color: var(--verde-inchis); }
.stiintific { color: #777; margin: 3px 0 0 0; }
.descriere-rezultat { color: #444; line-height: 1.5; margin: 15px 0; }
.btn-salveaza { background: #ffebeb; color: #e74c3c; border: 1px solid #ffb3b3; padding: 10px 20px; border-radius: 20px; font-weight: bold; cursor: pointer; transition: 0.3s; }
.btn-salveaza:hover { background: #e74c3c; color: white; }

@media (max-width: 500px) {
  .zona-selectie-dubla { grid-template-columns: 1fr; }
}
</style>