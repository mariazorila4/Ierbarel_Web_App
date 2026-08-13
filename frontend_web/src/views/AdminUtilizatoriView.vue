<template>
  <div class="page-wrapper">
    <div class="glass-card wide-card admin-mode">
      <div class="header">
        <button @click="mergiInapoi" class="btn-secundar">⬅ Înapoi</button>
        <h2 class="titlu">👥 Gestionare Utilizatori</h2>
      </div>

      <div class="tabel-container">
        <table class="tabel-admin">
          <thead>
            <tr>
              <th>ID</th>
              <th>Nume Utilizator</th>
              <th>Email</th>
              <th>Status</th>
              <th>Acțiuni</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in utilizatori" :key="user.id">
              <td>#{{ user.id }}</td>
              <td><strong>{{ user.username }}</strong></td>
              <td>{{ user.email }}</td>
              <td>
                <!-- Folosim proprietatea status (dacă nu o ai în DB, o simulăm ca 'Activ' momentan) -->
                <span :class="['badge-status', (user.status || 'Activ') === 'Activ' ? 'activ' : 'blocat']">
                  {{ user.status || 'Activ' }}
                </span>
              </td>
              <td class="actiuni">
                <button @click="schimbaStatus(user.id, user.status)" class="btn-actiune btn-blocare">
                  {{ (user.status || 'Activ') === 'Activ' ? 'Blochează' : 'Deblochează' }}
                </button>
                <button @click="stergeUtilizator(user.id)" class="btn-actiune btn-sterge">Șterge</button>
              </td>
            </tr>
          </tbody>
        </table>
        <p v-if="utilizatori.length === 0" style="text-align: center; margin-top: 20px;">Se încarcă utilizatorii... ⏳</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const mergiInapoi = () => router.push('/admin-dashboard')

// Variabila care va ține datele REALE din baza de date
const utilizatori = ref([])

// 1. Funcția care aduce toți utilizatorii din Spring Boot
const incarcaUtilizatori = async () => {
  try {
    const token = localStorage.getItem('jwt_token')
    const raspuns = await axios.get('http://localhost:8080/api/admin/utilizatori', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    utilizatori.value = raspuns.data
  } catch (eroare) {
    console.error("Eroare la încărcarea utilizatorilor:", eroare)
  }
}

// Apelăm funcția automat când se deschide pagina
onMounted(() => {
  incarcaUtilizatori()
})

// 2. Funcția de Ștergere a unui utilizator
const stergeUtilizator = async (id) => {
  if (!confirm(`Ești sigur că vrei să ștergi utilizatorul cu ID-ul #${id}?`)) return;

  try {
    const token = localStorage.getItem('jwt_token')
    await axios.delete(`http://localhost:8080/api/admin/utilizatori/${id}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    // După ștergere, reîncărcăm lista pentru a o actualiza vizual
    incarcaUtilizatori()
  } catch (eroare) {
    console.error("Eroare la ștergere:", eroare)
    alert("Nu s-a putut șterge utilizatorul.")
  }
}

// 3. Funcția de Blocare/Deblocare (Opțional, dacă adaugi coloana în DB)
const schimbaStatus = async (id, statusCurent) => {
  const noulStatus = (statusCurent || 'Activ') === 'Activ' ? 'Blocat' : 'Activ'
  try {
    const token = localStorage.getItem('jwt_token')
    await axios.put(`http://localhost:8080/api/admin/utilizatori/${id}/status`, { status: noulStatus }, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    incarcaUtilizatori()
  } catch (eroare) {
    console.error("Eroare la schimbarea statusului:", eroare)
    alert("Nu am putut schimba statusul. Ai coloana 'status' în baza de date?")
  }
}
</script>

<style scoped>
/* Păstrezi exact CSS-ul tău, nu trebuie schimbat nimic la el */
.page-wrapper { display: flex; justify-content: center; align-items: flex-start; min-height: 100vh; padding: 40px 20px; }
.glass-card { background: rgba(255, 255, 255, 0.9); padding: 2.5rem; border-radius: 20px; width: 100%; box-shadow: 0 8px 25px rgba(0,0,0,0.05); }
.wide-card { max-width: 900px; }
.admin-mode { border: 3px solid var(--roz-inchis); }
.header { display: flex; align-items: center; gap: 20px; border-bottom: 2px solid var(--crem-fundal); padding-bottom: 15px; margin-bottom: 20px; }
.titlu { color: var(--roz-inchis); margin: 0; }
.btn-secundar { background: white; border: 1px solid #ddd; padding: 8px 15px; border-radius: 8px; cursor: pointer; transition: 0.3s; color: #555; }
.btn-secundar:hover { background: #fee; color: #e74c3c; border-color: #e74c3c; }

.tabel-container { overflow-x: auto; }
.tabel-admin { width: 100%; border-collapse: collapse; text-align: left; }
.tabel-admin th { background-color: var(--roz-deschis); color: #333; padding: 12px; font-weight: bold; }
.tabel-admin td { padding: 12px; border-bottom: 1px solid #eee; color: #555; vertical-align: middle; }
.tabel-admin tr:hover { background-color: #fdfafb; }

.badge-status { padding: 5px 10px; border-radius: 20px; font-size: 0.85rem; font-weight: bold; }
.badge-status.activ { background-color: #d4edda; color: #155724; }
.badge-status.blocat { background-color: #f8d7da; color: #721c24; }

.actiuni { display: flex; gap: 10px; }
.btn-actiune { padding: 6px 12px; border: none; border-radius: 6px; cursor: pointer; font-size: 0.85rem; font-weight: bold; transition: 0.3s; }
.btn-blocare { background-color: #ffeeba; color: #856404; }
.btn-blocare:hover { background-color: #ffdf7e; }
.btn-sterge { background-color: #f8d7da; color: #721c24; }
.btn-sterge:hover { background-color: #f5c6cb; }
</style>