import { createStore } from 'vuex'
import axios from 'axios'

export default createStore({
  state: {
    chalets: [],
    loading: false,
    error: null,
  },
  mutations: {
    setChalets(state, chalets) { state.chalets = chalets },
    setLoading(state, v) { state.loading = v },
    setError(state, e) { state.error = e },
  },
  actions: {
    async fetchChalets({ commit }) {
      commit('setLoading', true)
      commit('setError', null)
      try {
        const res = await axios.get('http://localhost:8080/api/chalets')
        commit('setChalets', res.data)
      } catch (e) {
        commit('setError', e?.message || 'Erreur de chargement')
      } finally {
        commit('setLoading', false)
      }
    },
  },
  getters: {
    allChalets: (s) => s.chalets,
    isLoading: (s) => s.loading,
    error: (s) => s.error,
  },
})
