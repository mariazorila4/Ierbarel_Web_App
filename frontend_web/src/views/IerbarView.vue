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
          <img :src="planta.imagine_url || 'https://images.unsplash.com/photo-1550949826-38d77d121c2c?w=500'" :alt="planta.nume_uzual" class="poza-planta" />
          <div class="info-planta">
            <h3>{{ planta.nume_uzual }}</h3>
            <p class="nume-stiintific">{{ planta.denumire_stiintifica }}</p>
            
            <button class="btn-stergere" @click.stop="stergePlanta(planta.id, planta.nume_uzual)">
              🗑️ Șterge
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Fereastra Modală cu Scroll Global -->
    <div v-if="plantaSelectata" class="modal-overlay" @click.self="inchideDetalii">
      <div class="modal-content">
        <button class="btn-inchide" @click="inchideDetalii">✖</button>
        
        <div class="header-imagine">
          <img :src="plantaSelectata.imagine_url || 'https://images.unsplash.com/photo-1550949826-38d77d121c2c?w=500'" class="poza-banner" />
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
            <h4>Descriere:</h4>
            <p>{{ plantaSelectata.descriere }}</p>
          </div>

          <div v-if="plantaSelectata.locatie" class="sectiune-habitat-harta">
            <hr class="separator-modal" />
            <h4>🌍 Habitat Natural:</h4>
            <p class="text-habitat">{{ plantaSelectata.locatie }}</p>

            <h4 class="titlu-harta">📍 Locații pe Hartă:</h4>
            <iframe 
              width="100%" 
              height="250" 
              frameborder="0" 
              class="harta-iframe"
              :src="'https://maps.google.com/maps?q=' + encodeURIComponent(plantaSelectata.locatie) + '&output=embed'" 
              allowfullscreen>
            </iframe>
            <p class="nota-comunitate"><em>* În curând: Aici vor apărea locațiile exacte în care alți pasionați au descoperit această plantă! 🌿</em></p>
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

// Injectăm serviciul global de notificări
const notificare = inject('notificare')

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

onMounted(async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const userId = localStorage.getItem('user_id') 

    const raspuns = await axios.get(`http://localhost:8080/api/users/general/${userId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    
    planteleMele.value = raspuns.data.planteFavorite || []
  } catch (eroare) {
    console.error("Eroare la preluarea ierbarului:", eroare)
  } finally {
    seIncarca.value = false
  }
})

// Ștergerea unei plante favorite folosind mini-cardul NotificationModal
const stergePlanta = async (plantaId, numePlanta) => {
  const vreaSaSterga = await notificare({
    titlu: "Eliminare din Ierbar",
    mesaj: `Ești sigur că vrei să elimini "${numePlanta || 'această plantă'}" din colecția ta personală?`,
    tip: "error",
    esteConfirmare: true
  })

  if (!vreaSaSterga) return

  try {
    const token = localStorage.getItem('jwt_token')
    await axios.delete(`http://localhost:8080/api/plante/ierbar-personal/${plantaId}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    
    planteleMele.value = planteleMele.value.filter(planta => planta.id !== plantaId)
    
    await notificare({
      titlu: "Plantă Eliminată 🌿",
      mesaj: "Planta a fost ștearsă din colecția ta cu succes.",
      tip: "success"
    })
    
  } catch (eroare) {
    console.error("Eroare la ștergere:", eroare)
    
    let mesajEroare = "Nu am putut șterge planta din ierbarul personal."
    if (eroare.response && eroare.response.data) {
      mesajEroare = typeof eroare.response.data === 'object' 
        ? JSON.stringify(eroare.response.data, null, 2) 
        : eroare.response.data
    }

    await notificare({
      titlu: "Eroare la Ștergere",
      mesaj: mesajEroare,
      tip: "error"
    })
  }
}
</script>

