import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from "axios";

import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/quill.css'
import {createPinia} from "pinia";

axios.defaults.baseURL = 'http://localhost:8080'

const app = createApp(App)
app.use(createPinia())
app.use(router)

app.mount('#app')
