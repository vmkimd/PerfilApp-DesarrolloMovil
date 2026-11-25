// Punto de entrada del backend
const express = require('express');
const cors = require('cors');
const { PUERTO } = require('./config/config');
require('./config/database');

const authRoutes = require('./routes/authRoutes');
const perfilRoutes = require('./routes/perfilRoutes');

const app = express();
app.use(cors());
app.use(express.json());

app.get('/', (req, res) => res.json({ ok: true, mensaje: 'API PerfilApp funcionando' }));
app.use('/api/auth', authRoutes);
app.use('/api/perfil', perfilRoutes);

// Manejo general de errores (por ejemplo, archivo que no es imagen o muy grande)
app.use((err, req, res, next) => {
  console.error(err.message);
  res.status(400).json({ ok: false, mensaje: err.message || 'Error en la solicitud' });
});

app.listen(PUERTO, '0.0.0.0', () => {
  console.log(`Servidor PerfilApp escuchando en el puerto ${PUERTO}`);
});
