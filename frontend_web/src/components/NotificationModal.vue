<template>
  <div v-if="esteDeschis" class="notif-overlay" @click.self="inchideCuAnulare">
    <div class="notif-card">
      <div class="notif-header">
        <span class="notif-icon">{{ esteConfirmare ? '❓' : (tip === 'error' ? '🛑' : '🌿') }}</span>
        <h3>{{ titlu }}</h3>
      </div>
      
      <p class="notif-mesaj">{{ mesaj }}</p>

      <div class="notif-actiuni">
        <button v-if="esteConfirmare" @click="inchideCuAnulare" class="btn-secundar-notif">Anulează</button>
        <button @click="inchideCuConfirmare" :class="esteConfirmare || tip === 'error' ? 'btn-rosu-notif' : 'btn-verde-notif'">
          {{ esteConfirmare ? 'Da, confirm' : 'Am înțeles' }}
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
let rezolvaPromise = null

const afiseaza = (opțiuni) => {
  // opțiuni: { titlu, mesaj, tip, esteConfirmare }
  titlu.value = opțiuni.titlu || (opțiuni.esteConfirmare ? 'Confirmare' : 'Notificare')
  mesaj.value = opțiuni.mesaj || ''
  tip.value = opțiuni.tip || 'success'
  esteConfirmare.value = opțiuni.esteConfirmare || false
  esteDeschis.value = true

  return new Promise((resolve) => {
    rezolvaPromise = resolve
  })
}

const inchideCuConfirmare = () => {
  esteDeschis.value = false
  if (rezolvaPromise) rezolvaPromise(true)
}

const inchideCuAnulare = () => {
  esteDeschis.value = false
  if (rezolvaPromise) rezolvaPromise(false)
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
  max-width: 400px;
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

.notif-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.notif-icon {
  font-size: 2.2rem;
}

.notif-header h3 {
  margin: 0;
  color: var(--verde-inchis);
  font-size: 1.3rem;
}

.notif-mesaj {
  color: #555;
  font-size: 1rem;
  line-height: 1.5;
  margin: 0 0 20px 0;
}

.notif-actiuni {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.btn-verde-notif {
  background: var(--verde-inchis);
  color: white;
  border: none;
  padding: 10px 22px;
  border-radius: 10px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}
.btn-verde-notif:hover { background: var(--verde-deschis); color: #333; }

.btn-rosu-notif {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 10px 22px;
  border-radius: 10px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}
.btn-rosu-notif:hover { background: #c0392b; }

.btn-secundar-notif {
  background: #f0f0f0;
  color: #555;
  border: 1px solid #ccc;
  padding: 10px 20px;
  border-radius: 10px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}
.btn-secundar-notif:hover { background: #e0e0e0; }
</style>