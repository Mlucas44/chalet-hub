<template>
  <header style="padding: 12px;">
    <nav>
      <router-link to="/">Accueil</router-link>
    </nav>
  </header>
  <main style="padding: 12px;">
    <router-view />
  </main>
</template>

<script setup>
// rien ici pour l’instant
</script>

<style>
nav a { margin-right: 12px; }
</style>
