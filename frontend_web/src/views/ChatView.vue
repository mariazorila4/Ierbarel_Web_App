<template>
  <div class="page-wrapper">
    <div class="glass-card chat-wrapper">
      
      <!-- 📌 SIDEBAR STÂNGA -->
      <aside class="sidebar">
        <button @click="conversatieNoua" class="btn-chat-nou">
          <span class="icon-plus">➕</span> Conversație Nouă
        </button>

        <div class="sidebar-sectiune">
          <div class="sidebar-label">Istoric Conversații</div>
          <div class="lista-sesiuni">
            <div 
              class="item-sesiune activ"
              title="Conversația curentă cu Ghiocel"
            >
              <span class="icon-chat">💬</span>
              <span class="titlu-sesiune">Sfaturi Botanice Ghiocel</span>
            </div>
          </div>
        </div>

        <!-- Buton rapid înapoi în josul sidebar-ului -->
        <div class="sidebar-footer">
          <button @click="mergiInapoi" class="btn-secundar-sidebar">
            ⬅ Înapoi la Dashboard
          </button>
        </div>
      </aside>

      <!-- 📌 ZONA PRINCIPALĂ DE CHAT -->
      <main class="main-chat">
        
        <!-- Header Chat -->
        <div class="header">
          <div class="header-titlu">
            <MascotaGhiocel class="avatar-header" />
            <div>
              <h2 class="titlu">Discută cu Ghiocel</h2>
              <span class="status-online">🟢 Asistent AI Activ</span>
            </div>
          </div>
        </div>

        <!-- Zona de istoric mesaje -->
        <div class="chat-istoric" ref="zonaChat">
          <div 
            v-for="(mesaj, index) in istoricMesaje" 
            :key="index" 
            :class="['rand-mesaj', mesaj.rol === 'ghiocel' ? 'mesaj-ai' : 'mesaj-user']"
          >
            <div v-if="mesaj.rol === 'ghiocel'" class="avatar-container">
              <MascotaGhiocel class="avatar-mesaj" />
            </div>
            
            <div class="bula-mesaj" v-html="formateazaMarkdown(mesaj.text)"></div>
          </div>
        </div>

        <!-- Sugestii (Opțiuni rapide) -->
        <div class="sugestii" v-if="arataSugestii">
          <button v-for="optiune in optiuniRapide" :key="optiune" @click="trimiteMesaj(optiune)" class="chip-sugestie">
            {{ optiune }}
          </button>
        </div>

        <!-- Bara de introducere text -->
        <div class="zona-input">
          <input 
            type="text" 
            v-model="mesajNou" 
            @keyup.enter="trimiteMesajDinInput"
            placeholder="Întreabă-mă orice despre plante..." 
            class="input-chat"
          />
          <button @click="trimiteMesajDinInput" class="btn-trimite" :disabled="!mesajNou.trim()">
            Trimite 🌿
          </button>
        </div>

      </main>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import MascotaGhiocel from '../components/MascotaGhiocel.vue'

const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')

const zonaChat = ref(null)
const mesajNou = ref('')
const arataSugestii = ref(true)

const istoricMesaje = ref([
  { rol: 'ghiocel', text: 'Salut! Eu sunt Ghiocel, asistentul tău botanic motorizat de inteligență artificială. Cu ce te pot ajuta astăzi în grădina ta?' }
])

const optiuniRapide = ref([
  'Cum ud corect o plantă?',
  'De ce se îngălbenesc frunzele?',
  'Ce plante supraviețuiesc la umbră?',
  'Cum scap de dăunători ecologic?'
])

const formateazaMarkdown = (text) => {
  if (!text) return '';
  let html = text;
  html = html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
  html = html.replace(/\*(.*?)\*/g, '<em>$1</em>');
  html = html.replace(/\n/g, '<br>');
  return html;
}

const incarcaIstoricChat = async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    if (!token) return

    const response = await axios.get('/api/chat/istoric', {
      headers: { Authorization: `Bearer ${token}` }
    })

    if (Array.isArray(response.data) && response.data.length > 0) {
      istoricMesaje.value = response.data.map(m => ({
        rol: m.este_bot ? 'ghiocel' : 'user',
        text: m.mesaj
      }))
      arataSugestii.value = false
    }
    await scrollLaFinal()
  } catch (err) {
    console.error('Eroare la încărcarea istoricului de chat:', err)
  }
}

const trimiteMesaj = async (text) => {
  if (!text.trim()) return

  istoricMesaje.value.push({ rol: 'user', text: text })
  arataSugestii.value = false
  mesajNou.value = '' 
  await scrollLaFinal()

  istoricMesaje.value.push({ rol: 'ghiocel', text: 'Mă gândesc... 🌿', isLoading: true })
  await scrollLaFinal()

  try {
    const token = localStorage.getItem('jwt_token')
    if (!token) return

    const raspunsBackend = await axios.post('/api/chat/trimite', { 
      mesaj: text 
    },
    {
      headers: { 'Authorization': `Bearer ${token}` }
    })

    istoricMesaje.value.pop()
    istoricMesaje.value.push({ rol: 'ghiocel', text: raspunsBackend.data.mesaj })

  } catch (eroare) {
    console.error("Eroare la conectarea cu Spring Boot:", eroare)
    istoricMesaje.value.pop()
    istoricMesaje.value.push({ rol: 'ghiocel', text: 'Scuze, am pierdut conexiunea cu serverul. Verifică dacă backend-ul este pornit! 🥀' })
  }

  await scrollLaFinal()
}

