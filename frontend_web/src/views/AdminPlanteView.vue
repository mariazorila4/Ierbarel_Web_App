<template>
  <div class="page-wrapper">
    <div class="glass-card wide-card admin-mode">
      <div class="header header-spatiat">
        <div class="header-stanga">
          <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
          <h2 class="titlu">🌱 Baza de Date Plante</h2>
        </div>
        <!-- Butonul care deschide formularul gol pentru Adăugare -->
        <button @click="deschideModalAdaugare" class="btn-adaugare">➕ Adaugă Specie Nouă</button>
      </div>

      <div class="grila-admin-plante">
        <!-- Lista de plante existente -->
        <div v-for="planta in planteMock" :key="planta.id" class="card-planta-admin">
          <img :src="planta.imagineUrl" class="poza-mica" />
          <div class="info">
            <h3>{{ planta.nume }}</h3>
            <p>{{ planta.numeStiintific }}</p>
          </div>
          <div class="actiuni-planta">
            <!-- Butonul care deschide formularul completat pentru Editare -->
            <button @click="deschideModalEditare(planta)" class="btn-edit">✏️ Editează</button>
            <button @click="stergePlanta(planta.id)" class="btn-sterge-mic">🗑️</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ========================================= -->
    <!-- FEREASTRA MODALĂ PENTRU ADAUGARE / EDITARE -->
    <!-- ========================================= -->
    <div v-if="modalDeschis" class="modal-overlay" @click.self="inchideModal">
      <div class="modal-content formular-modal">
        <button class="btn-inchide" @click="inchideModal">✖</button>
        
        <!-- Titlul se schimbă dinamic în funcție de ce facem (Adăugare sau Editare) -->
        <h2 class="titlu-modal">{{ modEditare ? '✏️ Editează Specia' : '🌿 Adaugă Specie Nouă' }}</h2>
        <hr class="separator" />

        <form @submit.prevent="salveazaPlanta" class="formular-plante">
          
          <!-- Rândul 1: Nume -->
          <div class="rand-formular">
            <div class="grup-input">
              <label>Nume Uzual (Comun)</label>
              <input type="text" v-model="formPlanta.nume" placeholder="ex: Păpădie" required />
            </div>
            <div class="grup-input">
              <label>Nume Științific</label>
              <input type="text" v-model="formPlanta.numeStiintific" placeholder="ex: Taraxacum officinale" required />
            </div>
          </div>

          <!-- Rândul 2: Detalii Botanice -->
          <div class="rand-formular">
            <div class="grup-input">
              <label>Familie Botanică</label>
              <input type="text" v-model="formPlanta.familie" placeholder="ex: Asteraceae" required />
            </div>
            <div class="grup-input">
              <label>Perioadă Înflorire</label>
              <input type="text" v-model="formPlanta.perioadaInflorire" placeholder="ex: Aprilie - Septembrie" required />
            </div>
          </div>

          <!-- Rândul 3: Descriere -->
          <div class="grup-input complet">
            <label>Descriere Detaliată</label>
            <textarea v-model="formPlanta.descriere" rows="4" placeholder="Scrie aici informații despre plantă..." required></textarea>
          </div>

          <!-- Rândul 4: Imagine -->
          <div class="grup-input complet">
            <label>Imagine Plantă</label>
            <div class="zona-upload-mica">
              <input type="file" accept="image/*" @change="previzualizeazaImagine" class="input-file" />
              <div v-if="!formPlanta.imagineUrl" class="text-upload">📥 Apasă pentru a încărca o imagine</div>
              <img v-else :src="formPlanta.imagineUrl" class="previzualizare-img" />
            </div>
          </div>

          <!-- Butoane Formular -->
          <div class="actiuni-modal">
            <button type="button" @click="inchideModal" class="btn-secundar">Anulează</button>
            <button type="submit" class="btn-mare-verde">
              {{ modEditare ? 'Salvează Modificările' : 'Adaugă în Baza de Date' }}
            </button>
          </div>

        </form>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const mergiInapoi = () => router.push('/admin-dashboard')

