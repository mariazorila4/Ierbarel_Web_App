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

        <!-- Afișăm un mesaj de eroare dacă logarea eșuează -->
        <p v-if="mesajEroare" class="eroare-text">{{ mesajEroare }}</p>
        
        <button type="submit" class="btn-mare" :disabled="seIncarca">
          {{ seIncarca ? 'Se încarcă...' : 'Logare' }}
        </button>
      </form>

      <div class="linkuri-utile">
        <p>Nu ai cont? <a href="#" @click.prevent="mergiLaRegister" class="link-roz">Creează unul acum</a></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios' // <-- AM ADĂUGAT AXIOS

const router = useRouter()
const email = ref('')
const parola = ref('')
const mesajEroare = ref('')
const seIncarca = ref(false)

const gestioneazaLogin = async () => {
  mesajEroare.value = ''
  seIncarca.value = true
  
  try {
    // 1. Apelăm backend-ul real
    const raspuns = await axios.post('http://localhost:8080/api/auth/login', {
      email: email.value,
      password: parola.value
    });

    // 2. SALVĂM DATELE ÎN BROWSER 
    localStorage.setItem('jwt_token', raspuns.data.token);
    localStorage.setItem('user_id', raspuns.data.id);
    localStorage.setItem('tip_user', raspuns.data.tip_user);

    console.log('Logare reușită cu backend-ul!');

    // 3. Direcționăm utilizatorul în funcție de rol
    if (raspuns.data.tip_user === 'ADMIN') {
      router.push('/admin-dashboard'); 
    } else {
      router.push('/dashboard'); // sau '/chat'
    }

  } catch (eroare) {
    console.error("Eroare la logare:", eroare);
    
    // Verificăm dacă Spring Boot ne-a trimis un mesaj specific (ex: Cont blocat)
    if (eroare.response && eroare.response.data && eroare.response.data.mesaj) {
      mesajEroare.value = eroare.response.data.mesaj;
    } 
    // Dacă e o eroare generală (401 Unauthorized), afișăm mesajul standard
    else {
      mesajEroare.value = "Email sau parolă incorectă!";
    }
  } finally {
    seIncarca.value = false;
  }
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
.btn-mare:hover:not(:disabled) { background-color: var(--roz-deschis); color: #333; }
.btn-mare:disabled { background-color: #ccc; cursor: not-allowed; }
.linkuri-utile { margin-top: 2rem; text-align: center; font-size: 0.9rem; }
.link-roz { color: var(--roz-inchis); font-weight: bold; text-decoration: none; }
.eroare-text { color: red; font-size: 0.9rem; font-weight: bold; text-align: center; margin: 0; }
</style>