<template>
  <FallingPetals />
  <NotificationModal ref="notifRef" />
  <router-view />
</template>

<script setup>
import { ref, provide } from 'vue'
import FallingPetals from './components/FallingPetals.vue'
import NotificationModal from './components/NotificationModal.vue'

const notifRef = ref(null)

// Oferim funcția 'notificare' global în toată aplicația
provide('notificare', (opțiuni) => {
  if (notifRef.value) {
    return notifRef.value.afiseaza(opțiuni)
  }
  return Promise.resolve(false)
})
</script>

<style>
/* Definim culorile globale direct aici */
:root {
  --verde-inchis: #8FAF0F;
  --roz-deschis: #F5BACE;
  --albastru-pastel: #A9C8FE;
  --roz-inchis: #F088BB;
  --crem-fundal: #F5FCD8;
  --verde-deschis: #BDDB45;
}

body {
  margin: 0;
  padding: 0;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: var(--crem-fundal);
  color: #333;
}

/* Stil general pentru linkuri */
a {
  text-decoration: none;
}
</style>