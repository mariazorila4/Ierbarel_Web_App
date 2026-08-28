<template>
  <div class="page-wrapper">
    <div class="glass-profil-card">
      
      <!-- Buton Înapoi la Dashboard -->
      <button @click="router.push('/dashboard')" class="btn-inapoi">
        ← Înapoi la Dashboard
      </button>

      <h2 class="titlu">⚙️ Setări Profil</h2>
      
      <!-- Preview Avatar Circular -->
      <div class="avatar-preview-container">
        <div class="avatar-cerc-mare">
          <img 
            v-if="formProfil.imagine_url" 
            :src="formProfil.imagine_url" 
            alt="Avatar Profil" 
            class="poza-avatar"
            @error="imagineDefecta = true"
            @load="imagineDefecta = false"
          />
          <span v-if="!formProfil.imagine_url || imagineDefecta" class="icon-fallback">👤</span>
        </div>
        <p class="avatar-label">{{ formProfil.username || 'Utilizator' }}</p>
      </div>

      <!-- Alertă Feedback -->
      <div v-if="mesaj" :class="['alerta', tipMesaj]">
        {{ mesaj }}
      </div>

      <!-- Formular Date Generale -->
      <form @submit.prevent="actualizeazaProfil" class="form-sectiune">
        <h3>Informații Personale</h3>
        
        <div class="form-group">
          <label>Nume Utilizator</label>
          <input v-model="formProfil.username" type="text" placeholder="ex: AnaMaria" required />
        </div>

        <div class="form-group">
          <label>Adresă Email</label>
          <input v-model="formProfil.email" type="email" placeholder="ex: ana@email.com" required />
        </div>

        <div class="form-group">
          <label>URL Imagine Profil</label>
          <input 
            v-model="formProfil.imagine_url" 
            type="url" 
            placeholder="https://domeniu.ro/poza-mea.jpg" 
          />
          <small class="hint-text">Lipește un link direct către o fotografie din online.</small>
        </div>

        <button type="submit" class="btn-salvare">Salvează Modificările</button>
      </form>

      <hr class="separator" />

      <!-- Formular Schimbare Parolă -->
      <form @submit.prevent="schimbaParola" class="form-sectiune">
        <h3>Schimbare Parolă</h3>

        <div class="form-group">
          <label>Parola Curentă</label>
          <input v-model="formParola.parola_veche" type="password" placeholder="••••••••" required />
        </div>

        <div class="form-group">
          <label>Parola Nouă</label>
          <input v-model="formParola.parola_noua" type="password" placeholder="Minim 6 caractere" required />
        </div>

        <button type="submit" class="btn-parola">Modifică Parola</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const formProfil = ref({
  username: '',
  email: '',
  imagine_url: ''
})

const formParola = ref({
  parola_veche: '',
  parola_noua: ''
})

const imagineDefecta = ref(false)
const mesaj = ref('')
const tipMesaj = ref('succes')

const getAuthHeaders = () => {
  const token = localStorage.getItem('jwt_token') 

  if (!token || token === 'null' || token === 'undefined') {
    router.push('/login')
    return { headers: {} }
  }

  return { headers: { Authorization: `Bearer ${token}` } }
}

// 1. Încărcare profil din backend
const incarcaProfil = async () => {
  try {
    const response = await axios.get('http://localhost:8080/api/users/profil', getAuthHeaders())
    formProfil.value.username = response.data.username || ''
    formProfil.value.email = response.data.email || ''
    formProfil.value.imagine_url = response.data.imagine_url || ''
  } catch (err) {
    afiseazaMesaj('Eroare la încărcarea datelor de profil.', 'eroare')
  }
}

// 2. Salvare modificări date generale + imagine
const actualizeazaProfil = async () => {
  try {
    const response = await axios.put(
      'http://localhost:8080/api/users/profil', 
      formProfil.value, 
      getAuthHeaders()
    )
    afiseazaMesaj(response.data.mesaj || 'Profil actualizat cu succes!', 'succes')
  } catch (err) {
    const errText = err.response?.data || 'Nu s-au putut salva modificările.'
    afiseazaMesaj(typeof errText === 'string' ? errText : (errText.mesaj || 'Eroare la actualizare'), 'eroare')
  }
}

// 3. Schimbare parolă
const schimbaParola = async () => {
  try {
    const response = await axios.put(
      'http://localhost:8080/api/users/profil/schimba-parola', 
      formParola.value, 
      getAuthHeaders()
    )
    afiseazaMesaj(response.data.mesaj || 'Parola a fost schimbată cu succes!', 'succes')
    formParola.value = { parola_veche: '', parola_noua: '' }
  } catch (err) {
    const errText = err.response?.data || 'Eroare la schimbarea parolei.'
    afiseazaMesaj(typeof errText === 'string' ? errText : (errText.mesaj || 'Parola curentă este incorectă'), 'eroare')
  }
}

const afiseazaMesaj = (txt, tip) => {
  mesaj.value = txt
  tipMesaj.value = tip
  setTimeout(() => { mesaj.value = '' }, 4000)
}

onMounted(() => {
  incarcaProfil()
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

.glass-profil-card {
  background: rgba(255, 255, 255, 0.95);
  padding: 2.5rem;
  border-radius: 20px;
  border: 3px solid var(--verde-inchis);
  width: 100%;
  max-width: 550px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
  position: relative;
}

.btn-inapoi {
  background: transparent;
  border: none;
  color: var(--verde-inchis);
  font-weight: bold;
  cursor: pointer;
  margin-bottom: 1rem;
  padding: 0;
  transition: transform 0.2s;
}

.btn-inapoi:hover {
  transform: translateX(-3px);
}

.titlu {
  color: var(--verde-inchis);
  margin-top: 0;
  margin-bottom: 1.5rem;
  text-align: center;
}

/* Avatar Preview Circle */
.avatar-preview-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 1.5rem;
}

.avatar-cerc-mare {
  width: 90px;
  height: 90px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid var(--verde-inchis);
  background-color: var(--crem-fundal);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
}

.poza-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.icon-fallback {
  font-size: 2.5rem;
}

.avatar-label {
  margin-top: 8px;
  font-weight: bold;
  color: #444;
}

.form-sectiune {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-sectiune h3 {
  margin: 0.5rem 0 0 0;
  color: #333;
  font-size: 1.1rem;
  text-align: left;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  text-align: left;
}

.form-group label {
  font-weight: bold;
  font-size: 0.9rem;
  color: #555;
}

.form-group input {
  padding: 0.75rem 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 0.95rem;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input:focus {
  border-color: var(--verde-inchis);
}

.hint-text {
  font-size: 0.75rem;
  color: #888;
}

.btn-salvare, .btn-parola {
  padding: 0.85rem;
  border: none;
  border-radius: 10px;
  font-weight: bold;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background-color 0.2s, transform 0.1s;
  margin-top: 0.5rem;
}

.btn-salvare {
  background-color: var(--verde-inchis);
  color: white;
}

.btn-salvare:hover {
  background-color: var(--verde-deschis);
  color: #222;
}

.btn-parola {
  background-color: #34495e;
  color: white;
}

.btn-parola:hover {
  background-color: #2c3e50;
}

.separator {
  margin: 2rem 0 1.5rem 0;
  border: 0;
  border-top: 2px dashed #e0e0e0;
}

.alerta {
  padding: 0.8rem 1rem;
  border-radius: 10px;
  margin-bottom: 1.2rem;
  font-size: 0.9rem;
  font-weight: bold;
  text-align: center;
}

.alerta.succes {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.alerta.eroare {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
</style>