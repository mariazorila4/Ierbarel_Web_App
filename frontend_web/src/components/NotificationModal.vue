<template>
  <div v-if="esteDeschis" class="notif-overlay" @click.self="inchideCuAnulare">
    <div class="notif-card">
      <div class="notif-header">
        <span class="notif-icon">{{ esteConfirmare || estePrompt ? '❓' : (tip === 'error' ? '🛑' : '🌿') }}</span>
        <h3>{{ titlu }}</h3>
      </div>
      
      <p class="notif-mesaj">{{ mesaj }}</p>

      <!-- AICI ESTE SECRETUL: Câmpul text care apare pentru locație -->
      <input 
        v-if="estePrompt" 
        type="text" 
        v-model="valoareInput" 
        :placeholder="placeholderInput" 
        class="notif-input"
        @keyup.enter="inchideCuConfirmare"
      />

      <div class="notif-actiuni">
        <button v-if="esteConfirmare || estePrompt" @click="inchideCuAnulare" class="btn-secundar-notif">Anulează</button>
        <button @click="inchideCuConfirmare" :class="esteConfirmare || tip === 'error' ? 'btn-rosu-notif' : 'btn-verde-notif'">
          {{ esteConfirmare || estePrompt ? 'Confirmă' : 'Am înțeles' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const esteDeschis = ref(false)
const titlu = ref('')
const mesaj = ref('')
const tip = ref('success') // 'success' sau 'error'
const esteConfirmare = ref(false)

// Variabile noi pentru funcționalitatea de Prompt (Input Text)
const estePrompt = ref(false)
const valoareInput = ref('')
const placeholderInput = ref('')
let rezolvaPromise = null

const afiseaza = (optiuni) => {
  titlu.value = optiuni.titlu || (optiuni.esteConfirmare || optiuni.estePrompt ? 'Confirmare' : 'Notificare')
  mesaj.value = optiuni.mesaj || ''
  tip.value = optiuni.tip || 'success'
  esteConfirmare.value = optiuni.esteConfirmare || false
  
  // Setăm opțiunile pentru input
  estePrompt.value = optiuni.estePrompt || false
  valoareInput.value = optiuni.valoareDefault || ''
  placeholderInput.value = optiuni.placeholder || 'Scrie aici...'
  
  esteDeschis.value = true

  return new Promise((resolve) => {
    rezolvaPromise = resolve
  })
}

const inchideCuConfirmare = () => {
  esteDeschis.value = false
  // Dacă e prompt, returnăm textul scris. Altfel returnăm true.
  if (rezolvaPromise) rezolvaPromise(estePrompt.value ? valoareInput.value : true)
}

const inchideCuAnulare = () => {
  esteDeschis.value = false
  // Dacă e prompt și a anulat, returnăm null. Altfel false.
  if (rezolvaPromise) rezolvaPromise(estePrompt.value ? null : false)
}

defineExpose({ afiseaza })
</script>

<style scoped>
.notif-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  z-index: 100000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.notif-card {
  background: white;
  border-radius: 20px;
  width: 100%;
  max-width: 420px;
  padding: 25px;
  text-align: center;
  box-shadow: 0 15px 35px rgba(0,0,0,0.2);
  border: 3px solid var(--verde-deschis);
  animation: popNotif 0.25s ease-out forwards;
}

@keyframes popNotif {
  0% { transform: scale(0.8); opacity: 0; }
  100% { transform: scale(1); opacity: 1; }
}

.notif-header { display: flex; flex-direction: column; align-items: center; gap: 8px; margin-bottom: 12px; }
.notif-icon { font-size: 2.2rem; }
.notif-header h3 { margin: 0; color: var(--verde-inchis); font-size: 1.3rem; }

.notif-mesaj {
  color: #555;
  font-size: 1rem;
  line-height: 1.5;
  margin: 0 0 20px 0;
  white-space: pre-wrap; 
}

/* Stil NOU pentru input-ul de prompt */
.notif-input {
  width: 100%;
  padding: 12px;
  margin-bottom: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  font-size: 1rem;
  box-sizing: border-box;
  text-align: center;
  transition: 0.3s;
}
.notif-input:focus {
  outline: none;
  border-color: var(--verde-inchis);
  box-shadow: 0 0 5px rgba(0, 128, 0, 0.2);
}

.notif-actiuni { display: flex; justify-content: center; gap: 12px; }

.btn-verde-notif { background: var(--verde-inchis); color: white; border: none; padding: 10px 22px; border-radius: 10px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-verde-notif:hover { background: var(--verde-deschis); color: #333; }

.btn-rosu-notif { background: #e74c3c; color: white; border: none; padding: 10px 22px; border-radius: 10px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-rosu-notif:hover { background: #c0392b; }

.btn-secundar-notif { background: #f0f0f0; color: #555; border: 1px solid #ccc; padding: 10px 20px; border-radius: 10px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.btn-secundar-notif:hover { background: #e0e0e0; }
</style>