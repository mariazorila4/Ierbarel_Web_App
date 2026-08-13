<template>
  <div class="page-wrapper">
    <div class="glass-card full-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <h2 class="titlu">📚 Ierbarul Meu</h2>
      </div>

      <p class="descriere">Aici sunt toate plantele pe care le-ai identificat și salvat. Apasă pe oricare pentru detalii.</p>
      
      <p v-if="planteleMele.length === 0 && !seIncarca" class="mesaj-gol">
        Ierbarul tău este gol! Explorează Ierbarul Global pentru a adăuga plante. 🌱
      </p>

      <div class="grila-plante">
        <div 
          v-for="planta in planteleMele" 
          :key="planta.id" 
          class="card-planta"
          @click="deschideDetalii(planta)"
        >
          <img :src="planta.imagineUrl || 'https://images.unsplash.com/photo-1550949826-38d77d121c2c?w=500&q=80'" :alt="planta.nume_uzual" class="poza-planta" />
          <div class="info-planta">
            <h3>{{ planta.nume_uzual }}</h3>
            <p class="nume-stiintific">{{ planta.denumire_stiintifica }}</p>
            
            <!-- BUTONUL DE ȘTERGERE -->
            <button class="btn-stergere" @click.stop="stergePlanta(planta.id)">
              🗑️ Șterge
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Fereastra Modală -->
    <div v-if="plantaSelectata" class="modal-overlay" @click.self="inchideDetalii">
      <div class="modal-content">
        <button class="btn-inchide" @click="inchideDetalii">✖</button>
        <div class="modal-layout">
          <img :src="plantaSelectata.imagineUrl || 'https://images.unsplash.com/photo-1550949826-38d77d121c2c?w=500&q=80'" class="poza-detaliu" />
          <div class="detalii-text">
            <h2 class="nume-mare">{{ plantaSelectata.nume_uzual }}</h2>
            <p class="nume-stiintific-mare">{{ plantaSelectata.denumire_stiintifica }}</p>
            <hr class="separator-modal" />
            <div class="info-grid">
              <p><strong>🌿 Familie:</strong> {{ plantaSelectata.familie }}</p>
              <p><strong>🌸 Înflorire:</strong> {{ plantaSelectata.perioada_inflorire }}</p>
            </div>
            <div class="sectiune-descriere">
              <h4>Descriere:</h4>
              <p>{{ plantaSelectata.descriere }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')

const plantaSelectata = ref(null)
const planteleMele = ref([])
const seIncarca = ref(true)

const deschideDetalii = (planta) => {
  plantaSelectata.value = planta
  document.body.style.overflow = 'hidden'
}

const inchideDetalii = () => {
  plantaSelectata.value = null
  document.body.style.overflow = 'auto'
}

// 1. PRELUĂM PLANTELE UTILIZATORULUI LA ÎNCĂRCARE
onMounted(async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const userId = localStorage.getItem('user_id') // Luăm ID-ul salvat la login

    const raspuns = await axios.get(`http://localhost:8080/api/users/general/${userId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    
    // În backend ai definit variabila planteFavorite în clasa General
    planteleMele.value = raspuns.data.planteFavorite || []
  } catch (eroare) {
    console.error("Eroare la preluarea ierbarului:", eroare)
  } finally {
    seIncarca.value = false
  }
})

// 2. FUNCȚIA DE ȘTERGERE DIN IERBAR
const stergePlanta = async (plantaId) => {
  if (!confirm("Ești sigur că vrei să ștergi această plantă din colecția ta?")) return;

  try {
    const token = localStorage.getItem('jwt_token')
    await axios.delete(`http://localhost:8080/api/plante/ierbar-personal/${plantaId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    
    // O eliminăm și de pe ecran instant
    planteleMele.value = planteleMele.value.filter(planta => planta.id !== plantaId)
    
  } catch (eroare) {
    console.error("Eroare la ștergere:", eroare)
    alert("Nu am putut șterge planta.")
  }
}
</script>

<style scoped>
/* PĂSTREAZĂ TOT CSS-UL TĂU ȘI ADAUGĂ STILUL PENTRU MESAJ GOL ȘI BUTONUL DE ȘTERGERE */
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--albastru-pastel); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.full-card { max-width: 1000px; } 
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 10px; }
.titlu { color: var(--albastru-pastel); margin: 0; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }
.descriere { color: #666; margin-bottom: 30px; }
.grila-plante { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }
.card-planta { background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); border: 2px solid var(--crem-fundal); transition: transform 0.3s ease; cursor: pointer; }
.card-planta:hover { transform: translateY(-5px); border-color: var(--albastru-pastel); }
.poza-planta { width: 100%; height: 180px; object-fit: cover; }
.info-planta { padding: 15px; text-align: center; }
.info-planta h3 { margin: 0; color: var(--verde-inchis); }
.nume-stiintific { color: #888; font-style: italic; font-size: 0.9rem; margin: 5px 0 10px 0; }

/* STILURI NOI ADAUGATE */
.mesaj-gol { text-align: center; padding: 30px; background: #f9f9f9; border-radius: 12px; color: #666; font-style: italic; }
.btn-stergere { background: #f8d7da; color: #721c24; border: none; padding: 8px 15px; border-radius: 20px; font-size: 0.9rem; font-weight: bold; cursor: pointer; transition: 0.3s; margin-top: 10px; }
.btn-stergere:hover { background: #e2aeb3; }

/* STILURI MODAL */
.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0, 0, 0, 0.6); backdrop-filter: blur(5px); display: flex; justify-content: center; align-items: center; z-index: 1000; padding: 20px; box-sizing: border-box; }
.modal-content { background: var(--crem-fundal); width: 100%; max-width: 800px; border-radius: 20px; position: relative; overflow: hidden; box-shadow: 0 20px 50px rgba(0,0,0,0.3); animation: popUp 0.3s ease-out forwards; }
@keyframes popUp { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }
.btn-inchide { position: absolute; top: 15px; right: 15px; background: white; border: none; border-radius: 50%; width: 35px; height: 35px; font-size: 1.2rem; color: #555; cursor: pointer; box-shadow: 0 2px 5px rgba(0,0,0,0.2); z-index: 10; transition: 0.3s; }
.btn-inchide:hover { background: #fee; color: #e74c3c; transform: scale(1.1); }
.modal-layout { display: flex; flex-direction: row; }
.poza-detaliu { width: 45%; object-fit: cover; min-height: 300px; }
.detalii-text { padding: 30px; width: 55%; box-sizing: border-box; background: white; border-radius: 20px 0 0 20px; }
.nume-mare { margin: 0; color: var(--verde-inchis); font-size: 2rem; }
.nume-stiintific-mare { color: #888; font-style: italic; font-size: 1.1rem; margin-top: 5px; }
.separator-modal { border: none; border-top: 1px solid #eee; margin: 15px 0; }
.info-grid p { margin: 8px 0; color: #444; }
.info-grid strong { color: #333; }
.sectiune-descriere h4 { margin: 20px 0 5px 0; color: var(--verde-inchis); }
.sectiune-descriere p { color: #666; line-height: 1.6; font-size: 0.95rem; margin: 0; }
@media (max-width: 768px) { .modal-layout { flex-direction: column; } .poza-detaliu { width: 100%; height: 250px; min-height: auto; } .detalii-text { width: 100%; border-radius: 0; padding: 20px; } }
</style>