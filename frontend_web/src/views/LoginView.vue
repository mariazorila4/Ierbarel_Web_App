<template>
  <div class="page-wrapper">
    <div class="glass-card">
      <h2 class="titlu">Intră în cont</h2>
      
      <form @submit.prevent="gestioneazaLogin" class="formular">
        <div class="input-group">
          <label>Email</label>
          <input type="email" v-model.trim="email" placeholder="nume@exemplu.com" required />
        </div>
        
        <div class="input-group">
          <label>Parolă</label>
          <input type="password" v-model="parola" placeholder="********" required />
        </div>
        
        <button type="submit" class="btn-mare">Logare</button>
      </form>

      <div class="linkuri-utile">
        <p>Nu ai cont? <a href="#" @click.prevent="mergiLaRegister" class="link-roz">Creează unul acum</a></p>
        
        <hr class="separator" />
        
        <!-- Butonul secret pentru tine ca dezvoltator -->
        <p class="text-mic">Doar pentru testare:</p>
        <button @click="loginRapidCaAdmin" class="btn-text">🛡️ Logare rapidă ca Admin</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const email = ref('')
const parola = ref('')

const gestioneazaLogin = () => {
  // Logica inteligentă:
  if (email.value.includes('@admin')) {
    console.log('Bun venit, Administrator!')
    router.push('/admin-dashboard') 
  } else {
    console.log('Bun venit, Utilizator!')
    router.push('/dashboard') 
  }
}

// Această funcție te ajută să scapi de tastat când testezi interfața
const loginRapidCaAdmin = () => {
  email.value = 'sef@admin.ro'
  parola.value = 'ParolaGrea123!'
  // Apelăm direct funcția de logare
  gestioneazaLogin()
}

const mergiLaRegister = () => router.push('/register')
</script>

<style scoped>
.page-wrapper { display: flex; justify-content: center; align-items: center; min-height: 100vh; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 15px; border: 3px solid var(--roz-deschis); width: 100%; max-width: 400px; box-shadow: 0 8px 20px rgba(0,0,0,0.05); }
.titlu { color: var(--roz-inchis); text-align: center; margin-bottom: 2rem; }
.formular { display: flex; flex-direction: column; gap: 1.2rem; }
.input-group label { display: block; margin-bottom: 0.5rem; color: #333; font-weight: 600; }
.input-group input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 8px; font-size: 1rem; box-sizing: border-box; }
.input-group input:focus { outline: none; border-color: var(--albastru-pastel); box-shadow: 0 0 5px var(--albastru-pastel); }
.btn-mare { background-color: var(--roz-inchis); color: white; border: none; padding: 12px; border-radius: 8px; font-size: 1.1rem; cursor: pointer; font-weight: bold; margin-top: 10px; transition: 0.3s;}
.btn-mare:hover { background-color: var(--roz-deschis); color: #333; }
.linkuri-utile { margin-top: 2rem; text-align: center; font-size: 0.9rem; }
.link-roz { color: var(--roz-inchis); font-weight: bold; text-decoration: none; }
.separator { border: none; border-top: 1px solid #eee; margin: 1.5rem 0 1rem 0; }
.text-mic { font-size: 0.8rem; color: #999; margin-bottom: 5px; }
.btn-text { background: #f8f9fa; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; color: #555; cursor: pointer; font-weight: bold; transition: 0.3s; width: 100%; }
.btn-text:hover { background: var(--albastru-pastel); color: #333; border-color: var(--albastru-pastel); }
</style>