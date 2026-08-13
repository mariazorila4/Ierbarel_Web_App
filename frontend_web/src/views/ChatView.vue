<template>
  <div class="page-wrapper">
    <div class="glass-card chat-card">
      
      <!-- Header Chat -->
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <div class="header-titlu">
          <MascotaGhiocel class="avatar-header" />
          <h2 class="titlu">Discută cu Ghiocel</h2>
        </div>
      </div>

      <!-- Zona de istoric mesaje -->
      <div class="chat-istoric" ref="zonaChat">
        <div 
          v-for="(mesaj, index) in istoricMesaje" 
          :key="index" 
          :class="['rand-mesaj', mesaj.rol === 'ghiocel' ? 'mesaj-ai' : 'mesaj-user']"
        >
          <!-- Avatarul apare doar la mesajele trimise de Ghiocel -->
          <div v-if="mesaj.rol === 'ghiocel'" class="avatar-container">
            <MascotaGhiocel class="avatar-mesaj" />
          </div>
          
          <div class="bula-mesaj" v-html="formateazaMarkdown(mesaj.text)">
          </div>
        </div>
      </div>

      <!-- Sugestii (Opțiuni rapide) -->
      <div class="sugestii" v-if="arataSugestii">
        <button v-for="optiune in optiuniRapide" :key="optiune" @click="trimiteMesaj(optiune)" class="chip-sugestie">
          {{ optiune }}
        </button>
      </div>

      <!-- Bara de introducere text liber -->
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

    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios' // Folosim Axios pentru a vorbi cu Spring Boot
import MascotaGhiocel from '../components/MascotaGhiocel.vue'

const router = useRouter()
const mergiInapoi = () => router.push('/dashboard')

const formateazaMarkdown=(text)=>{
  if(!text) return '';

  let html=text;
  html=html.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
  html=html.replace(/\*(.*?)\*/g, '<em>$1</em>');
  html=html.replace(/\n/g, '<br>');

  return html;
}

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

const trimiteMesaj = async (text) => {
  if (!text.trim()) return

  // 1. Punem mesajul utilizatorului pe ecran
  istoricMesaje.value.push({ rol: 'user', text: text })
  arataSugestii.value = false
  mesajNou.value = '' 
  await scrollLaFinal()

  // 2. Afișăm statusul de încărcare al lui Ghiocel
  istoricMesaje.value.push({ rol: 'ghiocel', text: 'Mă gândesc... 🌿', isLoading: true })
  await scrollLaFinal()

  try {
    const token=localStorage.getItem('jwt_token');

    if(!token){
      console.error('Nu a putut fi gasit tokenul! Nu esti logat!');
      return;
    }

    const raspunsBackend = await axios.post('http://localhost:8080/api/chat/trimite', { 
      mesaj: text 
    },
    {
      headers:{
        'Authorization':`Bearer ${token}`
      }
    });

    // 4. Scoatem mesajul de așteptare...
    istoricMesaje.value.pop()
    
    istoricMesaje.value.push({ rol: 'ghiocel', text: raspunsBackend.data.mesaj })

  } catch (eroare) {
    console.error("Eroare la conectarea cu Spring Boot:", eroare)
    istoricMesaje.value.pop()
    istoricMesaje.value.push({ rol: 'ghiocel', text: 'Scuze, am pierdut conexiunea cu serverul (Spring Boot). Verifică dacă backend-ul este pornit! 🥀' })
  }

  await scrollLaFinal()
}

const trimiteMesajDinInput = () => trimiteMesaj(mesajNou.value)

const scrollLaFinal = async () => {
  await nextTick()
  if (zonaChat.value) {
    zonaChat.value.scrollTop = zonaChat.value.scrollHeight
  }
}
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; padding: 20px; }
.chat-card { background: rgba(255, 255, 255, 0.95); padding: 0; border-radius: 20px; width: 100%; max-width: 800px; height: 85vh; display: flex; flex-direction: column; border: 3px solid var(--verde-deschis); box-shadow: 0 10px 30px rgba(0,0,0,0.1); overflow: hidden; }

/* HEADER */
.header { display: flex; align-items: center; justify-content: space-between; padding: 20px; border-bottom: 2px solid var(--crem-fundal); background: white; }
.header-titlu { display: flex; align-items: center; gap: 10px; }
.avatar-header { width: 40px; height: 40px; }
.titlu { color: var(--verde-inchis); margin: 0; font-size: 1.5rem; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #f0f0f0; }

/* ISTORIC CHAT */
.chat-istoric { flex: 1; padding: 20px; overflow-y: auto; display: flex; flex-direction: column; gap: 20px; background: #fafdf5; }

.rand-mesaj { display: flex; align-items: flex-end; gap: 10px; width: 100%; }

.mesaj-ai { justify-content: flex-start; }
.mesaj-user { justify-content: flex-end; }

.avatar-container { flex-shrink: 0; width: 45px; height: 45px; background: white; border-radius: 50%; border: 2px solid var(--verde-deschis); display: flex; justify-content: center; align-items: center; overflow: hidden; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.avatar-mesaj { width: 90%; margin-top: 5px; }

.bula-mesaj { max-width: 70%; padding: 12px 18px; border-radius: 15px; font-size: 1rem; line-height: 1.5; box-shadow: 0 2px 5px rgba(0,0,0,0.05); }
.mesaj-ai .bula-mesaj { background-color: white; color: #444; border: 1px solid var(--verde-deschis); border-bottom-left-radius: 2px; }
.mesaj-user .bula-mesaj { background-color: var(--verde-inchis); color: white; border-bottom-right-radius: 2px; }

/* OPȚIUNI RAPIDE (SUGESTII) */
.sugestii { display: flex; flex-wrap: wrap; gap: 10px; padding: 10px 20px; background: white; border-top: 1px solid #eee; }
.chip-sugestie { background: #f9fdf2; border: 1px solid var(--verde-deschis); color: var(--verde-inchis); padding: 8px 15px; border-radius: 20px; font-size: 0.9rem; cursor: pointer; transition: 0.2s; white-space: nowrap; }
.chip-sugestie:hover { background: var(--verde-deschis); color: #333; }

/* INPUT */
.zona-input { display: flex; gap: 10px; padding: 20px; background: white; border-top: 1px solid #eee; }
.input-chat { flex: 1; padding: 12px 20px; border: 2px solid #ddd; border-radius: 25px; font-size: 1rem; outline: none; transition: 0.3s; }
.input-chat:focus { border-color: var(--verde-inchis); }
.btn-trimite { background: var(--verde-inchis); color: white; border: none; padding: 0 25px; border-radius: 25px; font-weight: bold; cursor: pointer; transition: 0.3s; }
.btn-trimite:hover:not(:disabled) { background: var(--verde-deschis); color: #333; }
.btn-trimite:disabled { background: #ccc; cursor: not-allowed; }
</style>