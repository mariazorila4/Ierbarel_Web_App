<template>
  <div class="page-wrapper">
    <div class="glass-card full-card">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <h2 class="titlu">📚 Ierbarul Meu</h2>
      </div>

      <p class="descriere">Aici sunt toate plantele pe care le-ai identificat și salvat. Apasă pe oricare pentru detalii.</p>

      <div class="grila-plante">
        <!-- Adăugăm un eveniment @click care apelează funcția de deschidere a detaliilor -->
        <div 
          v-for="planta in bazaDeDateMock" 
          :key="planta.id" 
          class="card-planta"
          @click="deschideDetalii(planta)"
        >
          <img :src="planta.imagineUrl" :alt="planta.nume" class="poza-planta" />
          <div class="info-planta">
            <h3>{{ planta.nume }}</h3>
            <p class="nume-stiintific">{{ planta.numeStiintific }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- FEREASTRA MODALĂ (Apare doar când plantaSelectata nu este null) -->
    <!-- Modificatorul .self asigură că se închide doar dacă dai click pe fundalul întunecat, nu și pe text -->
    <div v-if="plantaSelectata" class="modal-overlay" @click.self="inchideDetalii">
      <div class="modal-content">
        <!-- Butonul de închidere -->
        <button class="btn-inchide" @click="inchideDetalii">✖</button>
        
        <div class="modal-layout">
          <!-- Imaginea în stânga (sau sus pe telefon) -->
          <img :src="plantaSelectata.imagineUrl" class="poza-detaliu" />
          
          <!-- Informațiile în dreapta -->
          <div class="detalii-text">
            <h2 class="nume-mare">{{ plantaSelectata.nume }}</h2>
            <p class="nume-stiintific-mare">{{ plantaSelectata.numeStiintific }}</p>
            
            <hr class="separator-modal" />
            
            <div class="info-grid">
              <p><strong>🌿 Familie:</strong> {{ plantaSelectata.familie }}</p>
              <p><strong>🌸 Înflorire:</strong> {{ plantaSelectata.perioadaInflorire }}</p>
              <p><strong>📅 Adăugată la:</strong> {{ plantaSelectata.data }}</p>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')

// Variabila care va ține minte pe ce plantă ai dat click
const plantaSelectata = ref(null)

// Funcția care se activează la click pe un card
const deschideDetalii = (planta) => {
  plantaSelectata.value = planta
  // Blocăm derularea paginii din spate cât timp modalul e deschis
  document.body.style.overflow = 'hidden'
}

// Funcția care se activează la închiderea ferestrei
const inchideDetalii = () => {
  plantaSelectata.value = null
  // Deblocăm derularea paginii
  document.body.style.overflow = 'auto'
}

// Am îmbogățit datele cu informații suplimentare pentru detaliere
const bazaDeDateMock = ref([
  {
    id: 1,
    nume: 'Mușețel',
    numeStiintific: 'Matricaria chamomilla',
    familie: 'Asteraceae',
    perioadaInflorire: 'Mai - August',
    descriere: 'O plantă erbacee anuală, faimoasă pentru florile sale cu petale albe și mijloc galben. Este recunoscută la nivel mondial pentru proprietățile sale calmante, antiinflamatorii și antiseptice, fiind folosită adesea sub formă de ceai.',
    data: '12 Mai 2024',
    imagineUrl: 'https://joradahl.de/wp-content/uploads/2023/06/kamille.jpg'
  },
  {
    id: 2,
    nume: 'Păpădie',
    numeStiintific: 'Taraxacum officinale',
    familie: 'Asteraceae',
    perioadaInflorire: 'Aprilie - Septembrie',
    descriere: 'Deși este adesea considerată o buruiană de către grădinari, păpădia este o plantă extrem de nutritivă. Frunzele ei sunt excelente în salate, iar rădăcinile pot fi prăjite ca înlocuitor de cafea. Florile galbene se transformă în puf alb semințifer.',
    data: '10 Mai 2024',
    imagineUrl: 'https://www.springfarma.com/media/amasty/blog/1papalidst.jpg'
  },
  {
    id: 3,
    nume: 'Levănțică',
    numeStiintific: 'Lavandula angustifolia',
    familie: 'Lamiaceae',
    perioadaInflorire: 'Iunie - August',
    descriere: 'Un subarbust aromat nativ din regiunea mediteraneană, recunoscut imediat după florile sale de un mov vibrant și mirosul inconfundabil. Uleiul esențial de levănțică este folosit pe scară largă în aromaterapie pentru reducerea stresului și îmbunătățirea somnului.',
    data: '01 Mai 2024',
    imagineUrl: 'https://c.cdnmp.net/592978066/p/m/8/seminte-levantica-lavanda-500-seminte-de-lavanda-parfumata-calitate~14178.jpg'
  }
])
</script>

<style scoped>
/* STILURILE EXISTENTE PENTRU PAGINĂ ȘI GRILĂ */
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; border: 3px solid var(--albastru-pastel); width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.full-card { max-width: 1000px; } 
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 10px; }
.titlu { color: var(--albastru-pastel); margin: 0; text-shadow: 1px 1px 2px rgba(0,0,0,0.1); }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }
.descriere { color: #666; margin-bottom: 30px; }

.grila-plante { display: grid; grid-template-columns: repeat(auto-fill, minmax(250px, 1fr)); gap: 25px; }

/* AM ADAUGAT cursor: pointer PENTRU A INDICA FAPTUL CĂ SE POATE DA CLICK */
.card-planta {
  background: white; border-radius: 15px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05); 
  border: 2px solid var(--crem-fundal); transition: transform 0.3s ease; cursor: pointer;
}
.card-planta:hover { transform: translateY(-5px); border-color: var(--albastru-pastel); }

