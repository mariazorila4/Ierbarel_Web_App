<template>
  <div class="page-wrapper">
    <div class="glass-card">
      <h2 class="titlu">Creare Cont Nou</h2>
      
      <form @submit.prevent="gestioneazaInregistrarea" class="formular">
        <div class="input-group">
          <label>Nume utilizator (Username)</label>
          <input type="text" v-model.trim="username" placeholder="ex: BotanistulZelos" required />
        </div>

        <div class="input-group">
          <label>Email</label>
          <input type="email" v-model.trim="email" placeholder="nume@exemplu.com" required />
          <!-- Aici afișăm eroarea dacă email-ul există sau e invalid -->
          <p v-if="eroareEmail" class="text-eroare text-stanga">{{ eroareEmail }}</p>
        </div>
        
        <div class="input-group">
          <label>Parolă</label>
          <input type="password" v-model="parola" placeholder="********" required />
          
          <div class="card-cerinte">
            <p class="titlu-cerinte">Parola trebuie să conțină:</p>
            <ul class="lista-cerinte">
              <li v-for="regula in reguliParola" :key="regula.id" :class="['cerinta', { 'indeplinita': regula.esteBifata }]">
                <span class="iconita-frunza">🌿</span> {{ regula.text }}
              </li>
            </ul>
          </div>
          
          <p v-if="eroareTrimitere" class="text-eroare">Te rugăm să îndeplinești toate cerințele parolei!</p>
        </div>
        
        <button type="submit" class="btn-mare">Înregistrează-te</button>
      </form>

      <div class="linkuri-utile">
        <p>Ai deja un cont? <a href="#" @click.prevent="mergiLaLogin" class="link-verde">Loghează-te aici</a></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('')
const email = ref('')
const parola = ref('')
const eroareTrimitere = ref(false)
const eroareEmail = ref('') // Variabilă nouă pentru erorile de email

// Simulam baza de date (pentru test). Aici vei pune mai târziu verificarea din backend.
const emailuriExistenteBazaDeDate = ['test@exemplu.com', 'salut@ierbarel.ro']

const reguliParola = computed(() => {
  const p = parola.value
  return [
    { id: 1, text: 'Minim 8 caractere', esteBifata: p.length >= 8 },
    { id: 2, text: 'Cel puțin o literă MARE', esteBifata: /[A-Z]/.test(p) },
    { id: 3, text: 'Cel puțin o literă mică', esteBifata: /[a-z]/.test(p) },
    { id: 4, text: 'Cel puțin o cifră', esteBifata: /[0-9]/.test(p) },
    { id: 5, text: 'Un caracter special (!@#$ etc.)', esteBifata: /[!@#$%^&*(),.?":{}|<>]/.test(p) }
  ]
})

const parolaEsteValida = computed(() => reguliParola.value.every(regula => regula.esteBifata))

const gestioneazaInregistrarea = () => {
  eroareEmail.value = '' // Resetăm eroarea de email
  eroareTrimitere.value = false

  // 1. Verificare: Este un "hacker" care încearcă să se înregistreze ca admin?
  if (email.value.includes('@admin')) {
    eroareEmail.value = 'Nu poți folosi "@admin". Această adresă este rezervată!'
    return
  }

  // 2. Verificare: Email-ul există deja în "baza de date"?
  if (emailuriExistenteBazaDeDate.includes(email.value)) {
    eroareEmail.value = 'Acest email este deja asociat unui cont!'
    return
  }

  // 3. Verificăm parola
  if (!parolaEsteValida.value) {
    eroareTrimitere.value = true
    return
  }

  // Dacă ajungem aici, totul e perfect!
  console.log('Trimit spre Spring Boot:', { username: username.value, email: email.value })
  router.push('/login') // Îl trimitem la login cu succes
}

const mergiLaLogin = () => router.push('/login')
</script>

<style scoped>
/* Păstrează CSS-ul tău anterior aici, dar adaugă clasa de mai jos pentru eroare email */
.text-stanga { text-align: left; margin-bottom: 0; }
.text-eroare { color: #e74c3c; font-size: 0.85rem; font-weight: bold; margin-top: 5px; text-align: center; }

/* Restul claselor: page-wrapper, glass-card, input-group, etc. din codul precedent */
.page-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 15px; border: 3px solid var(--verde-deschis); width: 100%; max-width: 400px; box-shadow: 0 8px 20px rgba(0,0,0,0.05); }
.titlu { color: var(--verde-inchis); text-align: center; margin-bottom: 2rem; }
.formular { display: flex; flex-direction: column; gap: 1.2rem; }
.input-group label { display: block; margin-bottom: 0.5rem; color: #333; font-weight: 600; }
.input-group input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 8px; font-size: 1rem; box-sizing: border-box; transition: 0.3s; }
.input-group input:focus { outline: none; border-color: var(--verde-deschis); box-shadow: 0 0 5px var(--verde-deschis); }
.card-cerinte { background-color: #f9fdf2; border: 1px solid #e1eed3; border-radius: 8px; padding: 12px; margin-top: 10px; }
.titlu-cerinte { margin: 0 0 8px 0; font-size: 0.85rem; font-weight: bold; color: #555; }
.lista-cerinte { list-style: none; padding: 0; margin: 0; display: flex; flex-direction: column; gap: 5px; }
.cerinta { font-size: 0.85rem; color: #888; display: flex; align-items: center; transition: all 0.3s ease; }
.iconita-frunza { margin-right: 8px; filter: grayscale(100%) opacity(30%); transition: all 0.3s ease; }
.cerinta.indeplinita { color: var(--verde-inchis); font-weight: 500; }
.cerinta.indeplinita .iconita-frunza { filter: grayscale(0%) opacity(100%); }
.btn-mare { background-color: var(--verde-inchis); color: white; border: none; padding: 12px; border-radius: 8px; font-size: 1.1rem; cursor: pointer; font-weight: bold; margin-top: 10px; transition: 0.3s; }
.btn-mare:hover { background-color: var(--verde-deschis); color: #333; }
.linkuri-utile { margin-top: 1.5rem; text-align: center; font-size: 0.9rem; }
.link-verde { color: var(--verde-inchis); font-weight: bold; text-decoration: none; }
</style>