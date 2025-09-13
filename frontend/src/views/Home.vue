<template>
  <section>
    <h1>Chalets</h1>

    <!-- Filtres -->
    <form @submit.prevent="applyFilters" style="display:grid; gap:8px; max-width:520px; margin:12px 0">
      <div style="display:flex; gap:8px">
        <input v-model.trim="q" type="text" placeholder="Rechercher (nom ou lieu)..." />
        <input v-model.number="minPrice" type="number" min="0" placeholder="Prix min" />
        <input v-model.number="maxPrice" type="number" min="0" placeholder="Prix max" />
      </div>

      <div style="display:flex; gap:8px; align-items:center">
        <label>
          Taille page&nbsp;
          <select v-model.number="size">
            <option :value="3">3</option>
            <option :value="6">6</option>
            <option :value="10">10</option>
          </select>
        </label>

        <label>
          Tri&nbsp;
          <select v-model="sort">
            <option value="id,desc">Récents</option>
            <option value="id,asc">Anciens</option>
            <option value="pricePerNight,asc">Prix ↑</option>
            <option value="pricePerNight,desc">Prix ↓</option>
          </select>
        </label>

        <button type="submit">Filtrer</button>
        <button type="button" @click="resetFilters">Réinitialiser</button>
      </div>
    </form>

    <!-- État -->
    <p v-if="loading">Chargement…</p>
    <p v-if="error" style="color:red">{{ error }}</p>

    <!-- Liste -->
    <ul v-if="!loading && !error">
      <li v-for="c in chalets" :key="c.id">
        <router-link :to="`/chalet/${c.id}`"><strong>{{ c.name }}</strong></router-link>
        — {{ c.location }} — {{ c.pricePerNight }} $/nuit
      </li>
    </ul>

    <p v-if="!loading && !error && chalets.length === 0">Aucun chalet.</p>

    <!-- Pagination -->
    <div v-if="!loading && !error && totalPages > 1" style="margin-top:10px; display:flex; gap:8px; align-items:center">
      <button @click="prev" :disabled="page === 0">‹ Précédent</button>
      <span>Page {{ page + 1 }} / {{ totalPages }}</span>
      <button @click="next" :disabled="page >= totalPages - 1">Suivant ›</button>
    </div>
  </section>
</template>

<script setup>
import { onMounted, computed, ref, watch } from 'vue'
import { useStore } from 'vuex'
const store = useStore()

// état local pour filtres & pagination
const page = ref(0)
const size = ref(6)
const sort = ref('id,desc')
const q = ref('')
const minPrice = ref(null)
const maxPrice = ref(null)

// getters du store
const chalets = computed(() => store.getters.allChalets)
const loading = computed(() => store.getters.isLoading)
const error = computed(() => store.getters.error)
const totalPages = computed(() => store.getters.totalPages)

// charge la 1ère page au mount
onMounted(() => fetchList())

function fetchList() {
  store.dispatch('fetchChalets', {
    page: page.value,
    size: size.value,
    sort: sort.value,
    q: q.value || null,
    minPrice: minPrice.value ?? null,
    maxPrice: maxPrice.value ?? null,
  })
}

// pagination
function prev() { if (page.value > 0) { page.value--; fetchList() } }
function next() { if (page.value < totalPages.value - 1) { page.value++; fetchList() } }

// filtres
function applyFilters() {
  page.value = 0 // revient en page 1
  fetchList()
}
function resetFilters() {
  q.value = ''
  minPrice.value = null
  maxPrice.value = null
  sort.value = 'id,desc'
  size.value = 6
  page.value = 0
  fetchList()
}

// si la taille de page change, on repart en page 1
watch(size, () => { page.value = 0; fetchList() })
watch(sort, () => { page.value = 0; fetchList() })
</script>