// Baza de date simulată
const planteMock = ref([
  { 
    id: 1, nume: 'Mușețel', numeStiintific: 'Matricaria chamomilla', 
    familie: 'Asteraceae', perioadaInflorire: 'Mai - August', 
    descriere: 'O plantă recunoscută mondial pentru proprietățile sale calmante.',
    imagineUrl: 'https://images.unsplash.com/photo-1621571439975-d162a74c7d0d?w=200' 
  },
  { 
    id: 2, nume: 'Păpădie', numeStiintific: 'Taraxacum officinale', 
    familie: 'Asteraceae', perioadaInflorire: 'Aprilie - Septembrie', 
    descriere: 'Buruiană nutritivă cu flori galbene care se transformă în puf.',
    imagineUrl: 'https://images.unsplash.com/photo-1550949826-38d77d121c2c?w=200' 
  }
])

// --- LOGICA PENTRU FEREASTRA MODALĂ ---
const modalDeschis = ref(false)
const modEditare = ref(false)

// Obiectul care ține datele din formular la un moment dat
const formPlanta = ref({
  id: null,
  nume: '',
  numeStiintific: '',
  familie: '',
  perioadaInflorire: '',
  descriere: '',
  imagineUrl: null,
  fisierFizic: null // Aici vom ține fișierul real pentru a-l trimite spre backend mai târziu
})

// Când apăsăm pe "Adaugă"
const deschideModalAdaugare = () => {
  modEditare.value = false
  // Curățăm formularul
  formPlanta.value = { id: null, nume: '', numeStiintific: '', familie: '', perioadaInflorire: '', descriere: '', imagineUrl: null, fisierFizic: null }
  modalDeschis.value = true
  document.body.style.overflow = 'hidden' // Oprește scroll-ul paginii
}

// Când apăsăm pe "Editează"
const deschideModalEditare = (planta) => {
  modEditare.value = true
  // Copiem datele plantei în formular (folosim spread operator ... ca să nu modificăm direct baza de date până nu dăm Save)
  formPlanta.value = { ...planta, fisierFizic: null }
  modalDeschis.value = true
  document.body.style.overflow = 'hidden'
}

const inchideModal = () => {
  modalDeschis.value = false
  document.body.style.overflow = 'auto'
}

// Pentru încărcarea imaginii
const previzualizeazaImagine = (event) => {
  const fisier = event.target.files[0]
  if (fisier) {
    formPlanta.value.fisierFizic = fisier // Păstrăm fișierul pentru Axios (mai târziu)
    formPlanta.value.imagineUrl = URL.createObjectURL(fisier) // Creăm un URL local pentru previzualizare
  }
}

// Când apăsăm pe "Salvează" în formular
const salveazaPlanta = () => {
  if (modEditare.value) {
    // Aici va fi cererea PUT către Spring Boot
    console.log('Trimit spre Backend UPDATE pentru planta ID:', formPlanta.value.id, formPlanta.value)
    
    // Simulare vizuală update:
    const index = planteMock.value.findIndex(p => p.id === formPlanta.value.id)
    if (index !== -1) planteMock.value[index] = { ...formPlanta.value }
    
  } else {
    // Aici va fi cererea POST către Spring Boot
    console.log('Trimit spre Backend CREATE pentru planta nouă:', formPlanta.value)
    
    // Simulare vizuală adăugare:
    formPlanta.value.id = Date.now() // Generăm un ID temporar fals
    planteMock.value.push({ ...formPlanta.value })
  }
  
  inchideModal()
}

// Opțional, un buton de ștergere
const stergePlanta = (id) => {
  if(confirm('Ești sigur că vrei să ștergi această plantă?')) {
    // Aici va fi DELETE către Spring Boot
    planteMock.value = planteMock.value.filter(p => p.id !== id)
  }
}
</script>

