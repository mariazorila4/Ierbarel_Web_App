<template>
  <FallingPetals />
  <router-view />
</template>

<script setup>
import FallingPetals from './components/FallingPetals.vue'
</script>

<!-- Asigură-te că NU scrie "scoped" lângă style -->
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