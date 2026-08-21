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
                <span :class="['badge-status', (user.status || 'Activ') === 'Activ' ? 'activ' : 'blocat']">
                  {{ user.status || 'Activ' }}
                </span>
              </td>
              <td class="actiuni">
                <button @click="schimbaStatus(user.id, user.username, user.status)" class="btn-actiune btn-blocare">
                  {{ (user.status || 'Activ') === 'Activ' ? 'Blochează' : 'Deblochează' }}
                </button>
                <button @click="stergeUtilizator(user.id, user.username)" class="btn-actiune btn-sterge">Șterge</button>
              </td>
            </tr>
          </tbody>
        </table>
        <p v-if="seIncarca" style="text-align: center; margin-top: 20px;">Se încarcă utilizatorii... ⏳</p>
        <p v-else-if="utilizatori.length === 0" style="text-align: center; margin-top: 20px;">Nu există utilizatori în baza de date. 👥</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

// Injectăm serviciul global de notificări
const notificare = inject('notificare')

const router = useRouter()
const mergiInapoi = () => router.push('/admin-dashboard')

const utilizatori = ref([])
const seIncarca = ref(true)

// 1. Funcția care aduce toți utilizatorii din Spring Boot
const incarcaUtilizatori = async () => {
  try {
    seIncarca.value = true
    const token = localStorage.getItem('jwt_token')
    const raspuns = await axios.get('http://localhost:8080/api/admin/utilizatori', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    utilizatori.value = raspuns.data
  } catch (eroare) {
    console.error("Eroare la încărcarea utilizatorilor:", eroare)
  } finally {
    seIncarca.value = false
  }
}

onMounted(() => {
  incarcaUtilizatori()
})

// 2. Funcția de Ștergere a unui utilizator cu NotificationModal
const stergeUtilizator = async (id, username) => {
  const vreaSaSterga = await notificare({
    titlu: "Ștergere Utilizator",
    mesaj: `Ești sigur că vrei să ștergi utilizatorul "${username || '#' + id}"? Această acțiune nu poate fi anulată.`,
    tip: "error",
    esteConfirmare: true
  })

  if (!vreaSaSterga) return

  try {
    const token = localStorage.getItem('jwt_token')
    const raspuns = await axios.delete(`http://localhost:8080/api/admin/utilizatori/${id}`, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    
    await notificare({
      titlu: "Utilizator Șters",
      mesaj: raspuns.data || `Utilizatorul ${username || '#' + id} a fost eliminat cu succes.`,
      tip: "success"
    })

    incarcaUtilizatori()
  } catch (eroare) {
    console.error("Eroare la ștergere:", eroare)
    
    let mesajEroare = "Nu s-a putut șterge utilizatorul."
    if (eroare.response && eroare.response.data) {
      mesajEroare = typeof eroare.response.data === 'object' 
        ? JSON.stringify(eroare.response.data, null, 2) 
        : eroare.response.data
    }

    await notificare({
      titlu: "Eroare la Ștergere",
      mesaj: mesajEroare,
      tip: "error"
    })
  }
}

// 3. Funcția de Blocare/Deblocare cu NotificationModal
const schimbaStatus = async (id, username, statusCurent) => {
  const noulStatus = (statusCurent || 'Activ') === 'Activ' ? 'Blocat' : 'Activ'
  const acțiuneText = noulStatus === 'Blocat' ? 'blochezi' : 'deblochezi'

  const confirmare = await notificare({
    titlu: `${noulStatus === 'Blocat' ? 'Blocare' : 'Deblocare'} Utilizator`,
    mesaj: `Ești sigur că vrei să ${acțiuneText} utilizatorul "${username || '#' + id}"?`,
    tip: noulStatus === 'Blocat' ? 'error' : 'success',
    esteConfirmare: true
  })

  if (!confirmare) return

  try {
    const token = localStorage.getItem('jwt_token')
    await axios.put(`http://localhost:8080/api/admin/utilizatori/${id}/status`, { status: noulStatus }, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    
    await notificare({
      titlu: "Status Actualizat",
      mesaj: `Utilizatorul ${username || '#' + id} este acum ${noulStatus.toLowerCase()}.`,
      tip: "success"
    })

    incarcaUtilizatori()
  } catch (eroare) {
    console.error("Eroare la schimbarea statusului:", eroare)
    
    let mesajEroare = "Nu am putut schimba statusul. Ai coloana 'status' în baza de date?"
    if (eroare.response && eroare.response.data) {
      mesajEroare = typeof eroare.response.data === 'object' 
        ? JSON.stringify(eroare.response.data, null, 2) 
        : eroare.response.data
    }

    await notificare({
      titlu: "Eroare Status",
      mesaj: mesajEroare,
      tip: "error"
    })
  }
}
</script>

<style scoped>
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