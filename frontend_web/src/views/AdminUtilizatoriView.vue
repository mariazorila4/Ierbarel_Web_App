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
            <tr v-for="user in utilizatoriMock" :key="user.id">
              <td>#{{ user.id }}</td>
              <td><strong>{{ user.username }}</strong></td>
              <td>{{ user.email }}</td>
              <td>
                <span :class="['badge-status', user.status === 'Activ' ? 'activ' : 'blocat']">
                  {{ user.status }}
                </span>
              </td>
              <td class="actiuni">
                <button class="btn-actiune btn-blocare">{{ user.status === 'Activ' ? 'Blochează' : 'Deblochează' }}</button>
                <button class="btn-actiune btn-sterge">Șterge</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const mergiInapoi = () => router.push('/admin-dashboard')

const utilizatoriMock = ref([
  { id: 101, username: 'BotanistulZelos', email: 'test@exemplu.com', status: 'Activ' },
  { id: 102, username: 'MariaFlori', email: 'maria@natura.ro', status: 'Activ' },
  { id: 103, username: 'HackerRau', email: 'spammer@spam.com', status: 'Blocat' },
])
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

/* Stiluri Tabel Admin */
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