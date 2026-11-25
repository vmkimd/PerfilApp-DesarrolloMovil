// Configuración general del servidor
module.exports = {
  PUERTO: process.env.PUERTO || 3000,
  JWT_SECRETO: process.env.JWT_SECRETO || 'perfilapp_secreto_udv_2026',
  JWT_EXPIRA: '8h',
  RUTA_BD: process.env.RUTA_BD || 'perfil.db'
};
