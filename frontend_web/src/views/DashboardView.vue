<template>
  <div class="page-wrapper">
    <div class="glass-dashboard">
      
      <!-- Antet Dashboard -->
      <div class="header">
        <h2 class="titlu">🌱 Bun venit în Ierbărel!</h2>
        
        <div class="header-actiuni">
          <!-- Avatar Circular Interactiv -->
          <div @click="router.push('/profil')" class="avatar-cerc-container" title="Mergi la Profil">
            <img 
              v-if="user.imagine_profil" 
              :src="user.imagine_profil" 
              alt="Profil" 
              class="poza-avatar-cerc"
              @error="user.imagine_profil = ''" 
            />
            <span v-else class="icon-user-fallback">👤</span>
          </div>

          <button @click="deconectare" class="btn-secundar">Deconectare</button>
        </div>
      </div>

      <p class="subtitlu">Ce minunăție a naturii vrei să explorezi astăzi?</p>

      <!-- Grila cu acțiuni -->
      <div class="grid-actiuni">
        <div class="actiune-card card-verde">
          <div class="iconita">📸</div>
          <h3>Identifică Plantă</h3>
          <p>Încarcă o poză și asistentul îți va spune ce specie este.</p>
          <button @click="router.push('/identificare')" class="btn-actiune btn-verde">Începe</button>
        </div>

        <div class="actiune-card card-albastru">
          <div class="iconita">📚</div>
          <h3>Ierbarul Meu</h3>
          <p>Răsfoiește plantele pe care le-ai salvat în colecția ta.</p>
          <button @click="router.push('/ierbar')" class="btn-actiune btn-albastru">Deschide</button>
        </div>

        <div class="actiune-card card-roz">
          <div class="iconita">🌸</div>
          <h3>Curiozități</h3>
          <p>Articole și informații interesante despre lumea botanică.</p>
          <button @click="router.push('/curiozitati')" class="btn-actiune btn-roz">Citește</button>
        </div>

        <div class="actiune-card card-verde-deschis">
          <div class="iconita">🌍</div>
          <h3>Ierbar Global</h3>
          <p>Explorează enciclopedia și caută plante descoperite în întreaga lume.</p>
          <button @click="router.push('/ierbar-global')" class="btn-actiune btn-verde-deschis">Caută Plante</button>
        </div>
      </div>

    </div>

    <!-- Chat Plutitor -->
    <div class="buton-chat-plutitor" @click="router.push('/chat')" title="Întreabă-l pe Ghiocel!">
      <MascotaGhiocel class="mascota-mica" />
      <div class="balon-text">Ai o întrebare?</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import MascotaGhiocel from '../components/MascotaGhiocel.vue'

const router = useRouter()
const user = ref({ username: '', imagine_profil: '' })

// Încărcare profil utilizator conectat
const incarcaUser = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) return

    const response = await axios.get('http://localhost:8080/api/users/profil', {
      headers: { Authorization: `Bearer ${token}` }
    })
    user.value = response.data
  } catch (err) {
    console.error('Eroare la preluarea pozei de profil:', err)
  }
}

const deconectare = () => {
  localStorage.removeItem('token')
  router.push('/login')
}

onMounted(() => {
  incarcaUser()
})
</script>

<style scoped>
.page-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  box-sizing: border-box;
}

.glass-dashboard {
  background: rgba(255, 255, 255, 0.9);
  padding: 2.5rem;
  border-radius: 20px;
  border: 3px solid var(--verde-inchis);
  width: 100%;
  max-width: 900px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.05);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 2px solid var(--crem-fundal);
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.titlu {
  color: var(--verde-inchis);
  margin: 0;
}

.header-actiuni {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* Containerul CERC pentru Poza de Profil */
.avatar-cerc-container {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  border: 2.5px solid var(--verde-inchis);
  background-color: var(--crem-fundal);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.avatar-cerc-container:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(46, 204, 113, 0.3);
}

.poza-avatar-cerc {
  width: 100%;
  height: 100%;
  object-fit: cover; /* Asigură încadrarea perfectă fără deformare */
}

.icon-user-fallback {
  font-size: 1.4rem;
}

.subtitlu {
  color: #666;
  font-size: 1.1rem;
  margin-bottom: 30px;
}

.btn-secundar {
  background-color: transparent;
  color: #888;
  border: 1px solid #ddd;
  padding: 8px 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.3s;
}

.btn-secundar:hover {
  background-color: #fee;
  color: #e74c3c;
  border-color: #e74c3c;
}

.grid-actiuni {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 20px;
}

.actiune-card {
  background: white;
  padding: 20px;
  border-radius: 15px;
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 2px solid transparent;
}

.actiune-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.08);
}

.iconita {
  font-size: 3rem;
  margin-bottom: 10px;
}

.actiune-card h3 {
  margin: 10px 0;
  color: #333;
}

.actiune-card p {
  color: #777;
  font-size: 0.9rem;
  margin-bottom: 20px;
  min-height: 40px;
}

.btn-actiune {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  color: white;
  transition: 0.3s;
}

.card-verde { border-color: var(--verde-deschis); }
.btn-verde { background-color: var(--verde-inchis); }
.btn-verde:hover { background-color: var(--verde-deschis); color: #333; }

.card-albastru { border-color: var(--albastru-pastel); }
.btn-albastru { background-color: var(--albastru-pastel); color: #333; }
.btn-albastru:hover { background-color: #8daeeb; }

.card-roz { border-color: var(--roz-deschis); }
.btn-roz { background-color: var(--roz-inchis); }
.btn-roz:hover { background-color: var(--roz-deschis); color: #333; }

.card-verde-deschis { border-color: #8FAF0F; }
.btn-verde-deschis { background-color: #BDDB45; color: #333; }
.btn-verde-deschis:hover { background-color: #8FAF0F; color: white; }

.buton-chat-plutitor {
  position: fixed;
  bottom: 30px;
  right: 30px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 100;
  transition: transform 0.3s ease;
}
.buton-chat-plutitor:hover { transform: scale(1.1); }
.mascota-mica { width: 80px; filter: drop-shadow(0 5px 15px rgba(0,0,0,0.2)); }
.balon-text {
  background: white;
  padding: 5px 12px;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: bold;
  color: var(--verde-inchis);
  margin-top: -10px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.1);
  border: 2px solid var(--verde-deschis);
}
</style>