<style scoped>
/* CSS PENTRU PAGINĂ */
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
.mesaj-gol { text-align: center; padding: 30px; background: #f9f9f9; border-radius: 12px; color: #666; font-style: italic; }
.btn-stergere { background: #f8d7da; color: #721c24; border: none; padding: 8px 15px; border-radius: 20px; font-size: 0.9rem; font-weight: bold; cursor: pointer; transition: 0.3s; margin-top: 10px; }
.btn-stergere:hover { background: #e2aeb3; }

/* =========================================
   NOUL CSS PENTRU MODAL (CU SCROLL GLOBAL)
   ========================================= */
.modal-overlay { 
  position: fixed; 
  top: 0; left: 0; 
  width: 100vw; height: 100vh; 
  background: rgba(0, 0, 0, 0.7); 
  backdrop-filter: blur(5px); 
  display: flex; 
  justify-content: center; 
  align-items: center; 
  z-index: 9999; 
  padding: 20px; 
  box-sizing: border-box;
}

.modal-content { 
  background: #ffffff; 
  width: 100%; 
  max-width: 550px; 
  border-radius: 16px; 
  position: relative; 
  display: flex; 
  flex-direction: column; 
  max-height: 85vh; 
  overflow-y: auto;
  overflow-x: hidden;
  box-shadow: 0 20px 50px rgba(0,0,0,0.3); 
  animation: popUp 0.3s ease-out forwards; 
}
@keyframes popUp { 0% { transform: scale(0.9); opacity: 0; } 100% { transform: scale(1); opacity: 1; } }

.modal-content::-webkit-scrollbar { width: 6px; }
.modal-content::-webkit-scrollbar-thumb { background-color: var(--verde-deschis); border-radius: 10px; }
.modal-content::-webkit-scrollbar-track { background: transparent; }

.btn-inchide { 
  position: absolute; top: 15px; right: 15px; 
  background: rgba(255, 255, 255, 0.9); border: none; border-radius: 50%; 
  width: 36px; height: 36px; font-size: 1.2rem; color: #333; 
  cursor: pointer; box-shadow: 0 2px 10px rgba(0,0,0,0.2); 
  z-index: 10; display: flex; justify-content: center; align-items: center; 
  transition: 0.2s; 
}
.btn-inchide:hover { background: #fee; color: #e74c3c; transform: scale(1.1); }

.header-imagine { 
  width: 100%; 
  height: 500px; 
  flex-shrink: 0; 
  background: #f0f0f0; 
  border-radius: 16px 16px 0 0; 
}
.poza-banner { 
  width: 100%; 
  height: 100%; 
  object-fit: cover; 
  border-radius: 16px 16px 0 0; 
}

.detalii-text { 
  padding: 25px; 
  flex: 1; 
}

.nume-mare { margin: 0; color: var(--verde-inchis); font-size: 2rem; }
.nume-stiintific-mare { color: #888; font-style: italic; font-size: 1.05rem; margin-top: 5px; }
.separator-modal { border: none; border-top: 1px solid #eee; margin: 15px 0; }

.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
.info-grid p { margin: 5px 0; color: #444; font-size: 0.9rem; }
.info-grid strong { color: #333; }

.sectiune-descriere h4 { margin: 20px 0 10px 0; color: var(--verde-inchis); }
.sectiune-descriere p { color: #555; line-height: 1.6; font-size: 0.95rem; margin: 0; }

.sectiune-habitat-harta h4 { margin: 20px 0 10px 0; color: var(--verde-inchis); }
.text-habitat { color: #555; line-height: 1.5; font-size: 0.9rem; font-style: italic; margin: 0; background: #f4faeb; padding: 12px; border-left: 4px solid var(--verde-deschis); border-radius: 0 8px 8px 0; }
.harta-iframe { border: 0; border-radius: 12px; margin-top: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.08); }
.nota-comunitate { font-size: 0.8rem; color: #888; text-align: center; margin-top: 10px; }

@media (max-width: 500px) { .info-grid { grid-template-columns: 1fr; } .header-imagine { height: 250px; } }
</style>