<template>
  <section>
    <h1>Chalets</h1>

    <p v-if="loading">Chargement…</p>
    <p v-if="error" style="color:red">{{ error }}</p>

    <ul v-if="!loading && !error">
      <li v-for="c in chalets" :key="c.id">
        <strong>{{ c.name }}</strong> — {{ c.location }} — {{ c.pricePerNight }} $/nuit
        <router-link :to="`/chalet/${c.id}`"><strong>{{ c.name }}</strong></router-link>
      </li>
    </ul>

    <p v-if="!loading && !error && chalets.length === 0">Aucun chalet pour l’instant.</p>
  </section>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
onMounted(() => { store.dispatch('fetchChalets') })

const chalets = computed(() => store.getters.allChalets)
const loading = computed(() => store.getters.isLoading)
const error = computed(() => store.getters.error)
</script>
