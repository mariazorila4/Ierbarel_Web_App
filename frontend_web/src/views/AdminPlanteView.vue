<template>
  <div class="page-wrapper">
    <div class="glass-card wide-card admin-mode">
      <div class="header header-spatiat">
        <div class="header-stanga">
          <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
          <h2 class="titlu">🌱 Baza de Date Plante</h2>
        </div>
        <button @click="deschideModalAdaugare" class="btn-adaugare">➕ Adaugă Specie Nouă</button>
      </div>

      <div class="grila-admin-plante">
        <div v-for="planta in planteAdmin" :key="planta.id" class="card-planta-admin">
          <img :src="planta.imagineUrl || planta.imagine_url || 'https://images.unsplash.com/photo-1628808168235-96bece30fc6e?w=200'" class="poza-mica" />
          <div class="info">
            <h3>{{ planta.nume_uzual }}</h3>
            <p>{{ planta.denumire_stiintifica }}</p>
          </div>
          <div class="actiuni-planta">
            <button @click="deschideModalEditare(planta)" class="btn-edit">✏️ Editează</button>
            <button @click="stergePlanta(planta.id)" class="btn-sterge-mic">🗑️</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="modalDeschis" class="modal-overlay" @click.self="inchideModal">
      <div class="modal-content formular-modal">
        <button class="btn-inchide" @click="inchideModal">✖</button>
        
        <h2 class="titlu-modal">{{ modEditare ? '✏️ Editează Specia' : '🌿 Adaugă Specie Nouă' }}</h2>
        <hr class="separator" />

        <form @submit.prevent="salveazaPlanta" class="formular-plante">
          
          <div class="grup-input complet">
            <label>Categoria Plantei</label>
            <select v-model="formPlanta.categoriePlanta" required class="input-select">
              <option disabled value="">Selectează categoria...</option>
              <option value="FLOARE">Floare</option>
              <option value="ARBORE">Arbore</option>
              <option value="ARBUST">Arbust</option>
              <option value="IERBURI">Ierburi</option>
            </select>
          </div>

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

          <div class="rand-formular">
            <div class="grup-input">
              <label>Tip Plantă</label>
              <select v-model="formPlanta.tipPlanta" required class="input-select">
                <option value="ORNAMENTALA">Ornamentală</option>
                <option value="MEDICINALA">Medicinală</option>
                <option value="COMESTIBILA">Comestibilă</option>
                <option value="AROMATICA">Aromatică</option>
                <option value="SALBATICA">Sălbatică</option>
              </select>
            </div>
            <div class="grup-input">
              <label>Ciclu de viață</label>
              <select v-model="formPlanta.cicluDeViata" required class="input-select">
                <option value="ANUAL">Anual</option>
                <option value="BIENAL">Bienal</option>
                <option value="PEREN">Peren</option>
              </select>
            </div>
          </div>

          <div class="rand-formular">
            <div class="grup-input">
              <label>Înălțime maximă (metri)</label>
              <input type="number" step="0.1" v-model="formPlanta.inaltimeMaxima" placeholder="ex: 1.5" required />
            </div>
            <div class="grup-input checkbox-container">
              <label class="checkbox-label">
                <input type="checkbox" v-model="formPlanta.poateFiUscata" />
                Poate fi uscată pentru ierbar
              </label>
            </div>
          </div>

          <div v-if="formPlanta.categoriePlanta === 'FLOARE'" class="zona-specifica">
            <h4 class="titlu-specific">🌸 Detalii Floare</h4>
            <div class="rand-formular">
              <div class="grup-input">
                <label>Număr Petale</label>
                <input type="number" v-model="formPlanta.numarPetale" placeholder="ex: 5" required />
              </div>
              <div class="grup-input">
                <label>Culoare</label>
                <input type="text" v-model="formPlanta.culoare" placeholder="ex: Galben" required />
              </div>
            </div>
          </div>

          <div v-if="formPlanta.categoriePlanta === 'ARBORE'" class="zona-specifica">
            <h4 class="titlu-specific">🌳 Detalii Arbore</h4>
            <div class="rand-formular">
              <div class="grup-input">
                <label>Tip Coroană</label>
                <input type="text" v-model="formPlanta.tipCoroana" placeholder="ex: Globulară, Piramidală" required />
              </div>
              <div class="grup-input">
                <label>Tip Frunză</label>
                <input type="text" v-model="formPlanta.tipFrunza" placeholder="ex: Simplă, Compusă" required />
              </div>
            </div>
            <div class="grup-input checkbox-container" style="margin-top: 10px;">
              <label class="checkbox-label">
                <input type="checkbox" v-model="formPlanta.pomFructifer" />
                Este pom fructifer
              </label>
            </div>
          </div>

          <div v-if="formPlanta.categoriePlanta === 'ARBUST'" class="zona-specifica">
            <h4 class="titlu-specific">🌿 Detalii Arbust</h4>
            <div class="grup-input checkbox-container">
              <label class="checkbox-label">
                <input type="checkbox" v-model="formPlanta.produceFructe" />
                Produce fructe comestibile / decorative
              </label>
            </div>
          </div>

          <div v-if="formPlanta.categoriePlanta === 'IERBURI'" class="zona-specifica">
            <h4 class="titlu-specific">🌾 Detalii Ierburi</h4>
            <div class="grup-input">
              <label>Tip Tulpina</label>
              <input type="text" v-model="formPlanta.tipTulpina" placeholder="ex: Erectă, Târâtoare" required />
            </div>
          </div>

          <div class="grup-input complet">
            <label>Descriere Detaliată</label>
            <textarea v-model="formPlanta.descriere" rows="4" placeholder="Scrie aici informații despre plantă..." required></textarea>
          </div>

          <div class="grup-input complet">
            <label>📍 Locație (Unde se găsește?)</label>
            <input 
              type="text" 
              v-model="formPlanta.locatie" 
              placeholder="ex: Munții Carpați, Europa de Est, Parcul Herăstrău..." 
              required 
            />
          </div>

          <div class="grup-input complet">
            <label>URL Imagine Plantă</label>
            <input 
              type="url" 
              v-model="formPlanta.imagineUrl" 
              placeholder="Introdu link-ul imaginii (ex: https://.../poza.jpg)" 
            />
            <div v-if="formPlanta.imagineUrl" class="zona-previzualizare-url">
              <img :src="formPlanta.imagineUrl" class="previzualizare-img-url" alt="Previzualizare" />
            </div>
          </div>

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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const mergiInapoi = () => router.push('/admin-dashboard')