<style scoped>
/* --- STILURI EXISTENTE PAGINĂ --- */
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.wide-card { max-width: 900px; }
.admin-mode { border: 3px solid var(--verde-inchis); }
.header-spatiat { display: flex; justify-content: space-between; align-items: center; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 20px; }
.header-stanga { display: flex; align-items: center; gap: 20px; }
.titlu { color: var(--verde-inchis); margin: 0; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }
.btn-adaugare { background-color: var(--verde-inchis); color: white; border: none; padding: 10px 20px; border-radius: 8px; font-weight: bold; cursor: pointer; transition: 0.3s; }
.btn-adaugare:hover { background-color: var(--verde-deschis); color: #333; }

.grila-admin-plante { display: flex; flex-direction: column; gap: 15px; }
.card-planta-admin { display: flex; align-items: center; background: white; padding: 15px; border-radius: 12px; border: 1px solid #eee; gap: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.02); }
.poza-mica { width: 60px; height: 60px; border-radius: 8px; object-fit: cover; border: 2px solid var(--verde-deschis); }
.info { flex: 1; }
.info h3 { margin: 0 0 5px 0; color: #333; }
.info p { margin: 0; color: #888; font-style: italic; font-size: 0.9rem; }
.actiuni-planta { display: flex; gap: 10px; }
.btn-edit { background: var(--albastru-pastel); border: none; padding: 8px 15px; border-radius: 6px; cursor: pointer; font-weight: bold; color: #333; transition: 0.3s; }
.btn-edit:hover { background: #8daeeb; }
.btn-sterge-mic { background: #f8d7da; border: none; padding: 8px 12px; border-radius: 6px; cursor: pointer; transition: 0.3s; }
.btn-sterge-mic:hover { background: #f5c6cb; }

/* --- STILURI MODAL & FORMULAR NOI --- */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 1000; padding: 20px; box-sizing: border-box; }
.modal-content { background: var(--crem-fundal); width: 100%; max-width: 650px; border-radius: 20px; position: relative; padding: 30px; box-shadow: 0 20px 50px rgba(0,0,0,0.3); animation: popUp 0.3s ease-out forwards; max-height: 90vh; overflow-y: auto; }
@keyframes popUp { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }
.btn-inchide { position: absolute; top: 15px; right: 15px; background: white; border: none; border-radius: 50%; width: 35px; height: 35px; font-size: 1.2rem; color: #555; cursor: pointer; box-shadow: 0 2px 5px rgba(0,0,0,0.2); transition: 0.3s; }
.btn-inchide:hover { background: #fee; color: #e74c3c; transform: scale(1.1); }
.titlu-modal { margin: 0 0 10px 0; color: var(--verde-inchis); }
.separator { border: none; border-top: 1px solid #ddd; margin-bottom: 20px; }

/* Grila formularului */
.formular-plante { display: flex; flex-direction: column; gap: 15px; }
.rand-formular { display: flex; gap: 15px; }
.grup-input { flex: 1; display: flex; flex-direction: column; }
.grup-input.complet { width: 100%; }
.grup-input label { font-weight: bold; font-size: 0.9rem; color: #444; margin-bottom: 5px; }
.grup-input input, .grup-input textarea { padding: 10px; border: 1px solid #ccc; border-radius: 8px; font-size: 1rem; font-family: inherit; transition: 0.3s; }
.grup-input input:focus, .grup-input textarea:focus { outline: none; border-color: var(--verde-inchis); box-shadow: 0 0 5px rgba(143, 175, 15, 0.3); }

/* Upload Imagine */
.zona-upload-mica { position: relative; background: white; border: 2px dashed var(--verde-deschis); border-radius: 8px; height: 120px; display: flex; justify-content: center; align-items: center; overflow: hidden; cursor: pointer; transition: 0.3s; }
.zona-upload-mica:hover { background: #f9fdf2; }
.input-file { position: absolute; width: 100%; height: 100%; opacity: 0; cursor: pointer; z-index: 2; }
.text-upload { color: #888; font-size: 0.95rem; font-weight: bold; }
.previzualizare-img { width: 100%; height: 100%; object-fit: contain; background: #fff; z-index: 1; }

/* Butoane Jos */
.actiuni-modal { display: flex; justify-content: flex-end; gap: 15px; margin-top: 20px; }
.btn-mare-verde { background-color: var(--verde-inchis); color: white; border: none; padding: 10px 25px; border-radius: 8px; font-weight: bold; font-size: 1.05rem; cursor: pointer; transition: 0.3s; }
.btn-mare-verde:hover { background-color: var(--verde-deschis); color: #333; }

/* Responsive pe telefon */
@media (max-width: 600px) {
  .rand-formular { flex-direction: column; gap: 15px; }
}
</style>