const conversatieNoua = () => {
  istoricMesaje.value = [
    { rol: 'ghiocel', text: 'O nouă conversație a început! Cu ce te pot ajuta?' }
  ]
  arataSugestii.value = true
}

const trimiteMesajDinInput = () => trimiteMesaj(mesajNou.value)

const scrollLaFinal = async () => {
  await nextTick()
  if (zonaChat.value) {
    zonaChat.value.scrollTop = zonaChat.value.scrollHeight
  }
}

onMounted(() => {
  incarcaIstoricChat()
})
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; box-sizing: border-box; }

/* LAYOUT PRINCIPAL BARA STÂNGA + ZONA DE CHAT */
.chat-wrapper {
  background: rgba(255, 255, 255, 0.95);
  padding: 0;
  border-radius: 20px;
  width: 100%;
  max-width: 1000px;
  height: 85vh;
  display: flex;
  flex-direction: row;
  border: 3px solid var(--verde-deschis);
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
  overflow: hidden;
}

/* 🎨 STILURI SIDEBAR STÂNGA */
.sidebar {
  width: 260px;
  background: #f4f9ed;
  border-right: 2px solid var(--crem-fundal);
  display: flex;
  flex-direction: column;
  padding: 20px 15px;
  gap: 20px;
  flex-shrink: 0;
}

.btn-chat-nou {
  background-color: var(--verde-inchis);
  color: white;
  border: none;
  padding: 12px 15px;
  border-radius: 12px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: background-color 0.2s, transform 0.1s;
}

.btn-chat-nou:hover {
  background-color: var(--verde-deschis);
  color: #333;
}

.sidebar-sectiune {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sidebar-label {
  font-size: 0.8rem;
  font-weight: bold;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding-left: 5px;
}

.lista-sesiuni {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.item-sesiune {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  cursor: pointer;
  background: white;
  border: 1px solid #e2ebd8;
  font-size: 0.9rem;
  color: #444;
  transition: all 0.2s;
}

.item-sesiune.activ {
  background-color: #e8f5e9;
  border-color: var(--verde-inchis);
  font-weight: bold;
  color: var(--verde-inchis);
}

.titlu-sesiune {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-footer {
  margin-top: auto;
  border-top: 1px solid #e0ebcf;
  padding-top: 15px;
}

.btn-secundar-sidebar {
  width: 100%;
  background: white;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 10px;
  cursor: pointer;
  color: #555;
  font-weight: bold;
  font-size: 0.85rem;
  transition: background 0.2s;
}

.btn-secundar-sidebar:hover { background: #fee; color: #e74c3c; }

/* 💬 STILURI MAIN CHAT */
.main-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  background: white;
}

/* HEADER */
.header { display: flex; align-items: center; justify-content: space-between; padding: 15px 20px; border-bottom: 2px solid var(--crem-fundal); background: white; }
.header-titlu { display: flex; align-items: center; gap: 12px; }
.avatar-header { width: 42px; height: 42px; }
.titlu { color: var(--verde-inchis); margin: 0; font-size: 1.3rem; }
.status-online { font-size: 0.75rem; color: #27ae60; font-weight: bold; }

/* ISTORIC CHAT */
.chat-istoric { flex: 1; padding: 20px; overflow-y: auto; display: flex; flex-direction: column; gap: 20px; background: #fafdf5; }
.rand-mesaj { display: flex; align-items: flex-end; gap: 10px; width: 100%; }
.mesaj-ai { justify-content: flex-start; }
.mesaj-user { justify-content: flex-end; }
.avatar-container { flex-shrink: 0; width: 42px; height: 42px; background: white; border-radius: 50%; border: 2px solid var(--verde-deschis); display: flex; justify-content: center; align-items: center; overflow: hidden; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.avatar-mesaj { width: 90%; margin-top: 5px; }

.bula-mesaj { max-width: 70%; padding: 12px 18px; border-radius: 15px; font-size: 0.95rem; line-height: 1.5; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.mesaj-ai .bula-mesaj { background-color: white; color: #444; border: 1px solid var(--verde-deschis); border-bottom-left-radius: 2px; }
.mesaj-user .bula-mesaj { background-color: var(--verde-inchis); color: white; border-bottom-right-radius: 2px; }

/* SUGESTII */
.sugestii { display: flex; flex-wrap: wrap; gap: 10px; padding: 10px 20px; background: white; border-top: 1px solid #eee; }
.chip-sugestie { background: #f9fdf2; border: 1px solid var(--verde-deschis); color: var(--verde-inchis); padding: 8px 15px; border-radius: 20px; font-size: 0.85rem; cursor: pointer; transition: 0.2s; white-space: nowrap; }
.chip-sugestie:hover { background: var(--verde-deschis); color: #333; }

/* INPUT */
.zona-input { display: flex; gap: 10px; padding: 15px 20px; background: white; border-top: 1px solid #eee; }
.input-chat { flex: 1; padding: 12px 20px; border: 2px solid #ddd; border-radius: 25px; font-size: 0.95rem; outline: none; transition: 0.3s; }
.input-chat:focus { border-color: var(--verde-inchis); }
.btn-trimite { background: var(--verde-inchis); color: white; border: none; padding: 0 22px; border-radius: 25px; font-weight: bold; cursor: pointer; transition: 0.3s; }
.btn-trimite:hover:not(:disabled) { background: var(--verde-deschis); color: #333; }
.btn-trimite:disabled { background: #ccc; cursor: not-allowed; }

/* Adaptare telefoane (ascunde sidebar-ul pe ecrane mici) */
@media (max-width: 768px) {
  .sidebar { display: none; }
}
</style>