const planteAdmin = ref([])
const modalDeschis = ref(false)
const modEditare = ref(false)

const formPlanta = ref({
  id: null,
  categoriePlanta: '',
  nume: '',
  numeStiintific: '',
  familie: '',
  perioadaInflorire: '',
  descriere: '',
  locatie: '',
  imagineUrl: '',
  inaltimeMaxima: '',
  poateFiUscata: false,
  cicluDeViata: 'PEREN',
  tipPlanta: 'ORNAMENTALA',
  numarPetale: '',
  culoare: '',
  tipCoroana: '',
  tipFrunza: '',
  pomFructifer: false,
  produceFructe: false,
  tipTulpina: ''
})

const incarcaPlante = async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const raspuns = await axios.get('http://localhost:8080/api/plante', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    planteAdmin.value = raspuns.data
  } catch (eroare) {
    console.error("Eroare la preluarea plantelor:", eroare)
  }
}

onMounted(() => {
  incarcaPlante()
})

const deschideModalAdaugare = () => {
  modEditare.value = false
  resetFormular()
  modalDeschis.value = true
  document.body.style.overflow = 'hidden'
}

const deschideModalEditare = (planta) => {
  modEditare.value = true
  formPlanta.value = {
    id: planta.id,
    categoriePlanta: planta.categorie_planta || '',
    nume: planta.nume_uzual || '',
    numeStiintific: planta.denumire_stiintifica || '',
    familie: planta.familie || '',
    perioadaInflorire: planta.perioada_inflorire || '',
    descriere: planta.descriere || '',
    locatie: planta.locatie || '',
    imagineUrl: planta.imagineUrl || planta.imagine_url || '',
    inaltimeMaxima: planta.inaltime_maxima || '',
    poateFiUscata: planta.poate_fi_uscata || false,
    cicluDeViata: planta.ciclu_de_viata || 'PEREN',
    tipPlanta: planta.tip_planta || 'ORNAMENTALA',
    numarPetale: planta.numar_petale || '',
    culoare: planta.culoare || '',
    tipCoroana: planta.tip_coroana || '',
    tipFrunza: planta.tip_frunza || '',
    pomFructifer: planta.pom_fructifer || false,
    produceFructe: planta.produce_fructe || false,
    tipTulpina: planta.tip_tulpina || ''
  }
  modalDeschis.value = true
  document.body.style.overflow = 'hidden'
}

