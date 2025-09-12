import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Chalet from '../views/Chalet.vue'

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/chalet/:id', name: 'Chalet', component: Chalet },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