.poza-planta { width: 100%; height: 180px; object-fit: cover; }
.info-planta { padding: 15px; text-align: center; }
.info-planta h3 { margin: 0; color: var(--verde-inchis); }
.nume-stiintific { color: #888; font-style: italic; font-size: 0.9rem; margin: 5px 0 10px 0; }

/* ========================================= */
/* STILURI NOI PENTRU FEREASTRA MODALĂ      */
/* ========================================= */

/* Fundalul întunecat care acoperă tot ecranul */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* Asigură-te că stă peste petale și peste pagina principală */
  padding: 20px;
  box-sizing: border-box;
}

/* Fereastra propriu-zisă */
.modal-content {
  background: var(--crem-fundal);
  width: 100%;
  max-width: 800px;
  border-radius: 20px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0,0,0,0.3);
  animation: popUp 0.3s ease-out forwards;
}

/* O mică animație la deschidere */
@keyframes popUp {
  0% { transform: scale(0.9); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.btn-inchide {
  position: absolute;
  top: 15px;
  right: 15px;
  background: white;
  border: none;
  border-radius: 50%;
  width: 35px;
  height: 35px;
  font-size: 1.2rem;
  color: #555;
  cursor: pointer;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
  z-index: 10;
  transition: 0.3s;
}
.btn-inchide:hover { background: #fee; color: #e74c3c; transform: scale(1.1); }

/* Layout cu 2 coloane pe ecrane mari */
.modal-layout {
  display: flex;
  flex-direction: row;
}

.poza-detaliu {
  width: 45%;
  object-fit: cover;
  min-height: 300px;
}

.detalii-text {
  padding: 30px;
  width: 55%;
  box-sizing: border-box;
  background: white;
  border-radius: 20px 0 0 20px; /* Rotunjim colțurile unde se îmbină cu poza */
}

.nume-mare { margin: 0; color: var(--verde-inchis); font-size: 2rem; }
.nume-stiintific-mare { color: #888; font-style: italic; font-size: 1.1rem; margin-top: 5px; }
.separator-modal { border: none; border-top: 1px solid #eee; margin: 15px 0; }

.info-grid p { margin: 8px 0; color: #444; }
.info-grid strong { color: #333; }

.sectiune-descriere h4 { margin: 20px 0 5px 0; color: var(--verde-inchis); }
.sectiune-descriere p { color: #666; line-height: 1.6; font-size: 0.95rem; margin: 0; }

/* Adaptare pentru telefoane (punem elementele unele sub altele) */
@media (max-width: 768px) {
  .modal-layout { flex-direction: column; }
  .poza-detaliu { width: 100%; height: 250px; min-height: auto; }
  .detalii-text { width: 100%; border-radius: 0; padding: 20px; }
}
</style>