const resetFormular = () => {
  formPlanta.value = {
    id: null, categoriePlanta: '', nume: '', numeStiintific: '', familie: '', perioadaInflorire: '',
    descriere: '', locatie: '', imagineUrl: '', inaltimeMaxima: '', poateFiUscata: false, cicluDeViata: 'PEREN',
    tipPlanta: 'ORNAMENTALA', numarPetale: '', culoare: '', tipCoroana: '', tipFrunza: '',
    pomFructifer: false, produceFructe: false, tipTulpina: ''
  }
}

const inchideModal = () => {
  modalDeschis.value = false
  document.body.style.overflow = 'auto'
}

const salveazaPlanta = async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const adminId = localStorage.getItem('user_id') 

    const datePlantaJava = {
      categorie_planta: formPlanta.value.categoriePlanta,
      nume_uzual: formPlanta.value.nume,
      denumire_stiintifica: formPlanta.value.numeStiintific,
      familie: formPlanta.value.familie,
      descriere: formPlanta.value.descriere,
      locatie: formPlanta.value.locatie,
      inaltime_maxima: formPlanta.value.inaltimeMaxima ? formPlanta.value.inaltimeMaxima.toString() : "0",
      perioada_inflorire: formPlanta.value.perioadaInflorire,
      poate_fi_uscata: formPlanta.value.poateFiUscata ? "true" : "false",
      ciclu_de_viata: formPlanta.value.cicluDeViata,
      tip_planta: formPlanta.value.tipPlanta,
      imagine_url: formPlanta.value.imagineUrl,
      
      numar_petale: formPlanta.value.numarPetale ? formPlanta.value.numarPetale.toString() : "0",
      culoare: formPlanta.value.culoare || "-",
      tip_coroana: formPlanta.value.tipCoroana || "-",
      tip_frunza: formPlanta.value.tipFrunza || "-",
      pom_fructifer: formPlanta.value.pomFructifer ? "true" : "false",
      produce_fructe: formPlanta.value.produceFructe ? "true" : "false",
      tip_tulpina: formPlanta.value.tipTulpina || "-"
    }

    if (modEditare.value) {
      // 🟢 CERERE PUT PENTRU EDITARE
      const raspuns = await axios.put(`http://localhost:8080/api/plante/admin/${formPlanta.value.id}`, datePlantaJava, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      alert("✏️ " + raspuns.data) 
      incarcaPlante() 
    } else {
      // 🟢 CERERE POST PENTRU ADĂUGARE
      const raspuns = await axios.post(`http://localhost:8080/api/plante/admin/${adminId}`, datePlantaJava, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      alert("🌿 " + raspuns.data) 
      incarcaPlante() 
    }
    
    inchideModal()
  } catch (eroare) {
    console.error("Eroare la salvarea plantei:", eroare)
    
    if (eroare.response && eroare.response.data) {
      alert("🛑 Eroare din Java:\n" + eroare.response.data)
    } else {
      alert("Nu am putut contacta serverul.")
    }
  }
}

