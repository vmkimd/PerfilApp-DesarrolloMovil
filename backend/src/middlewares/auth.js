// Verifica el token JWT enviado en el encabezado Authorization: Bearer <token>
const jwt = require('jsonwebtoken');
const { JWT_SECRETO } = require('../config/config');

module.exports = (req, res, next) => {
  const encabezado = req.headers.authorization || '';
  const token = encabezado.startsWith('Bearer ') ? encabezado.substring(7) : null;

  if (!token) {
    return res.status(401).json({ ok: false, mensaje: 'Token no enviado' });
  }

  try {
    const datos = jwt.verify(token, JWT_SECRETO);
    req.usuarioId = datos.id;
    next();
  } catch (error) {
    return res.status(401).json({ ok: false, mensaje: 'Token inválido o vencido' });
  }
};
