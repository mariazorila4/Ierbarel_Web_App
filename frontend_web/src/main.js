import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// Setează portul backend-ului local
axios.defaults.baseURL = 'http://localhost:8080'

// ACEASTA ESTE LINIA CRITICĂ:
import './assets/main.css'

const app = createApp(App)

app.use(router)
app.mount('#app')