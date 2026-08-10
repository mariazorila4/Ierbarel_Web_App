<template>
  <div class="mascota-container">
    <!-- Codul SVG pentru Ghiocel -->
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 300 400" class="ghiocel-svg">
      
      <!-- Pelerina (Spate / Laterale) -->
      <!-- Partea stângă -->
      <path d="M 110 160 Q 50 260 40 340 Q 90 280 110 220 Z" fill="#8FAF0F" />
      <!-- Partea dreaptă -->
      <path d="M 190 160 Q 250 260 260 340 Q 210 280 190 220 Z" fill="#8FAF0F" />

      <!-- Corpul principal (forma de clopoțel cu două piciorușe) -->
      <path d="M 115 160 Q 90 280 80 340 Q 120 330 150 290 Q 180 330 220 340 Q 210 280 185 160 Z" 
            fill="#F5FCD8" stroke="#BDDB45" stroke-width="2"/>

      <!-- Fața -->
      <circle cx="150" cy="120" r="45" fill="#FFE5D9" />

      <!-- Fularul de la gât -->
      <path d="M 110 155 Q 150 170 190 155 L 185 165 Q 150 180 115 165 Z" fill="#A9C8FE" />
      
      <!-- Canafii/Codițele fularului (în dreapta) -->
      <path d="M 185 160 Q 220 165 240 145" stroke="#A9C8FE" stroke-width="5" fill="none" stroke-linecap="round"/>
      <ellipse cx="245" cy="142" rx="8" ry="5" fill="#A9C8FE" transform="rotate(-30 245 142)"/>
      
      <path d="M 185 165 Q 230 185 255 170" stroke="#A9C8FE" stroke-width="5" fill="none" stroke-linecap="round"/>
      <ellipse cx="260" cy="167" rx="8" ry="5" fill="#A9C8FE" transform="rotate(-20 260 167)"/>

      <!-- Ochii (Mari și expresivi) -->
      <circle cx="130" cy="125" r="8" fill="#333333" />
      <circle cx="170" cy="125" r="8" fill="#333333" />
      
      <!-- Sclipirea din ochi (Cute factor) -->
      <circle cx="133" cy="122" r="2.5" fill="#FFFFFF" />
      <circle cx="173" cy="122" r="2.5" fill="#FFFFFF" />

      <!-- Obrăjori -->
      <circle cx="118" cy="135" r="9" fill="#F5BACE" opacity="0.8" />
      <circle cx="182" cy="135" r="9" fill="#F5BACE" opacity="0.8" />

      <!-- Gura (Zâmbet mic) -->
      <path d="M 143 140 Q 150 148 157 140" stroke="#333333" stroke-width="2.5" fill="none" stroke-linecap="round"/>
      
      <!-- Năsuc -->
      <path d="M 148 132 Q 150 134 152 132" stroke="#333333" stroke-width="1.5" fill="none" stroke-linecap="round" opacity="0.5"/>

      <!-- Pălăria / Părul din Petale -->
      <!-- Petala stânga jos -->
      <path d="M 105 75 Q 60 100 65 140 Q 95 125 115 95 Z" fill="#FFFFFF" stroke="#BDDB45" stroke-width="2"/>
      <!-- Petala dreapta jos -->
      <path d="M 195 75 Q 240 100 235 140 Q 205 125 185 95 Z" fill="#FFFFFF" stroke="#BDDB45" stroke-width="2"/>
      <!-- Petala centrală (breton) -->
      <path d="M 110 70 Q 150 120 190 70 Q 150 50 110 70 Z" fill="#FFFFFF" stroke="#BDDB45" stroke-width="2"/>

      <!-- Frunzele de sus (Moțul) -->
      <!-- Frunza mare din stânga -->
      <path d="M 150 65 Q 120 10 90 25 Q 125 45 150 65 Z" fill="#8FAF0F" />
      <!-- Frunza mică din dreapta -->
      <path d="M 150 65 Q 165 35 185 30 Q 170 55 150 65 Z" fill="#8FAF0F" />
      
    </svg>
  </div>
</template>

<style scoped>
.mascota-container {
  width: 200px; /* Poți ajusta mărimea de aici */
  height: auto;
  display: flex;
  justify-content: center;
  align-items: center;
  transition: transform 0.3s ease;
}

.mascota-container:hover {
  transform: translateY(-10px); /* Ghiocel sare puțin când pui mouse-ul pe el! */
}

.ghiocel-svg {
  width: 100%;
  height: 100%;
  filter: drop-shadow(0 10px 15px rgba(0,0,0,0.1));
}
</style>