// 🟢 FUNCȚIA DE ȘTERGERE REALĂ
const stergePlanta = async (id) => {
  if(confirm('Ești sigur că vrei să ștergi definitiv această plantă din Baza de Date?')) {
    try {
      const token = localStorage.getItem('jwt_token')
      
      const raspuns = await axios.delete(`http://localhost:8080/api/plante/admin/${id}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      })
      
      alert("🗑️ " + raspuns.data)
      incarcaPlante() // Actualizăm lista instantaneu pe ecran
      
    } catch (eroare) {
      console.error("Eroare la ștergerea plantei:", eroare)
      if (eroare.response && eroare.response.data) {
        alert("🛑 Eroare la ștergere:\n" + eroare.response.data)
      } else {
        alert("A apărut o problemă la ștergerea plantei.")
      }
    }
  }
}
</script>

<style scoped>
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

.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 1000; padding: 20px; box-sizing: border-box; }
.modal-content { background: var(--crem-fundal); width: 100%; max-width: 650px; border-radius: 20px; position: relative; padding: 30px; box-shadow: 0 20px 50px rgba(0,0,0,0.3); animation: popUp 0.3s ease-out forwards; max-height: 90vh; overflow-y: auto; }
@keyframes popUp { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }
.btn-inchide { position: absolute; top: 15px; right: 15px; background: white; border: none; border-radius: 50%; width: 35px; height: 35px; font-size: 1.2rem; color: #555; cursor: pointer; box-shadow: 0 2px 5px rgba(0,0,0,0.2); transition: 0.3s; }
.btn-inchide:hover { background: #fee; color: #e74c3c; transform: scale(1.1); }
.titlu-modal { margin: 0 0 10px 0; color: var(--verde-inchis); }
.separator { border: none; border-top: 1px solid #ddd; margin-bottom: 20px; }

.formular-plante { display: flex; flex-direction: column; gap: 15px; }
.rand-formular { display: flex; gap: 15px; }
.grup-input { flex: 1; display: flex; flex-direction: column; }
.grup-input.complet { width: 100%; }
.grup-input label { font-weight: bold; font-size: 0.9rem; color: #444; margin-bottom: 5px; }
.grup-input input, .grup-input textarea, .input-select { padding: 10px; border: 1px solid #ccc; border-radius: 8px; font-size: 1rem; font-family: inherit; transition: 0.3s; background: white; }
.grup-input input:focus, .grup-input textarea:focus, .input-select:focus { outline: none; border-color: var(--verde-inchis); box-shadow: 0 0 5px rgba(143, 175, 15, 0.3); }

.checkbox-container { justify-content: center; }
.checkbox-label { display: flex; align-items: center; gap: 8px; font-weight: bold; color: #333; cursor: pointer; font-size: 0.95rem; }
.checkbox-label input { width: 18px; height: 18px; cursor: pointer; }

.zona-specifica { background: #f4faeb; border-left: 4px solid var(--verde-inchis); padding: 15px; border-radius: 8px; margin: 10px 0; }
.titlu-specific { margin: 0 0 15px 0; color: var(--verde-inchis); font-size: 1rem; }

.zona-previzualizare-url { margin-top: 10px; border: 2px dashed var(--verde-deschis); border-radius: 8px; height: 150px; display: flex; justify-content: center; align-items: center; overflow: hidden; background: white; }
.previzualizare-img-url { max-width: 100%; max-height: 100%; object-fit: contain; }

.actiuni-modal { display: flex; justify-content: flex-end; gap: 15px; margin-top: 20px; }
.btn-mare-verde { background-color: var(--verde-inchis); color: white; border: none; padding: 10px 25px; border-radius: 8px; font-weight: bold; font-size: 1.05rem; cursor: pointer; transition: 0.3s; }
.btn-mare-verde:hover { background: var(--verde-deschis); color: #333; }

@media (max-width: 600px) {
  .rand-formular { flex-direction: column; gap: 15px; }
}
</style>