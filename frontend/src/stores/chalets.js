// src/store/index.js
import { createStore } from 'vuex'
import api from '../api'

export default createStore({
  state: {
    chalets: [],
    loading: false,
    error: null,
    totalPages: 1,
  },
  mutations: {
    setChalets(state, chalets) { state.chalets = chalets || [] },
    setLoading(state, v) { state.loading = v },
    setError(state, e) { state.error = e },
    setTotalPages(state, n) { state.totalPages = n || 1 },
  },
  actions: {
    /**
     * Charge la liste des chalets via l'endpoint PAGINÉ /api/chalets/search
     * avec des valeurs par défaut → ainsi, la page affiche quelque chose dès l’arrivée.
     */
    async fetchChalets({ commit }, params = {}) {
      const {
        page = 0, size = 6, sort = 'id,desc',
        q = null,
        minPrice = null, maxPrice = null,
      } = params

      commit('setLoading', true); commit('setError', null)
      try {
        const query = { page, size, sort }
        if (q) query.q = q                     
        if (minPrice != null && minPrice !== '') query.minPrice = minPrice
        if (maxPrice != null && maxPrice !== '') query.maxPrice = maxPrice

        // 1) on essaie l'endpoint paginé
        const { data } = await api.get('/api/chalets/search', { params: query })
        commit('setChalets', data.content)
        commit('setTotalPages', data.totalPages)
      } catch (e) {
        // 2) fallback sur l'endpoint simple
        try {
          const { data } = await api.get('/api/chalets')
          commit('setChalets', data)
          commit('setTotalPages', 1)
        } catch (e2) {
          commit('setError', e2?.response?.data || e2.message || 'Loading error')
          commit('setChalets', [])
          commit('setTotalPages', 1)
        }
      } finally {
        commit('setLoading', false)
      }
    },

    /**
     * Option "mode simple" si tu veux revenir à l’endpoint non paginé.
     * Non utilisé par défaut, mais dispo si besoin.
     */
    async fetchChaletsRaw({ commit }) {
      commit('setLoading', true); commit('setError', null)
      try {
        const { data } = await api.get('/api/chalets')
        commit('setChalets', data)
        commit('setTotalPages', 1)
      } catch (e) {
        commit('setError', e?.response?.data || e.message)
        commit('setChalets', [])
      } finally {
        commit('setLoading', false)
      }
    },
  },
  getters: {
    allChalets: s => s.chalets,
    isLoading: s => s.loading,
    error: s => s.error,
    totalPages: s => s.totalPages,
  },
})
