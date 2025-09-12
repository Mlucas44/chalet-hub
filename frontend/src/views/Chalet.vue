<template>
  <section v-if="chalet" class="container">
    <h1>{{ chalet.name }}</h1>
    <p>{{ chalet.location }} — {{ chalet.pricePerNight }} $/night</p>

    <hr />

    <h2>Book this chalet</h2>
    <form @submit.prevent="reserve" class="form">
      <label>
        Start date
        <input type="date" v-model="startDate" :min="today" required />
      </label>
      <label>
        End date
        <input type="date" v-model="endDate" :min="startDate || today" required />
      </label>

      <button :disabled="loading">
        {{ loading ? 'Booking…' : 'Reserve' }}
      </button>
    </form>

    <p v-if="error" style="color:#c00; margin-top:8px">{{ error }}</p>

    <div v-if="result" class="box">
      <h3>Price breakdown</h3>
      <ul>
        <li> Nights: <strong>{{ result.nights }}</strong></li>
        <li> Subtotal: <strong>{{ money(result.subtotal) }}</strong></li>
        <li> TPS (5%): <strong>{{ money(result.tps) }}</strong></li>
        <li> TVQ (9.975%): <strong>{{ money(result.tvq) }}</strong></li>
        <li> Total: <strong>{{ money(result.total) }}</strong></li>
      </ul>
      <button @click="clearResult">Close</button>
    </div>

    <div class="box" v-if="bookings.length">
      <h3>Existing bookings (read-only)</h3>
      <ul>
        <li v-for="b in bookings" :key="b.id">
          {{ b.startDate }} → {{ b.endDate }} ({{ b.nights }} nights)
          <button @click="cancel(b.id)" :disabled="loading">Cancel</button>
        </li>
      </ul>
    </div>
  </section>

  <p v-else>Loading…</p>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const chalet = ref(null)
const bookings = ref([])
const startDate = ref('')
const endDate = ref('')
const result = ref(null)
const error = ref(null)
const loading = ref(false)

const today = computed(() => new Date().toISOString().slice(0,10))

function money(x) {
  // Backend renvoie BigDecimal -> string/number. On normalise.
  const n = typeof x === 'number' ? x : parseFloat(x)
  return `${n.toFixed(2)} $`
}

function clearResult(){ result.value = null }

async function loadChalet() {
  const { id } = route.params
  chalet.value = (await api.get(`/api/chalets/${id}`)).data
}

async function loadBookings() {
  const { id } = route.params
  // endpoint read-only côté backend (optionnel mais utile pour voir les réservations)
  try {
    const res = await api.get(`/api/bookings/chalet/${id}`)
    bookings.value = res.data || []
  } catch { bookings.value = [] }
}

async function reserve() {
  error.value = null
  result.value = null
  loading.value = true
  try {
    const { id } = route.params
    // Envoie en ISO (yyyy-mm-dd) — c’est ce que veulent tes contrôleurs Spring (LocalDate)
    const res = await api.post('/api/bookings', {
      chaletId: Number(id),
      startDate: startDate.value,
      endDate: endDate.value,
    })
    result.value = res.data
    await loadBookings() // refresh la liste “existing bookings”
  } catch (e) {
    // 400: dates invalides ; 409: conflit ; autres: message générique
    error.value = e?.response?.data || e?.message || 'Error'
  } finally {
    loading.value = false
  }
}

async function cancel(id) {
  loading.value = true
  error.value = null
  try {
    await api.delete(`/api/bookings/${id}`)
    await loadBookings()
  } catch (e) {
    error.value = e?.response?.data || e?.message
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadChalet()
  await loadBookings()
})
</script>

<style>
.container { max-width: 720px; margin: 24px auto; padding: 0 12px; }
.form { display: grid; grid-template-columns: 1fr 1fr auto; gap: 12px; align-items: end; }
.form label { display: grid; gap: 6px; font-size: 14px; }
.box { border: 1px solid #ddd; border-radius: 8px; padding: 12px; margin-top: 16px; }
button { padding: 8px 12px; border-radius: 6px; border: 1px solid #ccc; cursor: pointer; }
button:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
