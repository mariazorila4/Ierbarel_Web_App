<template>
  <div class="petals-container">
    <div
      v-for="n in 25"
      :key="n"
      class="petal"
      :style="genereazaStilAleatoriu()"
    ></div>
  </div>
</template>

<script setup>
const genereazaStilAleatoriu = () => {
  const culori = ['#F5BACE', '#BDDB45', '#A9C8FE', '#F088BB', '#8FAF0F'];
  const culoareAleasa = culori[Math.floor(Math.random() * culori.length)];

  return {
    left: `${Math.random() * 100}vw`,
    animationDelay: `${Math.random() * 5}s`,
    animationDuration: `${7 + Math.random() * 8}s`, 
    backgroundColor: culoareAleasa,
    transform: `scale(${0.8 + Math.random() * 1.5})` 
  };
};
</script>

<style scoped>
.petals-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: -1; 
  overflow: hidden;
}

.petal {
  position: absolute;
  top: -15vh;
  width: 40px;
  height: 40px;
  border-radius: 50% 0 50% 50%;
  opacity: 0.75; 
  animation: fall linear infinite;
  box-shadow: 2px 2px 5px rgba(0,0,0,0.1);
}

@keyframes fall {
  0% {
    top: -15vh;
    transform: translateX(0) rotate(0deg);
  }
  50% {
    transform: translateX(30px) rotate(180deg);
  }
  100% {
    top: 110vh;
    transform: translateX(-30px) rotate(360deg);
  }
}
</style>